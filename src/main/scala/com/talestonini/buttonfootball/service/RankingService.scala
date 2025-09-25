package com.talestonini.buttonfootball.service

import cats.effect.IO
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Rankings.*
import org.http4s.dom.FetchClientBuilder
import org.http4s.{Header, Method, Request}
import org.typelevel.ci.CIString

object RankingService extends CommonService:

  def getRankings(codChampionshipType: Code, numUpToEdition: Int): IO[List[Ranking]] =
    val uri = toButtonFootballApiUri("rankings").withQueryParam("codChampionshipType", codChampionshipType)
      .withQueryParam("numUpToEdition", numUpToEdition)
    val request = Request[IO](Method.GET, uri).withHeaders(Header.Raw(CIString("Content-Type"), "application/json"))
    FetchClientBuilder[IO].create
      .expectOr[List[Ranking]](request)(errorResponse =>
        IO(RuntimeException(s"failed getting rankings: $errorResponse")))
  end getRankings

end RankingService