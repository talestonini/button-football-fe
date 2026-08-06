package com.talestonini.buttonfootball

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.component.*
import com.talestonini.buttonfootball.component.facade.*
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.service.*
import com.talestonini.buttonfootball.util.*
import com.talestonini.buttonfootball.component.{AccordionItem, Footer, Languages}
import com.talestonini.util.*
import com.talestonini.buttonfootball.livechart.LiveChart

@main
def ButtonFootballFrontEnd(): Unit =
  seGetTeams()
  seGetTeamTypes()
  Tooltip.initTooltips()
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
    LiveChart.appElement(),
    cls := s"container shadow ${spacingStyle("p")} text-muted",
    Debug.internalStateView(),
    div(
      cls := "row",
      h1(cls := "col", buildStyleAttr("font-weight: bold"), text <-- I18n(AppTitleToken)),
      div(cls := "col-auto align-self-center", Languages(vLang, PT_BR, EN))
    ),
    div(
      cls := s"accordion",
      AccordionItem("collapseTeamTypes", TeamTypeToken, TeamTypesContent()).wrapInDiv(mainRowClasses),
      AccordionItem("collapseChampionships", ChampionshipToken, ChampionshipsContent()).wrapInDiv(mainRowClasses)
    ),
    Tabs().wrapInDiv(mainRowClasses),
    div(
      cls := "text-center",
      Footer()
    ),
    // input(
    //   typ := "text",
    //   value <-- teamName,
    //   onInput.mapToValue --> teamName,
    //   onChange --> (ev => seGetTeams(teamName.now()))
    // ),
  )