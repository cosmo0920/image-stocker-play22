// app/models/user.scala
package models

import play.api.db.slick.{Profile, Session}
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted

case class User(id: Option[Int], name: String, email: String, password: String)

class Users(tag: Tag) extends Table[User](tag, "users") {
  def id       = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name     = column[String]("name", O.NotNull)
  def email    = column[String]("email", O.NotNull)
  def password = column[String]("password", O.NotNull)
  def * = (id.?, name, email, password) <> (User.tupled, User.unapply _)
}

object Users extends DAO {
  def findById(id: Int)(implicit s: Session): Option[User] = {
    Users filter {_.id === id } firstOption
  }

  def findByName(name: String)(implicit s: Session): Option[User] = {
    Users filter { _.name === name } firstOption
  }

  def findByEmail(email: String)(implicit s: Session): Option[User] = {
    Users filter { _.email === email } firstOption
  }

  def insert(user: User)(implicit s: Session) {
    Users += user
  }

  def count(implicit s: Session): Int =
    Query(Users.length).first

  def findAll(filter: String = "%")(implicit s: Session): lifted.Query[Users, User] = {
    for {
      u <- Users
      if (u.name like ("%" + filter))
    } yield u
  }

  def update(id: Int, user: User)(implicit s: Session) {
    val userToUpdate: User = user.copy(Some(id))
    Users.where(_.id === id).update(userToUpdate)
  }

  def delete(id: Int)(implicit s: Session) {
    Users.where(_.id === id).delete
  }
}
