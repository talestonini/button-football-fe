package com.talestonini.buttonfootball.model

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.circe.CirceEntityCodec._

object Rankings:

  case class Ranking(id: Id, championshipType: String, team: String, teamLogoImgFile: String, numBestPos: Int,
                     numWorstPos: Int, numAvgPos: Int, numParticipations: Int, numRankingPoints: Int,
                     numRankingPos: Int, numPoints: Int, numMatches: Int, numWins: Int, numDraws: Int, numLosses: Int,
                     numGoalsScored: Int, numGoalsConceded: Int, numGoalsDiff: Int, numChampionships: Int,
                     numUpToEdition: Int) extends Model

  implicit val rankingDecoder: EntityDecoder[IO, Ranking] = jsonOf[IO, Ranking]
  implicit val rankingEncoder: EntityEncoder[IO, Ranking] = jsonEncoderOf[IO, Ranking]
  implicit val rankingsDecoder: EntityDecoder[IO, List[Ranking]] = jsonOf[IO, List[Ranking]]
  implicit val rankingsEncoder: EntityEncoder[IO, List[Ranking]] = jsonEncoderOf[IO, List[Ranking]]

end Rankings