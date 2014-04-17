// app/models/image.scala
package models

import play.api.db.slick.{Profile, Session}
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted

case class Image(id: Option[Int], name: String, filename: String, description: String)

class Images(tag: Tag) extends Table[Image](tag, "images") {
  def id          = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name        = column[String]("name", O.NotNull)
  def filename    = column[String]("filename", O.NotNull)
  def description = column[String]("description", O.NotNull)
  def * = (id.?, name, filename, description) <> (Image.tupled, Image.unapply _)
}

object Images extends DAO {
  def findById(id: Int)(implicit s: Session): Option[Image] = {
    Images filter {_.id === id } firstOption
  }

  def findByName(name: String)(implicit s: Session): Option[Image] = {
    Images filter { _.name === name } firstOption
  }

  def insert(image: Image)(implicit s: Session) {
    Images += image
  }

  def count(implicit s: Session): Int =
    Query(Images.length).first

  def findAll(filter: String = "%")(implicit s: Session): lifted.Query[Images, Image] = {
    for {
      u <- Images
      if (u.name like ("%" + filter))
    } yield u
  }

  def update(id: Int, image: Image)(implicit s: Session) {
    val imageToUpdate: Image = image.copy(Some(id))
    Images.where(_.id === id).update(imageToUpdate)
  }

  def delete(id: Int)(implicit s: Session) {
    Images.where(_.id === id).delete
  }
}
