import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/noexistent")) must beNone
    }

    "render the login page" in new WithApplication{
      val login = route(FakeRequest(GET, "/login")).get
      status(login) must equalTo(OK)
    }

    "render the logout page" in new WithApplication {
      val logout = route(FakeRequest(GET, "/logout")).get
      status(logout) must equalTo(SEE_OTHER)
    }

    "render the upload page without authentication" in new WithApplication {
      val upload = route(FakeRequest(GET, "/img/upload")).get
      status(upload) must equalTo(BAD_REQUEST)
    }

    "render the select page without authentication" in new WithApplication {
      val select = route(FakeRequest(GET, "/img/select")).get
      status(select) must equalTo(SEE_OTHER)
    }

    "render the img/:id without img upload" in new WithApplication() {
      val img = route(FakeRequest(GET, "/img/100")).get
      status(img) must equalTo(NOT_FOUND)
    }

    "render the gallery page" in new WithApplication{
      val gallery = route(FakeRequest(GET, "/img/list")).get
      status(gallery) must equalTo(OK)
    }

    "render the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("image stocker")
    }
  }
}
