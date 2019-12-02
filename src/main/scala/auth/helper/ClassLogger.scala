package auth.helper

import com.typesafe.scalalogging.Logger

trait ClassLogger {
  val logger = Logger(this.getClass)
}