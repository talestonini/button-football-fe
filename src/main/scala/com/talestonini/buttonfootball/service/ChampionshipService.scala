package com.talestonini.buttonfootball.service

import cats.effect.IO
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Championships.*
import com.talestonini.buttonfootball.model.ChampionshipTypes.*
import com.talestonini.buttonfootball.model.Matches.*
import org.http4s.dom.FetchClientBuilder
import org.http4s.FormDataDecoder.formEntityDecoder
import org.http4s.{Header, Method, Request}
import org.typelevel.ci.CIString

object ChampionshipService extends CommonService:

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

end ChampionshipService