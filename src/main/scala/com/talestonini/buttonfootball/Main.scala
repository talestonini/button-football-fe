package com.talestonini.buttonfootball

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.component.TTTable
import com.talestonini.buttonfootball.component.TTTable.TTHeader
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Championships.*
import com.talestonini.buttonfootball.model.ChampionshipTypes.*
import com.talestonini.buttonfootball.model.Teams.*
import com.talestonini.buttonfootball.model.TeamTypes.*
import org.scalajs.dom

@main
def ButtonFootballFrontEnd(): Unit =
  seGetTeamTypes()
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    div(
      h1("Jogo de Botão"),
      renderTeamTypeRadios(),
      renderChampionshipTypeSelect(),
      renderChampionshipEditionsRange(),
      renderStateForInspection(true)
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

def renderStateForInspection(isEnabled: Boolean) =
  if (!isEnabled)
    div()
  else
    div(
      div(
        child.text <-- selectedTeamType.signal.map(tt => "Team type: " + tt.getOrElse(NO_TEAM_TYPE).description)
      ),
      div(
        child.text <-- selectedChampionshipType.signal
          .map(ct => "Championship type: " + ct.getOrElse(NO_CHAMPIONSHIP_TYPE).description),
      ),
      div(
        child.text <-- selectedChampionship.signal
          .map(c => "Championship edition: " + c.getOrElse(NO_CHAMPIONSHIP).numEdition)
      )
    )

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
      children <-- teamTypes.signal.map(tts => tts.map(tt =>
        div(
          className := "form-check",
          label(
            className := "form-check-label",
            input(
              className := "form-check-input",
              typ := "radio",
              nameAttr := "teamType",
              value := tt.code,
              onInput.mapToValue --> { code =>
                selectedTeamType.update(_ => teamTypes.now().find((tt) => tt.code == code))
                seGetChampionshipTypes(code)
              },
              checked <-- selectedTeamType.signal.map(_.getOrElse(NO_TEAM_TYPE).code == tt.code)
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
      renderCardTitle("Campeonato"),
      select(
        className := "form-select",
        children <-- championshipTypes.signal.map(cts => cts.map(ct =>
          option(
            value := ct.code,
            ct.description
          )
        )),
        onInput.mapToValue --> { code =>
          selectedChampionshipType.update(_ => championshipTypes.now().find((ct) => ct.code == code))
          seGetChampionships(code)
        },
      )
    )
  )

def renderChampionshipEditionsRange(): Element =
  div(
    className := "card",
    div(
      className := "card-body",
      renderCardTitle("Edição"),
      input(
        idAttr := "championshipEditionsRange",
        className := "form-range",
        typ := "range",
        minAttr <-- championships.signal.map(cs =>
          if (!cs.isEmpty) MIN_CHAMPIONSHIP_EDITION.toString() else NO_CHAMPIONSHIP_EDITION.toString()
        ),
        maxAttr <-- championships.signal.map(cs => 
          if (!cs.isEmpty) cs.length.toString() else NO_CHAMPIONSHIP_EDITION.toString()),
        onInput.mapToValue --> { edition =>
          selectedChampionship.update(_ => championships.now().find((ce) => ce.numEdition == edition.toInt))
        },
        value <-- selectedChampionship.signal.map(_.getOrElse(NO_CHAMPIONSHIP).numEdition.toString())
      ),
      child.text <-- selectedChampionship.signal.map(c => c.getOrElse(NO_CHAMPIONSHIP).numEdition)
    )
  )