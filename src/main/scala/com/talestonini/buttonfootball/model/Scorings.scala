package com.talestonini.buttonfootball.model

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.circe.CirceEntityCodec._

object Scorings:

  case class Scoring(championshipType: String, numPos: Int, numPoints: Int) extends Model

  implicit val scoringDecoder: EntityDecoder[IO, Scoring] = jsonOf[IO, Scoring]
  implicit val scoringEncoder: EntityEncoder[IO, Scoring] = jsonEncoderOf[IO, Scoring]
  implicit val scoringsDecoder: EntityDecoder[IO, List[Scoring]] = jsonOf[IO, List[Scoring]]
  implicit val scoringsEncoder: EntityEncoder[IO, List[Scoring]] = jsonEncoderOf[IO, List[Scoring]]

end Scorings