package com.talestonini.buttonfootball

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.features.unitArrows
import com.talestonini.buttonfootball.component.*
import com.talestonini.buttonfootball.component.Table.*
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Championships.*
import com.talestonini.buttonfootball.model.Standings.*
import com.talestonini.buttonfootball.model.TeamTypes.*
import com.talestonini.buttonfootball.util.*
import com.talestonini.buttonfootball.util.Logo.*
import com.talestonini.buttonfootball.util.Window.Size

@main
def ButtonFootballFrontEnd(): Unit =
  seGetTeams()
  seGetTeamTypes()
  renderOnDomContentLoaded(
    Elem.byId("app"),
    // div(
    //   children <-- teams.signal.map(ts => ts.map(t => img(src := Logo.forTeam(t.logoImgFile)))),
    //   mainAppElement()
    // )
    mainAppElement()
  )

def mainAppElement(): Element =
  div(
    cls := s"container shadow ${spacingStyle("p")} text-muted",
    h1(
      buildStyleAttr("font-weight: bold"),
      "Jogo de Botão"
    ).wrapInDiv(s"row-12 ${spacingStyle("pb")}"),
    div(
      cls := s"row ${spacingStyle("pb")} ${spacingStyle("g")}",
      teamTypeRadios().wrapInDiv("col-auto"),
      championshipTypeSelect().wrapInDiv("col"),
    ),
    championshipEditionsRange().wrapInDiv(s"row-12 ${spacingStyle("pb")}"),
    tabs().wrapInDiv(s"row-12 ${spacingStyle("pb")}"),
    // input(
    //   typ := "text",
    //   value <-- teamName,
    //   onInput.mapToValue --> teamName,
    //   onChange --> (ev => seGetTeams(teamName.now()))
    // ),
    Debug.internalStateView()
  )

// --- rendering functions ---------------------------------------------------------------------------------------------

def teamTypeRadios(): Element =
  div(
    cls := "card shadow h-100 w-100",
    div(
      cls := "card-body",
      cardTitle("Tipo de Time"),
      children <-- vTeamTypes.signal.map(tts => tts.map(tt =>
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
                vSelectedTeamType.update(_ => vTeamTypes.now().find((tt) => tt.code == code))
                seGetChampionshipTypes(code)
              },
              checked <-- vSelectedTeamType.signal.map(_.getOrElse(NO_TEAM_TYPE).code == tt.code)
            ),
            tt.description
          )
        )
      ))
    )
  )

def championshipTypeSelect(): Element =
  div(
    cls := "card shadow h-100 w-100",
    div(
      cls := "card-body",
      cardTitle("Campeonato"),
      select(
        cls := "form-select",
        children <-- vChampionshipTypes.signal.map(cts => cts.map(ct =>
          option(
            value := ct.code,
            ct.description
          )
        )),
        onChange.mapToValue --> { code =>
          vSelectedChampionshipType.update(_ => vChampionshipTypes.now().find((ct) => ct.code == code))
          seGetChampionships(code)
        },
      )
    )
  )

def championshipEditionsRange(): Element =
  div(
    cls := "card shadow",
    div(
      cls := "card-body",
      cardTitle("Edição"),
      div(
        cls := "row",
        div(
          cls := "col",
          input(
            idAttr := "championshipEditionsRange",
            cls := "form-range",
            typ := "range",
            minAttr <-- vChampionships.signal.map(cs =>
              (if (cs.nonEmpty) MIN_CHAMPIONSHIP_EDITION else NO_CHAMPIONSHIP_EDITION).toString
            ),
            maxAttr <-- vChampionships.signal.map(cs => 
              (if (cs.nonEmpty) cs.length else NO_CHAMPIONSHIP_EDITION).toString),
            onChange.mapToValue --> { edition =>
              vSelectedChampionship.update(_ => vChampionships.now().find((c) => c.numEdition == edition.toInt))
              vSelectedEdition.update(_ => edition.toInt)
              seGetMatches(vSelectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
              // TODO: check whether using 2 APIs is a cleaner design - this is currently not working as the following
              //       quick succession of requests result in backend ConcurrentModificationException
              // seGetGroupStandings(selectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
              // seGetFinalStandings(selectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
              seGetStandings(vSelectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
            },
            value <-- vSelectedChampionship.signal.map(_.getOrElse(NO_CHAMPIONSHIP).numEdition.toString())
          )
        ),
        div(
          cls := "col-auto",
          div(
            child.text <-- vSelectedChampionship.signal.map(c => c.getOrElse(NO_CHAMPIONSHIP).numEdition)
          )
        )
      )
    )
  )

def tabs(): Element =
  div(
    cls := "border rounded shadow",
    ul(
      cls := "nav nav-tabs",
      children <-- sTabs.map(ts => ts.map(t =>
        li(
          cls := "nav-item",
          button(
            cls := "nav-link text-muted",
            cls <-- vActiveTab.signal.map(at => if (t == at) "active" else ""),
            onClick --> vActiveTab.update(ev => t),
            b(t)
          )
        )
      )),
    ),
    div(
      cls := "tab-content p-0",
      child <-- vActiveTab.signal.map(tabContent)
    )
  )

def tabContent(tabName: String): Element =
  div(
    cls := "text-center",
    // spinner(),
    if (tabName.startsWith(GROUP))
      groupMatchesTabContent(tabName)
    else if (tabName == FINALS_TAB)
      FinalsMatchesTabContent()
    else if (tabName == FINAL_STANDINGS_TAB)
      finalStandingsTabContent()
    else
      div()
  )

def groupMatchesTabContent(tabName: String): Element =
  div(
    div(
      cls := "container border bg-white p-3",
      buildStyleAttr("overflow-x: auto"),
      table(
        cls := "table align-middle mb-0",
        tbody(
          children <-- sGroupsMatches.signal.map(
            gms => gms.filter(gm => gm.`type` == tabName).map(gm => MatchTableRow(gm))
          )
        )
      ),
    ),
    div(
      cls := "container border border-top-0 bg-white p-3 text-end",
      buildStyleAttr("overflow-x: auto"),
      child <-- vGroupStandings.signal.map(gss => {
        val vGroupStandings: Var[List[Standing]] = Var(gss.filter(gs => gs.`type` == tabName))
        val ws = Window.size()
        val smallish = ws == Size.Small || ws == Size.Medium
        Table[Standing](vGroupStandings, List(
          Column(if (smallish) "" else "Intra-Grupo", 4),
          standingsTeamColumn(ws),
          Column(if (smallish) "P" else "Pontos", 7),
          Column(if (smallish) "J" else "Jogos", 8),
          Column(if (smallish) "V" else "Vitórias", 9),
          Column(if (smallish) "E" else "Empates", 10),
          Column(if (smallish) "D" else "Derrotas", 11),
          Column(if (smallish) "GM" else "Gols Marcados", 12),
          Column(if (smallish) "GS" else "Gols Sofridos", 13),
          Column(if (smallish) "S" else "Saldo de Gols", 14),
          Column(if (smallish) "EG" else "Extra-Grupo", 5)
        ))
      })
    )
  )

def finalStandingsTabContent(): Element =
  div(
    cls := "container border bg-white p-3 text-end",
    buildStyleAttr("overflow-x: auto"),
    child <-- vFinalStandings.signal.map(fss => {
      val vFinalStandings: Var[List[Standing]] = Var(fss)
      val ws = Window.size()
      val smallish = ws == Size.Small || ws == Size.Medium
      Table[Standing](vFinalStandings, List(
        Column(if (smallish) "" else "Final", 6),
        standingsTeamColumn(ws),
        Column("", 2, "text-start", !smallish),
        Column(if (smallish) "P" else "Pontos", 7),
        Column(if (smallish) "J" else "Jogos", 8),
        Column(if (smallish) "V" else "Vitórias", 9),
        Column(if (smallish) "E" else "Empates", 10),
        Column(if (smallish) "D" else "Derrotas", 11),
        Column(if (smallish) "GM" else "Gols Marcados", 12),
        Column(if (smallish) "GS" else "Gols Sofridos", 13),
        Column(if (smallish) "S" else "Saldo de Gols", 14)
      ))
    })
  )

private def standingsTeamColumn(windowSize: Size) =
  Column("", 2, "text-center", true, Some((teamName: String) =>
    LogoImage(Logo.forTeamName(teamName).getOrElse(""), XSMALL_TEAM_LOGO_PX_SIZE)
  ))

def spinner(): Element =
  div(
    cls := "d-flex justify-content-center",
    div(
      cls := "spinner-border",
      role := "status",
      span(
        cls := "visually-hidden",
        "Carregando..."
      )
    )
  )