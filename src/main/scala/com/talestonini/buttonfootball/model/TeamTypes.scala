package com.talestonini.buttonfootball.model

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import org.http4s.circe.CirceEntityCodec._

object TeamTypes:

  case class TeamType(id: Int, code: String, description: String) extends Model

  implicit val teamDecoder: EntityDecoder[IO, TeamType] = jsonOf[IO, TeamType]
  implicit val teamEncoder: EntityEncoder[IO, TeamType] = jsonEncoderOf[IO, TeamType]
  implicit val teamsDecoder: EntityDecoder[IO, List[TeamType]] = jsonOf[IO, List[TeamType]]
  implicit val teamsEncoder: EntityEncoder[IO, List[TeamType]] = jsonEncoderOf[IO, List[TeamType]]

end TeamTypes