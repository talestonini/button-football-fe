package com.talestonini.buttonfootball.model

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.circe.CirceEntityCodec._

object ChampionshipTypes:

  case class ChampionshipType(id: Id, code: Code, description: String, logoImgFile: String) extends Model

  val NO_CHAMPIONSHIP_TYPE: ChampionshipType = ChampionshipType(-1, "-", "-", "-")

  implicit val championshipTypeDecoder: EntityDecoder[IO, ChampionshipType] = jsonOf[IO, ChampionshipType]
  implicit val championshipTypeEncoder: EntityEncoder[IO, ChampionshipType] = jsonEncoderOf[IO, ChampionshipType]
  implicit val championshipTypesDecoder: EntityDecoder[IO, List[ChampionshipType]] = jsonOf[IO, List[ChampionshipType]]
  implicit val championshipTypesEncoder: EntityEncoder[IO, List[ChampionshipType]] =
    jsonEncoderOf[IO, List[ChampionshipType]]

end ChampionshipTypes