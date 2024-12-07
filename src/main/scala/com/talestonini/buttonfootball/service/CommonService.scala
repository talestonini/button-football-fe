package com.talestonini.buttonfootball.service

import org.http4s.UriTemplate
import org.http4s.Uri
import org.http4s.UriTemplate.PathElm

trait CommonService {

  private val ButtonFootballApiHost = "localhost:8080"

  def toButtonFootballApiUri(path: String): Uri =
    UriTemplate(
      authority = Some(Uri.Authority(host = Uri.RegName(ButtonFootballApiHost))),
      scheme = Some(Uri.Scheme.http), // TODO: configure http or https
      path = List(PathElm(path))
    ).toUriIfPossible.getOrElse(throw RuntimeException("unable to build URI"))

}