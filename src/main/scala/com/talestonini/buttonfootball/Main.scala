package com.talestonini.buttonfootball

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.features.unitArrows
import com.talestonini.buttonfootball.component.FinalsMatchesTabContent
import com.talestonini.buttonfootball.component.MatchElement
import com.talestonini.buttonfootball.component.Table
import com.talestonini.buttonfootball.component.Table.Header
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Championships.*
import com.talestonini.buttonfootball.model.Standings.*
import com.talestonini.buttonfootball.model.TeamTypes.*
import com.talestonini.buttonfootball.util.*
import org.scalajs.dom

@main
def ButtonFootballFrontEnd(): Unit =
  // seGetTeams()
  seGetTeamTypes()
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    // div(
    //   children <-- teams.signal.map(ts => ts.map(t => img(src := Logo.forTeam(t.logoImgFile)))),
    //   mainAppElement()
    // )
    mainAppElement()
  )

def mainAppElement(): Element =
  div(
    cls := s"container shadow ${spacingStyle("p")}",
    h1("Jogo de Botão").wrapInDiv(s"row-12 ${spacingStyle("pb")}"),
    div(
      cls := s"row ${spacingStyle("pb")} ${spacingStyle("g")}",
      renderTeamTypeRadios().wrapInDiv("col-auto"),
      renderChampionshipTypeSelect().wrapInDiv("col"),
    ),
    renderChampionshipEditionsRange().wrapInDiv(s"row-12 ${spacingStyle("pb")}"),
    renderMatchesTabs().wrapInDiv(s"row-12 ${spacingStyle("pb")}"),
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
    Debug.renderInternalState(false)
  )

// --- rendering functions ---------------------------------------------------------------------------------------------

def renderTeamTypeRadios(): Element =
  div(
    cls := "card shadow",
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
    cls := "card shadow h-100 w-100",
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
    cls := "card shadow",
    div(
      cls := "card-body",
      renderCardTitle("Edição"),
      div(
        cls := "row",
        div(
          cls := "col",
          input(
            idAttr := "championshipEditionsRange",
            cls := "form-range",
            typ := "range",
            minAttr <-- championships.signal.map(cs =>
              (if (cs.nonEmpty) MIN_CHAMPIONSHIP_EDITION else NO_CHAMPIONSHIP_EDITION).toString
            ),
            maxAttr <-- championships.signal.map(cs => 
              (if (cs.nonEmpty) cs.length else NO_CHAMPIONSHIP_EDITION).toString),
            onChange.mapToValue --> { edition =>
              selectedChampionship.update(_ => championships.now().find((c) => c.numEdition == edition.toInt))
              selectedEdition.update(_ => edition.toInt)
              seGetMatches(selectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
              // TODO: check whether using 2 APIs is a cleaner design - this is currently not working as the following
              //       quick succession of requests result in backend ConcurrentModificationException
              // seGetGroupStandings(selectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
              // seGetFinalStandings(selectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
              seGetStandings(selectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
            },
            value <-- selectedChampionship.signal.map(_.getOrElse(NO_CHAMPIONSHIP).numEdition.toString())
          )
        ),
        div(
          cls := "col-auto",
          div(
            child.text <-- selectedChampionship.signal.map(c => c.getOrElse(NO_CHAMPIONSHIP).numEdition)
          )
        )
      )
    )
  )

def renderMatchesTabs(): Element =
  div(
    cls := "border rounded shadow",
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
      cls := "tab-content p-0",
      child <-- activeTab.signal.map(renderMatchesTabContent)
    )
  )

def renderMatchesTabContent(tabName: String): Element =
  div(
    cls := "text-center",
    if (tabName.startsWith(GROUP))
      renderGroupMatchesTabContent(tabName)
    else if (tabName == FINALS_TAB)
      FinalsMatchesTabContent()
    else if (tabName == FINAL_STANDINGS_TAB)
      renderFinalStandingsTabContent()
    else
      div()
  )

def renderGroupMatchesTabContent(tabName: String): Element =
  div(
    div(
      cls := "container border bg-white p-3",
      table(
        cls := "table align-middle mb-0",
        tbody(
          children <-- groupsMatches.signal.map(gms => gms.filter(gm => gm.`type` == tabName).map(gm => MatchElement(gm)))
        )
      ),
    ),
    div(
      cls := "container border border-top-0 bg-white p-3",
      child <-- groupStandings.signal.map(gss => {
        val groupStandingsVar: Var[List[Standing]] = Var(gss.filter(gs => gs.`type` == tabName))
        Table[Standing](groupStandingsVar, List(
          Header("Intra-Grupo", 4),
          Header("Extra-Grupo", 5),
          Header("Time", 2),
          Header("Pontos", 7),
          Header("Jogos", 8),
          Header("Vitórias", 9),
          Header("Empates", 10),
          Header("Derrotas", 11),
          Header("Gols Marcados", 12),
          Header("Gols Sofridos", 13),
          Header("Saldo de Gols", 14)
        ))
      })
    )
  )

def renderFinalStandingsTabContent(): Element =
  div(
    cls := "container border bg-white p-3",
    child <-- finalStandings.signal.map(fss => {
      val finalStandingsVar: Var[List[Standing]] = Var(fss)
      Table[Standing](finalStandingsVar, List(
        Header("Final", 6),
        Header("Time", 2),
        Header("Pontos", 7),
        Header("Jogos", 8),
        Header("Vitórias", 9),
        Header("Empates", 10),
        Header("Derrotas", 11),
        Header("Gols Marcados", 12),
        Header("Gols Sofridos", 13),
        Header("Saldo de Gols", 14)
      ))
    })
  )