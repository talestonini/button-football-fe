package com.talestonini.buttonfootball

import cats.effect.unsafe.implicits.global
import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.service.TeamsService
import org.scalajs.dom
import scala.scalajs.concurrent.JSExecutionContext.queue
import scala.util.{Failure, Success}
import com.talestonini.buttonfootball.model.Teams.Team

val team: Var[Team] = Var(Team(-1, "-", "-", "-", "-", "-", "-", "-"))
val teamSig = team.signal

@main
def ButtonFootballFrontEnd(): Unit = {
  val team = getTeam("Corinthians")

  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    table(
      className := "table",
      thead(className := "thead-light", tr(
        th("Nome"), th("Tipo"), th("Nome Completo"), th("Fundação"), th("Cidade"), th("País")
      )),
      tbody(tr(
        td(child.text <-- teamSig.map(t => t.name)),
        td(child.text <-- teamSig.map(t => t.`type`)),
        td(child.text <-- teamSig.map(t => t.fullName)),
        td(child.text <-- teamSig.map(t => t.foundation)),
        td(child.text <-- teamSig.map(t => t.city)),
        td(child.text <-- teamSig.map(t => t.country))
      ))
    )
  )
}

def getTeam(name: String): Unit = {
  TeamsService
    .getTeam(name)
    .unsafeToFuture()
    .onComplete({
      case s: Success[Team] => team.update(_ => s.value)
      case f: Failure[Team] => println(s"failed getting team: ${f.exception.getMessage()}")
    })(queue)

}