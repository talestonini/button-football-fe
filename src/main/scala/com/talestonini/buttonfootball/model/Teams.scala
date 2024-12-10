package com.talestonini.buttonfootball.model

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import org.http4s.circe.CirceEntityCodec._

object Teams {
  case class Team(id: Int, name: String, `type`: String, fullName: String, foundation: String, city: String,
                  country: String, logoImgFile: String) extends Model

  implicit val teamDecoder: EntityDecoder[IO, Team] = jsonOf[IO, Team]
  implicit val teamEncoder: EntityEncoder[IO, Team] = jsonEncoderOf[IO, Team]
  implicit val teamsDecoder: EntityDecoder[IO, List[Team]] = jsonOf[IO, List[Team]]
  implicit val teamsEncoder: EntityEncoder[IO, List[Team]] = jsonEncoderOf[IO, List[Team]]
}