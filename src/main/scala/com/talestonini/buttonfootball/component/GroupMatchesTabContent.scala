package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.component.Table
import com.talestonini.buttonfootball.component.Table.*
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Standings.*
import com.talestonini.buttonfootball.service.*
import com.talestonini.util.*
import com.talestonini.util.Window.Size

object GroupMatchesTabContent:

  def apply(tabName: String): Element =
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
            Column(if (smallish) StandingsIntraGroupShortToken else StandingsIntraGroupToken, 6),
            StandingsTeamColumn(ws),
            Column(if (smallish) StandingsPointsShortToken else StandingsPointsToken, 9),
            Column(if (smallish) StandingsMatchesShortToken else StandingsMatchesToken, 10),
            Column(if (smallish) StandingsWinsShortToken else StandingsWinsToken, 11),
            Column(if (smallish) StandingsDrawsShortToken else StandingsDrawsToken, 12),
            Column(if (smallish) StandingsLossesShortToken else StandingsLossesToken, 13),
            Column(if (smallish) StandingsGoalsScoredShortToken else StandingsGoalsScoredToken, 14),
            Column(if (smallish) StandingsGoalsConcededShortToken else StandingsGoalsConcededToken, 15),
            Column(if (smallish) StandingsGoalsDiffShortToken else StandingsGoalsDiffToken, 16),
            Column(if (smallish) StandingsExtraGroupShortToken else StandingsExtraGroupToken, 7)
          ))
        })
      )
    )

end GroupMatchesTabContent