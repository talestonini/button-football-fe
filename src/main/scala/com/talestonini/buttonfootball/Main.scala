package com.talestonini.buttonfootball

import cats.effect.unsafe.implicits.global
import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.component.TTTable
import com.talestonini.buttonfootball.component.TTTable.TTHeader
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Teams.*
import com.talestonini.buttonfootball.service.TeamService
import org.scalajs.dom
import scala.scalajs.concurrent.JSExecutionContext.queue
import scala.util.{Failure, Success}

@main
def ButtonFootballFrontEnd(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    div(
      input(
        typ := "text",
        value <-- teamName,
        onInput.mapToValue --> teamName,
        onChange --> (ev => seGetTeams(teamName.now()))
      ),
      TTTable.renderTable(teams, List(
        TTHeader("Nome", 1),
        TTHeader("Tipo", 2),
        TTHeader("Nome Completo", 3),
        TTHeader("Fundação", 4),
        TTHeader("Cidade", 5),
        TTHeader("País", 6)
      ))
    )
  )

def seGetTeams(name: String): Unit =
  TeamService
    .getTeams(if (name.isBlank()) None else Some(name.trim()))
    .unsafeToFuture()
    .onComplete({
      case s: Success[List[Team]] => teams.update(_ => s.value)
      case f: Failure[List[Team]] => println(s"failed fetching team(s): ${f.exception.getMessage()}")
    })(queue)