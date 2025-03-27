package com.talestonini.buttonfootball.util

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.TeamTypes.NO_TEAM_TYPE
import com.talestonini.buttonfootball.model.ChampionshipTypes.NO_CHAMPIONSHIP_TYPE
import com.talestonini.buttonfootball.model.Championships.NO_CHAMPIONSHIP
import com.talestonini.buttonfootball.component.FinalsMatchesTabContent.{cols, rows}
import com.talestonini.buttonfootball.service.ChampionshipService.calcNumQualif
import org.scalajs.dom.HTMLDivElement

object Debug:

  def internalStateView(isEnabled: Boolean = false): ReactiveHtmlElement[HTMLDivElement] =
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
        div(child.text <-- teams.signal.map(ts => s"Teams count: ${ts.size}"))
      )

  // --- assertions functions ------------------------------------------------------------------------------------------
  
  def assertCorrectNumQualifAndFinalsMatches(): Signal[String] =
    selectedChampionship.signal
      .combineWith(numFinalsMatches)
      .combineWith(numTeams)
      .map { case (sc, nfm, nt) => "Number of qualified teams: " + (calcNumQualif(nt) match {
        case Left(e) =>
          s"error (${e.getMessage})"
        case Right(calcNumQualif) =>
          val dbNumQualif = sc.getOrElse(NO_CHAMPIONSHIP).numQualif
          val res = if (dbNumQualif == calcNumQualif && nfm == calcNumQualif) "" else "in"
          s"${res}correct (db: $dbNumQualif, calculated: $calcNumQualif, number of finals matches: $nfm)"
      })}

end Debug