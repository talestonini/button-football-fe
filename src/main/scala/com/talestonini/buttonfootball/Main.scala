package com.talestonini.buttonfootball

import cats.effect.unsafe.implicits.global
import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Teams.*
import com.talestonini.buttonfootball.service.TeamService
import org.scalajs.dom
import scala.scalajs.concurrent.JSExecutionContext.queue
import scala.util.{Failure, Success}
import org.scalajs.dom.Event

@main
def ButtonFootballFrontEnd(): Unit = {
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    div(
      input(
        typ := "text",
        value <-- teamName,
        onInput.mapToValue --> teamName,
        onChange --> (ev => getTeams(teamName.now()))
      ),
      // button(className := "btn btn-primary", "⏎️", onClick --> (ev => getTeams(teamName.now()))),
      table(
        className := "table",
        thead(className := "thead-light", tr(
          th("Nome"), th("Tipo"), th("Nome Completo"), th("Fundação"), th("Cidade"), th("País")
        )),
        tbody(
          children <-- teams.signal.map(teams => teams.map(t => renderTeamRow(t)))
        )
      )
    )
  )
}

def renderTeamRow(team: Team): Element =
  tr(
    td(team.name), td(team.`type`), td(team.fullName), td(team.foundation), td(team.city), td(team.country)
  )

def getTeams(name: String): Unit = {
  TeamService
    .getTeams(if (name.isBlank()) None else Some(name.trim()))
    .unsafeToFuture()
    .onComplete({
      case s: Success[List[Team]] => teams.update(_ => s.value)
      case f: Failure[List[Team]] => println(s"failed getting team: ${f.exception.getMessage()}")
    })(queue)

}