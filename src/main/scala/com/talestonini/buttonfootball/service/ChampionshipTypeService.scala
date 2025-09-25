package com.talestonini.buttonfootball.service

import cats.effect.IO
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Championships.*
import com.talestonini.buttonfootball.model.ChampionshipTypes.*
import com.talestonini.buttonfootball.model.Rankings.*
import com.talestonini.buttonfootball.model.Scorings.*
import org.http4s.dom.FetchClientBuilder
import org.http4s.{Header, Method, Request}
import org.typelevel.ci.CIString

object ChampionshipTypeService extends CommonService:

  def getChampionshipTypes(codTeamType: Option[Code]): IO[List[ChampionshipType]] =
    val uri = toButtonFootballApiUri("championshipTypes").withOptionQueryParam("codTeamType", codTeamType)
    val request = Request[IO](Method.GET, uri).withHeaders(Header.Raw(CIString("Content-Type"), "application/json"))
    FetchClientBuilder[IO].create
      .expectOr[List[ChampionshipType]](request)(errorResponse =>
        IO(RuntimeException(s"failed getting team types: $errorResponse")))
  end getChampionshipTypes

  def getChampionships(championshipTypeId: Id): IO[List[Championship]] =
    val uri = toButtonFootballApiUri("championshipTypes")/(championshipTypeId)
    val request = Request[IO](Method.GET, uri).withHeaders(Header.Raw(CIString("Content-Type"), "application/json"))
    FetchClientBuilder[IO].create
      .expectOr[List[Championship]](request)(errorResponse =>
        IO(RuntimeException(s"failed getting championships: $errorResponse")))
  end getChampionships

  def getRankings(championshipTypeId: Id, numUpToEdition: Int): IO[List[Ranking]] =
    val uri = (toButtonFootballApiUri("championshipTypes")/(championshipTypeId)/("rankings"))
      .withQueryParam("numUpToEdition", numUpToEdition)
    val request = Request[IO](Method.GET, uri).withHeaders(Header.Raw(CIString("Content-Type"), "application/json"))
    FetchClientBuilder[IO].create
      .expectOr[List[Ranking]](request)(errorResponse =>
        IO(RuntimeException(s"failed getting rankings: $errorResponse")))
  end getRankings

  def getScorings(championshipTypeId: Id): IO[List[Scoring]] =
    val uri = toButtonFootballApiUri("championshipTypes")/(championshipTypeId)/("scorings")
    val request = Request[IO](Method.GET, uri).withHeaders(Header.Raw(CIString("Content-Type"), "application/json"))
    FetchClientBuilder[IO].create
      .expectOr[List[Scoring]](request)(errorResponse =>
        IO(RuntimeException(s"failed getting rankings: $errorResponse")))
  end getScorings

end ChampionshipTypeService