package com.talestonini.buttonfootball.service

import cats.effect.IO
import com.talestonini.buttonfootball.exception.InvalidNumberOfTeams
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Championships.*
import com.talestonini.buttonfootball.model.Matches.*
import com.talestonini.buttonfootball.model.Standings.*
import org.http4s.dom.FetchClientBuilder
import org.http4s.{Header, Method, Request}
import org.typelevel.ci.CIString

object ChampionshipService extends CommonService:

  // NOTE: As a rule, let's make all calculations in this app as a function of the number of teams in a championship.

  def isValidNumberOfTeamsInChampionship(numTeams: Int): Either[Exception, Boolean] =
    if (numTeams <= 0)
      Left(IllegalArgumentException("input must be a positive integer"))
    else
      Right(numTeams % NUM_TEAMS_PER_GROUP == 0)

  def calcNumGroups(numTeams: Int): Either[Exception, Int] =
    isValidNumberOfTeamsInChampionship(numTeams) match {
      case Left(e) => Left(InvalidNumberOfTeams(numTeams))
      case Right(isValid) => Right(numTeams/NUM_TEAMS_PER_GROUP)
    }

  /**
    * The closest to half of the number of teams, so that not all teams in a group are able to qualify to the finals.
    * We don't want all the teams in a group qualifying to the finals.
    *
    * @param numTeams the number of teams
    * @return
    */
  def calcNumQualif(numTeams: Int): Either[Exception, Int] =
    def closestPowerOfTwoToHalfOf(n: Int): Int = {
      val half = n / 2.0
      var power = 1

      while (power * 2 <= half) {
        power *= 2
      }

      val lowerPower = power
      val higherPower = power * 2

      if ((half - lowerPower) < (higherPower - half)) lowerPower else higherPower
    }

    calcNumGroups(numTeams) match {
      case Left(e) => Left(e)
      case Right(ng) => Right(closestPowerOfTwoToHalfOf(numTeams))
    }
  end calcNumQualif

  def getChampionships(codChampionshipType: Option[Code]): IO[List[Championship]] =
    val uri = toButtonFootballApiUri("championships").withOptionQueryParam("codChampionshipType", codChampionshipType)
    val request = Request[IO](Method.GET, uri).withHeaders(Header.Raw(CIString("Content-Type"), "application/json"))
    FetchClientBuilder[IO].create
      .expectOr[List[Championship]](request)(errorResponse =>
        IO(RuntimeException(s"failed getting championships: $errorResponse")))
  end getChampionships

  def getMatches(championshipId: Id): IO[List[Match]] =
    val uri = toButtonFootballApiUri("championships")/(championshipId)/("matches")
    val request = Request[IO](Method.GET, uri)
    FetchClientBuilder[IO].create
      .expectOr[List[Match]](request)(errorResponse =>
        IO(RuntimeException(s"failed getting matches: $errorResponse")))
  end getMatches

  def getStandings(championshipId: Id): IO[List[Standing]] =
    val uri = toButtonFootballApiUri("championships")/(championshipId)/("standings")
    val request = Request[IO](Method.GET, uri)
    FetchClientBuilder[IO].create
      .expectOr[List[Standing]](request)(errorResponse =>
        IO(RuntimeException(s"failed getting standings: $errorResponse")))
  end getStandings

  def getGroupStandings(championshipId: Id): IO[List[Standing]] =
    val uri = toButtonFootballApiUri("championships")/(championshipId)/("groupStandings")
    val request = Request[IO](Method.GET, uri)
    FetchClientBuilder[IO].create
      .expectOr[List[Standing]](request)(errorResponse =>
        IO(RuntimeException(s"failed getting group standings: $errorResponse")))
  end getGroupStandings

  def getFinalStandings(championshipId: Id): IO[List[Standing]] =
    val uri = toButtonFootballApiUri("championships")/(championshipId)/("finalStandings")
    val request = Request[IO](Method.GET, uri)
    FetchClientBuilder[IO].create
      .expectOr[List[Standing]](request)(errorResponse =>
        IO(RuntimeException(s"failed getting final standings: $errorResponse")))
  end getFinalStandings

end ChampionshipService