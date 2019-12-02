package auth.schema

import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

class Users(tag: Tag) extends Table[(Int, String, String, Boolean)](tag, "users") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def username = column[String]("username")
  def password = column[String]("password")
  def isAdmin = column[Boolean]("is_admin", O.Default(false))
  def * = (id, username, password, isAdmin)
}
