package auth.models

case class AuthedUser(
                     id: Int,
                     username: String
                     )
object AuthedUser {
  implicit val enc: io.circe.Encoder[AuthedUser] = io.circe.generic.semiauto.deriveEncoder[AuthedUser]
  implicit val dec: io.circe.Decoder[AuthedUser] = io.circe.generic.semiauto.deriveDecoder[AuthedUser]
}