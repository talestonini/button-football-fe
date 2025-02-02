package com.talestonini.buttonfootball.model

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import org.http4s.circe.CirceEntityCodec._

object TeamTypes:

  case class TeamType(id: Id, code: Code, description: String) extends Model

  val NO_TEAM_TYPE = TeamType(-1, "-", "-")

  implicit val teamTypeDecoder: EntityDecoder[IO, TeamType] = jsonOf[IO, TeamType]
  implicit val teamTypeEncoder: EntityEncoder[IO, TeamType] = jsonEncoderOf[IO, TeamType]
  implicit val teamTypesDecoder: EntityDecoder[IO, List[TeamType]] = jsonOf[IO, List[TeamType]]
  implicit val teamTypesEncoder: EntityEncoder[IO, List[TeamType]] = jsonEncoderOf[IO, List[TeamType]]

end TeamTypes