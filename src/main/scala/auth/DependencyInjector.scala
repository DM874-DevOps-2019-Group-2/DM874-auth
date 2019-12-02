package auth

import auth.services.JWTService

class DependencyInjector(
                        config: com.typesafe.config.Config
                        ) {
  lazy val jwtService = new JWTService(config)
}
