package com.talestonini.buttonfootball.model

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.circe.CirceEntityCodec._

object Matches:

  case class Match(id: Id, championship: String, numEdition: Int, `type`: String, teamA: String, teamB: String,
                   teamALogoImgFile: String, teamBLogoImgFile: String, numGoalsTeamA: Option[Int],
                   numGoalsTeamB: Option[Int], numGoalsExtraA: Option[Int], numGoalsExtraB: Option[Int],
                   numGoalsPntA: Option[Int], numGoalsPntB: Option[Int]) extends Model {

    def winner(): Option[String] =
      if (s"${numGoalsPntA.getOrElse(0)}${numGoalsExtraA.getOrElse(0)}${numGoalsTeamA.getOrElse(0)}".toInt >
          s"${numGoalsPntB.getOrElse(0)}${numGoalsExtraB.getOrElse(0)}${numGoalsTeamB.getOrElse(0)}".toInt)
        Some(teamA)
      else if (s"${numGoalsPntA.getOrElse(0)}${numGoalsExtraA.getOrElse(0)}${numGoalsTeamA.getOrElse(0)}".toInt <
               s"${numGoalsPntB.getOrElse(0)}${numGoalsExtraB.getOrElse(0)}${numGoalsTeamB.getOrElse(0)}".toInt)
        Some(teamB)
      else
        None

    def loser(): Option[String] =
      if (s"${numGoalsPntA.getOrElse(0)}${numGoalsExtraA.getOrElse(0)}${numGoalsTeamA.getOrElse(0)}".toInt <
          s"${numGoalsPntB.getOrElse(0)}${numGoalsExtraB.getOrElse(0)}${numGoalsTeamB.getOrElse(0)}".toInt)
        Some(teamA)
      else if (s"${numGoalsPntA.getOrElse(0)}${numGoalsExtraA.getOrElse(0)}${numGoalsTeamA.getOrElse(0)}".toInt >
               s"${numGoalsPntB.getOrElse(0)}${numGoalsExtraB.getOrElse(0)}${numGoalsTeamB.getOrElse(0)}".toInt)
        Some(teamB)
      else
        None

  }
  
  implicit val matchDecoder: EntityDecoder[IO, Match] = jsonOf[IO, Match]
  implicit val matchEncoder: EntityEncoder[IO, Match] = jsonEncoderOf[IO, Match]
  implicit val matchesDecoder: EntityDecoder[IO, List[Match]] = jsonOf[IO, List[Match]]
  implicit val matchesEncoder: EntityEncoder[IO, List[Match]] = jsonEncoderOf[IO, List[Match]]

end Matches