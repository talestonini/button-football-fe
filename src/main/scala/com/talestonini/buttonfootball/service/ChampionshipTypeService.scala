package com.talestonini.buttonfootball.service

import cats.effect.IO
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Championships.*
import com.talestonini.buttonfootball.model.ChampionshipTypes.*
import org.http4s.dom.FetchClientBuilder
import org.http4s.FormDataDecoder.formEntityDecoder
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

  def getChampionships(championshipTypeId: Int): IO[List[Championship]] =
    val uri = toButtonFootballApiUri("championshipTypes")./(championshipTypeId)
    val request = Request[IO](Method.GET, uri).withHeaders(Header.Raw(CIString("Content-Type"), "application/json"))
    FetchClientBuilder[IO].create
      .expectOr[List[Championship]](request)(errorResponse =>
        IO(RuntimeException(s"failed getting championships: $errorResponse")))
  end getChampionships

end ChampionshipTypeService