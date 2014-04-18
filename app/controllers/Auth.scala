package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models._
import play.api.db.slick._
import play.api.Play.current

trait Secured {

  def username(request: RequestHeader) = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.login)

  def withAuth(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }

  def IsAuthenticated(f: => String => Request[AnyContent] => Result) =
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
  }

  def withUser(f: User => Request[AnyContent] => Result) = withAuth {
    username => implicit request =>
      DB.withSession { implicit session =>
        Users.findByName(username).map { user =>
          f(user)(request)
        }.getOrElse(onUnauthorized(request))
      }
  }
}

object Auth extends Controller {

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => check(email, password)
    })
  )

  def check(username: String, password: String) = {
    val adminEmail = Play.current.configuration.getString("admin.email")
    val adminPass = Play.current.configuration.getString("admin.password")
    (username == adminEmail.get && password == adminPass.get)
  }

  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  def signup = Action {
    // TODO
    Ok("Not implemented")
  }

  def register = Action {
    // TODO
    Ok("Not implemented")
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => Redirect(routes.Application.index).withSession(Security.username -> user._1)
    )
  }

  def logout = Action {
    Redirect(routes.Auth.login).withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }
}