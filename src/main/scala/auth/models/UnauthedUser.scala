package auth.models

case class UnauthedUser(
                       username: String,
                       password: String
                       )

object UnauthedUser {
  implicit val enc: io.circe.Encoder[UnauthedUser] = io.circe.generic.semiauto.deriveEncoder[UnauthedUser]
  implicit val dec: io.circe.Decoder[UnauthedUser] = io.circe.generic.semiauto.deriveDecoder[UnauthedUser]
}