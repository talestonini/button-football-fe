package com.talestonini.buttonfootball.model

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.circe.CirceEntityCodec._

object Championships:

  case class Championship(id: Id, `type`: String, teamType: String, numEdition: Int, dtCreation: String,
                          dtEnd: Option[String], numTeams: Int, numQualif: Int, status: String) extends Model

  val NO_CHAMPIONSHIP: Championship = Championship(-1, "-", "-", NO_CHAMPIONSHIP_EDITION, "-", None, -1, -1, "-")

  implicit val championshipDecoder: EntityDecoder[IO, Championship] = jsonOf[IO, Championship]
  implicit val championshipEncoder: EntityEncoder[IO, Championship] = jsonEncoderOf[IO, Championship]
  implicit val championshipsDecoder: EntityDecoder[IO, List[Championship]] = jsonOf[IO, List[Championship]]
  implicit val championshipsEncoder: EntityEncoder[IO, List[Championship]] = jsonEncoderOf[IO, List[Championship]]

end Championships