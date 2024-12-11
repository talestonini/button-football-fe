package com.talestonini.buttonfootball.service

import org.http4s.{Uri, UriTemplate}
import org.http4s.UriTemplate.PathElm

trait CommonService:

  private val ButtonFootballApiHost = "localhost:8080"

  def toButtonFootballApiUri(path: String): Uri =
    UriTemplate(
      authority = Some(Uri.Authority(host = Uri.RegName(ButtonFootballApiHost))),
      scheme = Some(Uri.Scheme.http),
      path = List(PathElm(path))
    ).toUriIfPossible.getOrElse(throw RuntimeException("unable to build URI"))

end CommonService