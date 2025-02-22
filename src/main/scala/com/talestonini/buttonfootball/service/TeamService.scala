package com.talestonini.buttonfootball.service

import cats.effect.IO
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Teams.*
import org.http4s.dom.FetchClientBuilder
import org.http4s.{Header, Method, Request}
import org.typelevel.ci.CIString

object TeamService extends CommonService:

  def getTeams(name: Option[String]): IO[List[Team]] =
    val uri = toButtonFootballApiUri("teams").withOptionQueryParam("name", name)
    val request = Request[IO](Method.GET, uri).withHeaders(Header.Raw(CIString("Content-Type"), "application/json"))
    FetchClientBuilder[IO].create
      .expectOr[List[Team]](request)(errorResponse =>
        IO(RuntimeException(s"failed getting teams: $errorResponse")))
  end getTeams

end TeamService