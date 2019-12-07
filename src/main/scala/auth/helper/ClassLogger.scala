package auth.helper

import com.typesafe.scalalogging.Logger

trait Logged
object Logged extends Logged

trait ClassLogger {
  val logger = Logger(this.getClass)

  def withLogger(block: Logger => Unit) = {
    block(logger)
    Logged
  }
}