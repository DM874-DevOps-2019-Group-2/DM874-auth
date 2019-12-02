package auth.services

import auth.Main.config
import pdi.jwt.{Jwt, JwtAlgorithm}

import scala.util.Try

class JWTService(
                  config: com.typesafe.config.Config
                ) {
  val sc = config.getString("jwtSecret")
  val algo = "HS256"

  def createToken(payload: String): String = Jwt.encode(payload, sc, JwtAlgorithm.HS256)

  def tryDecode(jwtToken: String): Try[String] = Jwt.decodeRaw(jwtToken, sc, Seq(JwtAlgorithm.HS256))
}
