// app/models/DAO

package models

import scala.slick.lifted.TableQuery

private[models] trait DAO {
  val Images = TableQuery[Images]
}
