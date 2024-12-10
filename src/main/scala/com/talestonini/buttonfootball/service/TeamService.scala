package com.talestonini.buttonfootball.service

import cats.effect.IO
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Teams.*
import org.http4s.FormDataDecoder.formEntityDecoder
import org.http4s.Request
import org.http4s.Method
import org.http4s.Header
import org.typelevel.ci.CIString
import org.http4s.dom.FetchClientBuilder

object TeamService extends CommonService {
  def getTeams(name: Option[String]): IO[List[Team]] = {
    val uri = toButtonFootballApiUri("teams").withOptionQueryParam("name", name)
    val request = Request[IO](Method.GET, uri).withHeaders(Header.Raw(CIString("Content-Type"), "application/json"))

    FetchClientBuilder[IO].create
      .expectOr[List[Team]](request)(errorResponse =>
        IO(RuntimeException(s"failed getting team: $errorResponse")))
  }
}