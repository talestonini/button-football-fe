package com.talestonini.buttonfootball.service

import cats.effect.IO
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.TeamTypes.*
import org.http4s.dom.FetchClientBuilder
import org.http4s.{Header, Method, Request}
import org.typelevel.ci.CIString

object TeamTypeService extends CommonService:

  def getTeamTypes(): IO[List[TeamType]] =
    val uri = toButtonFootballApiUri("teamTypes")
    val request = Request[IO](Method.GET, uri).withHeaders(Header.Raw(CIString("Content-Type"), "application/json"))
    FetchClientBuilder[IO].create
      .expectOr[List[TeamType]](request)(errorResponse =>
        IO(RuntimeException(s"failed getting team types: $errorResponse")))
  end getTeamTypes

end TeamTypeService