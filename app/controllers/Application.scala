package controllers

import play.api._
import play.api.mvc._
import models._
import play.api.data.Forms._
import play.api.data.Form
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.Play.current

object Application extends Controller with Secured {
  val imageForm = Form(
    mapping(
      "id"   -> optional(number),
      "name" -> nonEmptyText,
      "filename" -> nonEmptyText,
      "description" -> nonEmptyText
    )(Image.apply)(Image.unapply)
  )

  def index = DBAction { implicit rs =>
    val imagesNum = Images.count
    val images = Images.findAll().list
    Ok(views.html.index(imagesNum, images))
  }

  def show(id: Int) = DBAction { implicit rs =>
    Images.findById(id).map { image =>
      Ok(views.html.show(image))
    }.getOrElse(NotFound(views.html.error("Cannot Find Image")))
  }

  def edit(id :Int) = IsAuthenticated { implicit rs =>
    username =>
      DB.withSession {
        implicit rs =>
          Images.findById(id).map {
            image =>
              Ok(views.html.edit(id, imageForm.fill(image)))
          }.getOrElse(NotFound(views.html.error("Cannot Find Image")))
      }
  }

  def update(id: Int) = DBAction(parse.multipartFormData) { implicit request =>
    request.body.file("picture").map { picture =>
      import java.io.File
        val contentType = picture.contentType
        val imageFile = Images.findById(id)
        DB.withSession { implicit session =>
          val image = imageForm.bindFromRequest
          if (image.hasErrors) Redirect(routes.Application.select)
          else contentType match {
            case Some("image/jpeg") | Some("image/png") | Some("image/gif") => picture.ref.moveTo(new File(s"public/picture/${image.get.filename}"), true)
              Images.update(id, image.get)
              if (image.get.filename != imageFile.get.filename) {
                new File(s"public/picture/${imageFile.get.filename}").delete
              }
              Redirect(routes.Application.index)
            case Some(s) => Ok(views.html.error(s"Invalid file format ${s}"))
            case None => Ok(views.html.error("Invalid file format"))
          }
        }
      }.getOrElse {
        Redirect(routes.Application.index).flashing(
          "error" -> "Missing file")
      }
  }

  def delete(id: Int) = IsAuthenticated { implicit rs =>
    username =>
    DB.withSession {
      implicit rs =>
        import java.io.File
        val image = Images.findById(id)
        new File(s"public/picture/${image.get.filename}").delete
        Images.delete(id)
        Redirect(routes.Application.index)
    }
  }

  def select  = IsAuthenticated {
     username =>
       implicit request =>
         val image = imageForm.bindFromRequest
         Ok(views.html.upload(image))
     }

  def list = DBAction { implicit rs =>
    val images = Images.findAll().list
    Ok(views.html.gallery(images))
  }

  def upload = DBAction(parse.multipartFormData){ implicit request =>
    request.body.file("picture").map { picture =>
      import java.io.File
      val filename = picture.filename
      val contentType = picture.contentType
      DB.withSession { implicit session =>
        val image = imageForm.bindFromRequest
        if (image.hasErrors) Redirect(routes.Application.select)
        else contentType match {
          case Some("image/jpeg") | Some("image/png") | Some("image/gif") => picture.ref.moveTo(new File(s"public/picture/${image.get.filename}"))
            Images.insert(image.get)
            Ok(views.html.done(s"${image.get.filename} uploaded"))
          case Some(s) => Ok(views.html.error(s"Invalid file format ${s}"))
          case None => Ok(views.html.error("Invalid file format"))
        }
      }
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
        "error" -> "Missing file")
    }
  }
}
