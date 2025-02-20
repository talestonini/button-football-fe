package com.talestonini.buttonfootball

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.features.unitArrows
import com.talestonini.buttonfootball.component.FinalsMatchesTabContent
import com.talestonini.buttonfootball.component.TTTable
import com.talestonini.buttonfootball.component.TTTable.TTHeader
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Championships.*
import com.talestonini.buttonfootball.model.ChampionshipTypes.*
import com.talestonini.buttonfootball.model.Matches.*
import com.talestonini.buttonfootball.model.Teams.*
import com.talestonini.buttonfootball.model.TeamTypes.*
import com.talestonini.buttonfootball.service.ChampionshipService.calcNumQualif
import com.talestonini.buttonfootball.component.FinalsMatchesTabContent.{rows, cols}
import org.scalajs.dom

@main
def ButtonFootballFrontEnd(): Unit =
  seGetTeamTypes()
  FinalsMatchesTabContent.setupSvgCurvesAutoRender()
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    div(
      cls := "container",
      styleAttr := "height: 110px;",
      renderStateForInspection(false),
      h1("Jogo de Botão"),
      div(
        cls := "row h-100",
        renderTeamTypeRadios().wrap("col-auto h-100 d-flex"),
        renderChampionshipTypeSelect().wrap("col h-100 d-flex"),
      ),
      renderChampionshipEditionsRange().wrap("row h-100"),
      renderMatchesTabs(),
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

extension (elem: Element)
  def wrap(className: String = ""): Element = div(cls := className, elem)

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
      ),
      div(
        child.text <-- tabs.map(ts => "Tab count: " + ts.length)
      ),
      div(
        child.text <-- activeTab.signal.map(at => "Active tab: " + at)
      ),
      div(
        child.text <-- assertCorrectNumQualifAndFinalsMatches()
      ),
      div(
        child.text <-- rows.combineWith(cols).map {
          case(r, c) => s"Finals rows: $r, Finals cols: $c"
        }
      )
    )

def renderTeamTypeRadios(): Element =
  div(
    cls := "card h-100 w-100",
    div(
      cls := "card-body",
      renderCardTitle("Tipo de Time"),
      children <-- teamTypes.signal.map(tts => tts.map(tt =>
        div(
          cls := "form-check",
          label(
            cls := "form-check-label",
            input(
              cls := "form-check-input",
              typ := "radio",
              nameAttr := "teamType",
              value := tt.code,
              onChange.mapToValue --> { code =>
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
    cls := "card h-100 w-100",
    div(
      cls := "card-body",
      renderCardTitle("Campeonato"),
      select(
        cls := "form-select",
        children <-- championshipTypes.signal.map(cts => cts.map(ct =>
          option(
            value := ct.code,
            ct.description
          )
        )),
        onChange.mapToValue --> { code =>
          selectedChampionshipType.update(_ => championshipTypes.now().find((ct) => ct.code == code))
          seGetChampionships(code)
        },
      )
    )
  )

def renderChampionshipEditionsRange(): Element =
  div(
    cls := "container card h-100 w-100",
    div(
      cls := "row card-body",
      renderCardTitle("Edição"),
      div(
        cls := "col",
        input(
          idAttr := "championshipEditionsRange",
          cls := "form-range",
          typ := "range",
          minAttr <-- championships.signal.map(cs =>
            (if (!cs.isEmpty) MIN_CHAMPIONSHIP_EDITION else NO_CHAMPIONSHIP_EDITION).toString()
          ),
          maxAttr <-- championships.signal.map(cs => 
            (if (!cs.isEmpty) cs.length else NO_CHAMPIONSHIP_EDITION).toString()),
          onChange.mapToValue --> { edition =>
            selectedChampionship.update(_ => championships.now().find((ce) => ce.numEdition == edition.toInt))
            seGetMatches(selectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
            seGetGroupStandings(selectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
          },
          value <-- selectedChampionship.signal.map(_.getOrElse(NO_CHAMPIONSHIP).numEdition.toString())
        )
      ),
      div(
        cls := "col-auto",
        child.text <-- selectedChampionship.signal.map(c => c.getOrElse(NO_CHAMPIONSHIP).numEdition)
      )
    )
  )

def renderMatchesTabs(): Element =
  div(
    cls := "row h-100 justify-content-center",
    ul(
      cls := "nav nav-tabs",
      children <-- tabs.map(ts => ts.map(t =>
        li(
          cls := "nav-item",
          button(
            cls := "nav-link",
            cls <-- activeTab.signal.map(at => if (t == at) "active" else ""),
            onClick --> activeTab.update(ev => t),
            b(t)
          )
        )
      )),
    ),
    div(
      cls := "tab-content",
      child <-- activeTab.signal.map(renderMatchesTabContent)
    )
  )

def renderMatchesTabContent(tabName: String): Element =
  div(
    cls := "text-center",
    if (tabName.startsWith(GROUP)) renderGroupMatchesTabContent(tabName) else FinalsMatchesTabContent()
  )

def renderGroupMatchesTabContent(tabName: String): Element =
  table(
    cls := "table",
    tbody(
      children <-- matches.signal.map(ms => ms.filter(m => m.`type` == tabName).map(m => renderMatch(m)))
    )
  )

def renderMatch(m: Match, isFinalsStage: Boolean = false): Element =
  def displayInFinals() = display(if (isFinalsStage) "table-cell" else "none")
  tr(
    td(cls := "col text-end", styleAttr := "width: 200px;", m.teamA),
    td(cls := "col-auto text-center", m.numGoalsPntA, displayInFinals()),
    td(cls := "col-auto text-center", m.numGoalsExtraA, displayInFinals()),
    td(cls := "col-auto text-center", m.numGoalsTeamA),
    td(cls := "col-auto text-center", " x "),
    td(cls := "col-auto text-center", m.numGoalsTeamB),
    td(cls := "col-auto text-center", m.numGoalsExtraB, displayInFinals()),
    td(cls := "col-auto text-center", m.numGoalsPntB, displayInFinals()),
    td(cls := "col text-start", styleAttr := "width: 200px;", m.teamB)
  )
end renderMatch

// --- assertions functions ------------------------------------------------------------------------------------------

def assertCorrectNumQualifAndFinalsMatches(): Signal[String] =
  selectedChampionship.signal
    .combineWith(numFinalsMatches)
    .combineWith(numTeams)
    .map { case (sc, nfm, nt) => "Number of qualified teams: " + (calcNumQualif(nt) match {
      case Left(e) =>
        s"error (${e.getMessage()})"
      case Right(calcNumQualif) =>
        val dbNumQualif = sc.getOrElse(NO_CHAMPIONSHIP).numQualif
        val res = if (dbNumQualif == calcNumQualif && nfm == calcNumQualif) "" else "in"
        s"${res}correct (db: $dbNumQualif, calculated: $calcNumQualif, number of finals matches: $nfm)"
    })}