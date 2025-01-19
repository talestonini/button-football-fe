package com.talestonini.buttonfootball.model

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import org.http4s.circe.CirceEntityCodec._

object ChampionshipTypes:

  case class ChampionshipType(id: Int, code: String, description: String) extends Model

  val NO_CHAMPIONSHIP_TYPE = ChampionshipType(-1, "-", "-")

  implicit val teamDecoder: EntityDecoder[IO, ChampionshipType] = jsonOf[IO, ChampionshipType]
  implicit val teamEncoder: EntityEncoder[IO, ChampionshipType] = jsonEncoderOf[IO, ChampionshipType]
  implicit val teamsDecoder: EntityDecoder[IO, List[ChampionshipType]] = jsonOf[IO, List[ChampionshipType]]
  implicit val teamsEncoder: EntityEncoder[IO, List[ChampionshipType]] = jsonEncoderOf[IO, List[ChampionshipType]]

end ChampionshipTypes