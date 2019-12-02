package auth.helper


object DatabaseProvider {
  import slick.jdbc.PostgresProfile.api._

  val db = Database.forConfig("db")
}
