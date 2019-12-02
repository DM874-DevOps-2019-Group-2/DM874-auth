package auth

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import auth.models.{AuthedUser, UnauthedUser}
import auth.schema.Users
import slick.lifted.TableQuery

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main extends App with auth.helper.ClassLogger {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val config = com.typesafe.config.ConfigFactory.load()

  val dependencyInjector = new DependencyInjector(config)

  val route =
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
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  //Block
  

  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
