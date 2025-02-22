package com.talestonini.buttonfootball.model

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.circe.CirceEntityCodec._

object Standings:

  case class Standing(id: Id, championship: String, team: String, `type`: String, numIntraGrpPos: Option[Int],
                      numExtraGrpPos: Option[Int], numFinalPos: Option[Int], numPoints: Int, numMatches: Int,
                      numWins: Int, numDraws: Int, numLosses: Int, numGoalsScored: Int, numGoalsConceded: Int,
                      numGoalDiff:Int) extends Model

  implicit val standingDecoder: EntityDecoder[IO, Standing] = jsonOf[IO, Standing]
  implicit val standingEncoder: EntityEncoder[IO, Standing] = jsonEncoderOf[IO, Standing]
  implicit val standingsDecoder: EntityDecoder[IO, List[Standing]] = jsonOf[IO, List[Standing]]
  implicit val standingsEncoder: EntityEncoder[IO, List[Standing]] = jsonEncoderOf[IO, List[Standing]]

end Standings