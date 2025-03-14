package com.talestonini.buttonfootball

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.features.unitArrows
import com.talestonini.buttonfootball.component.FinalsMatchesTabContent
import com.talestonini.buttonfootball.component.MatchElement
import com.talestonini.buttonfootball.component.TTTable
import com.talestonini.buttonfootball.component.TTTable.TTHeader
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Championships.*
import com.talestonini.buttonfootball.model.ChampionshipTypes.*
import com.talestonini.buttonfootball.model.Standings.*
import com.talestonini.buttonfootball.model.TeamTypes.*
import com.talestonini.buttonfootball.service.ChampionshipService.calcNumQualif
import com.talestonini.buttonfootball.component.FinalsMatchesTabContent.{rows, cols, staticCellLinks}
import org.scalajs.dom

@main
def ButtonFootballFrontEnd(): Unit =
  // seGetTeams()
  seGetTeamTypes()
  FinalsMatchesTabContent.setupAutoReRenderOfCellLinksOnWindowEvents()
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    div(
      // children <-- teams.signal.map(ts => ts.map(t => img(src := Logo.forTeam(t.logoImgFile)))),
      mainAppElement()
    )
  )

def mainAppElement(): Element =
  div(
    cls := "container",
    styleAttr := "height: 110px;",
    renderStateForInspection(),
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

// --- rendering functions ---------------------------------------------------------------------------------------------

extension (elem: Element)
  def wrap(className: String = ""): Element = div(cls := className, elem)

def renderStateForInspection(isEnabled: Boolean = false) =
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
      ),
      div(
        child.text <-- groupStandings.signal.combineWith(finalStandings.signal).map {
          case(gss, fss) => s"Group Standings: ${gss.size}, Final Standings: ${fss.size}"
        }
      ),
      div(child.text <-- staticCellLinks.signal.map(scl => s"Static cell links size: ${scl.size}")),
      div(child.text <-- teams.signal.map(ts => s"Teams count: ${ts.size}"))
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
    if (tabName.startsWith(GROUP))
      renderGroupMatchesTabContent(tabName)
    else if (tabName == FINALS_TAB)
      FinalsMatchesTabContent()
    else if (tabName == FINAL_STANDINGS_TAB)
      renderFinalStandingsTabContent()
    else
      div("Invalid tab :(")
  )

def renderGroupMatchesTabContent(tabName: String): Element =
  div(
    table(
      cls := "table",
      styleAttr := "vertical-align: middle",
      tbody(
        children <-- groupsMatches.signal.map(gms => gms.filter(gm => gm.`type` == tabName).map(gm => MatchElement(gm)))
      )
    ),
    div(
      cls := "container card h-100 w-100",
      div(
        cls := "card-body",
        renderCardTitle("Classificação"),
        child <-- groupStandings.signal.map(gss => {
          val groupStandingsVar: Var[List[Standing]] = Var(gss.filter(gs => gs.`type` == tabName))
          TTTable[Standing](groupStandingsVar, List(
            TTHeader("Intra-Grupo", 4),
            TTHeader("Extra-Grupo", 5),
            TTHeader("Time", 2),
            TTHeader("Pontos", 7),
            TTHeader("Jogos", 8),
            TTHeader("Vitórias", 9),
            TTHeader("Empates", 10),
            TTHeader("Derrotas", 11),
            TTHeader("Gols Marcados", 12),
            TTHeader("Gols Sofridos", 13),
            TTHeader("Saldo de Gols", 14)
          ))
        })
      )
    )
  )

def renderFinalStandingsTabContent(): Element =
  div(
    child <-- finalStandings.signal.map(fss => {
      val finalStandingsVar: Var[List[Standing]] = Var(fss)
      TTTable[Standing](finalStandingsVar, List(
        TTHeader("Final", 6),
        TTHeader("Time", 2),
        TTHeader("Pontos", 7),
        TTHeader("Jogos", 8),
        TTHeader("Vitórias", 9),
        TTHeader("Empates", 10),
        TTHeader("Derrotas", 11),
        TTHeader("Gols Marcados", 12),
        TTHeader("Gols Sofridos", 13),
        TTHeader("Saldo de Gols", 14)
      ))
    })
  )

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