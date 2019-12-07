package auth

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import auth.models.{AuthedUser, UnauthedUser}
import auth.schema.Users
import slick.lifted.TableQuery

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.io.StdIn
import scala.util.{Failure, Success}

object Main extends App with auth.helper.ClassLogger {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val config = com.typesafe.config.ConfigFactory.load()

  val dependencyInjector = new DependencyInjector(config)

  val route =
    concat(
      path("login") {
        post  {
          entity(as[String]) { body =>
            //Check login body
            import io.circe.parser._
            val parsedBody: Option[UnauthedUser] = decode[UnauthedUser](body).toOption

            //Query database
            val checkedLogin: Future[Option[AuthedUser]] = parsedBody.map{ unauthedUser =>
              import slick.jdbc.PostgresProfile.api._
              import auth.helper.DatabaseProvider.db

              val users = slick.lifted.TableQuery[Users]

              //Query for username
              val q = users.filter(_.username === unauthedUser.username).map(x => (x.password, x.id)).result.headOption.map{
                case None => {
                  logger.error(s"Failed to find user with username ${unauthedUser.username}")
                  None
                }
                case Some((dbPassword, dbId)) => {
                  import com.github.t3hnar.bcrypt._

                  unauthedUser.password.isBcryptedSafe(dbPassword).toOption.flatMap{
                    case true => Some(AuthedUser(dbId, unauthedUser.username))
                    case false => None
                  }
                }
              }

              db.run(q)
            }.getOrElse{
              logger.error(s"Failed to parse user")
              Future.successful(None)
            }
            import io.circe.syntax._

            //Convert the user into a jwt token
            val jwt: Future[Option[String]] = checkedLogin.map(_.map{ authedUser =>
              dependencyInjector.jwtService.createToken(authedUser.asJson.noSpaces)
            })

            //Respond to requester
            onComplete(jwt) {
              case Failure(exception) => {
                logger.error(s"Error: Future failed future ${exception}")
                complete(HttpResponse(status = StatusCodes.InternalServerError))
              }
              case Success(value) => value match {
                case None =>
                  complete(HttpEntity(ContentTypes.`application/json`, (auth.models.RequestResponse.Error("Failed to authenticate") : auth.models.RequestResponse).asJson.noSpaces))
                case Some(value) =>
                  complete(HttpEntity(ContentTypes.`application/json`, (auth.models.RequestResponse.Success(value) : auth.models.RequestResponse).asJson.noSpaces))
              }
            }
          }
        }
      },
      path("register") {
        post {
          withLogger(_.info("hello!"))
          entity(as[String]) { body =>
            //Check login body
            import io.circe.parser._
            import io.circe.syntax._
            val parsedBody: Option[UnauthedUser] = decode[UnauthedUser](body).toOption

            parsedBody match {
              case None => complete(HttpEntity(ContentTypes.`application/json`, (auth.models.RequestResponse.Error("Failed to parse register body") : auth.models.RequestResponse).asJson.noSpaces))
              case Some(unauthedUser) => {
                import slick.jdbc.PostgresProfile.api._
                import auth.helper.DatabaseProvider.db

                val users = slick.lifted.TableQuery[Users]

                val newUserQ = users.filter(_.username === unauthedUser.username).exists.result.flatMap{ doesUsernameExist =>
                  if (doesUsernameExist) {
                    DBIO.successful(Some("Username already exists"))
                  } else {
                    import com.github.t3hnar.bcrypt._

                    val hashedPasword = unauthedUser.password.bcrypt

                    val insQ = users += ((0, unauthedUser.username, hashedPasword, false))

                    insQ.map(_ => None)
                  }
                }

                onComplete(db.run(newUserQ.transactionally)) {
                  case Failure(exception) => {
                    logger.error(s"Error: Future failed future ${exception}")
                    complete(HttpResponse(status = StatusCodes.InternalServerError))
                  }
                  case Success(value) => value match {
                    case Some(errMsg) =>
                      complete(HttpEntity(ContentTypes.`application/json`, (auth.models.RequestResponse.Error(errMsg) : auth.models.RequestResponse).asJson.noSpaces))
                    case None =>
                      complete(HttpEntity(ContentTypes.`application/json`, (auth.models.RequestResponse.Success("") : auth.models.RequestResponse).asJson.noSpaces))
                  }
                }
              }
            }
          }
        }
      }
    )

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8084)

  //Block
  Thread.sleep(Int.MaxValue)

  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
