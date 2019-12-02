package auth.models
import io.circe.Json

sealed trait RequestResponse

object RequestResponse {
  case class Error(
                  error: String
                  ) extends RequestResponse
  case class Success(
                    payload: String
                    ) extends RequestResponse

  implicit val enc: io.circe.Encoder[RequestResponse] = new io.circe.Encoder[RequestResponse]{
    implicit val encSuc: io.circe.ObjectEncoder[Success] = io.circe.generic.semiauto.deriveEncoder[Success]
    implicit val encErr: io.circe.ObjectEncoder[Error] = io.circe.generic.semiauto.deriveEncoder[Error]

    import io.circe.syntax._

    override def apply(a: RequestResponse): Json = a match {
      case e: Error => e.asJsonObject.add("$type", "Error".asJson).asJson
      case s: Success => s.asJsonObject.add("$type", "Success".asJson).asJson
    }
  }
}