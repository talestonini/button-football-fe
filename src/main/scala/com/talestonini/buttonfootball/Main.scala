package com.talestonini.buttonfootball

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.component.*
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.util.*
import com.talestonini.component.AccordionItem
import com.talestonini.util.*

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

private val mainRowClasses = s"row-12 ${spacingStyle("pb")}"

def mainAppElement(): Element =
  div(
    cls := s"container shadow ${spacingStyle("p")} text-muted",
    Debug.internalStateView(),
    h1(buildStyleAttr("font-weight: bold"), "Jogo de Botão"),
    div(
      cls := s"accordion",
      AccordionItem("collapseTeamTypes", "Tipo de Time", TeamTypesContent()).wrapInDiv(mainRowClasses),
      AccordionItem("collapseChampionships", "Campeonato", ChampionshipsContent()).wrapInDiv(mainRowClasses)
    ),
    Tabs().wrapInDiv(mainRowClasses),
    // input(
    //   typ := "text",
    //   value <-- teamName,
    //   onInput.mapToValue --> teamName,
    //   onChange --> (ev => seGetTeams(teamName.now()))
    // ),
  )