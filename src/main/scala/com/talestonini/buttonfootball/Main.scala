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
import com.talestonini.buttonfootball.model.Championships.Championship
import com.talestonini.buttonfootball.model.ChampionshipTypes.ChampionshipType
import com.talestonini.buttonfootball.model.TeamTypes.TeamType
import com.talestonini.buttonfootball.service.*

@main
def ButtonFootballFrontEnd(): Unit =
  seGetTeamTypes()
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    div(
      h1("Jogo de Botão"),
      renderTeamTypeRadios(),
      renderChampionshipTypeSelect()
      // input(
      //   typ := "text",
      //   value <-- teamName,
      //   onInput.mapToValue --> teamName,
      //   onChange --> (ev => seGetTeams(teamName.now()))
      // ),
      // TTTable.renderTable(teams, List(
      //   TTHeader("Nome", 1),
      //   TTHeader("Tipo", 2),
      //   TTHeader("Nome Completo", 3),
      //   TTHeader("Fundação", 4),
      //   TTHeader("Cidade", 5),
      //   TTHeader("País", 6)
      // ))
    )
  )

// --- rendering functions ---------------------------------------------------------------------------------------------

def renderCardTitle(title: String) =
  h6(
    className := "card-subtitle mb-2 text-muted",
    title
  )

def renderTeamTypeRadios(): Element =
  div(
    className := "card",
    div(
      className := "card-body",
      renderCardTitle("Tipo de Time"),
      children <-- teamTypes.signal.map(data => data.map(tt =>
        div(
          className := "form-check",
          label(
            className := "form-check-label",
            input(
              className := "form-check-input",
              typ := "radio",
              nameAttr := "teamType",
              value := tt.code,
              onInput.mapToValue --> selectedTeamType,
              onChange --> (ev => seGetChampionshipTypes(selectedTeamType.now()))
            ),
            tt.description
          )
        )
      ))
    )
  )

def renderChampionshipTypeSelect(): Element =
  div(
    className := "card",
    div(
      className := "card-body",
      renderCardTitle("Tipo de Campeonato"),
      select(
        className := "form-select",
        children <-- championshipTypes.signal.map(data => data.map(ct =>
          option(
            value := ct.code,
            ct.description
          )
        )),
        onInput.mapToValue --> selectedChampionshipType,
        onChange --> (ev => seGetChampionships(selectedChampionshipType.now()))
      )
    )
  )

// --- side-effect functions -------------------------------------------------------------------------------------------

def seGetTeamTypes(): Unit =
  println(s"fetching team types")
  TeamTypeService
    .getTeamTypes()
    .unsafeToFuture()
    .onComplete({
      case s: Success[List[TeamType]] => teamTypes.update(_ => s.value)
      case f: Failure[List[TeamType]] => println(s"failed fetching team types: ${f.exception.getMessage()}")
    })(queue)
end seGetTeamTypes

def seGetChampionshipTypes(codTeamType: String): Unit =
  println(s"fetching championship types with team type code '${codTeamType}'")
  ChampionshipTypeService
    .getChampionshipTypes(Some(codTeamType))
    .unsafeToFuture()
    .onComplete({
      case s: Success[List[ChampionshipType]] => championshipTypes.update(_ => s.value)
      case f: Failure[List[ChampionshipType]] =>
        println(s"failed fetching championship type: ${f.exception.getMessage()}")
    })(queue)
end seGetChampionshipTypes

def seGetChampionships(codChampionshipType: String): Unit =
  println(s"fetching championships with championship type code '${codChampionshipType}'")
  ChampionshipService
    .getChampionships(Some(codChampionshipType))
    .unsafeToFuture()
    .onComplete({
      case s: Success[List[Championship]] => championships.update(_ => s.value)
      case f: Failure[List[Championship]] =>
        println(s"failed fetching championships: ${f.exception.getMessage()}")
    })(queue)
end seGetChampionships

def seGetChampionships(championshipTypeId: Int): Unit =
  println(s"fetching championships with championship type id '${championshipTypeId}'")
  ChampionshipTypeService
    .getChampionships(championshipTypeId)
    .unsafeToFuture()
    .onComplete({
      case s: Success[List[Championship]] => championships.update(_ => s.value)
      case f: Failure[List[Championship]] =>
        println(s"failed fetching championships: ${f.exception.getMessage()}")
    })(queue)
end seGetChampionships

def seGetTeams(name: String): Unit =
  println(s"fetching team with name '${name}'")
  TeamService
    .getTeams(if (name.isBlank()) None else Some(name.trim()))
    .unsafeToFuture()
    .onComplete({
      case s: Success[List[Team]] => teams.update(_ => s.value)
      case f: Failure[List[Team]] => println(s"failed fetching team: ${f.exception.getMessage()}")
    })(queue)
end seGetTeams