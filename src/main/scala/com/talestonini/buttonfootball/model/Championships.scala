package com.talestonini.buttonfootball.model

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import org.http4s.circe.CirceEntityCodec._

object Championships:

  val NO_CHAMPIONSHIP_EDITION = 0
  val MIN_CHAMPIONSHIP_EDITION = 1

  case class Championship(id: Int, `type`: String, teamType: String, numEdition: Int, dtCreation: String,
                          dtEnd: Option[String], numTeams: Int, numQualif: Int, status: String) extends Model

  val NO_CHAMPIONSHIP = Championship(-1, "-", "-", NO_CHAMPIONSHIP_EDITION, "-", None, -1, -1, "-")

  implicit val teamDecoder: EntityDecoder[IO, Championship] = jsonOf[IO, Championship]
  implicit val teamEncoder: EntityEncoder[IO, Championship] = jsonEncoderOf[IO, Championship]
  implicit val teamsDecoder: EntityDecoder[IO, List[Championship]] = jsonOf[IO, List[Championship]]
  implicit val teamsEncoder: EntityEncoder[IO, List[Championship]] = jsonEncoderOf[IO, List[Championship]]

end Championships