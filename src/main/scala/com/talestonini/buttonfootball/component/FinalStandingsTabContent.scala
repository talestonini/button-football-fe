package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.component.Table.Column
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Standings.*
import com.talestonini.buttonfootball.service.*
import com.talestonini.util.*
import com.talestonini.util.Window.Size

object FinalStandingsTabContent:

  def apply(): Element =
    div(
      cls := "container border bg-white p-3 text-end",
      buildStyleAttr("overflow-x: auto"),
      child <-- vFinalStandings.signal.map(fss => {
        val vFinalStandings: Var[List[Standing]] = Var(fss)
        val ws = Window.size()
        val smallish = ws == Size.Small || ws == Size.Medium
        Table[Standing](vFinalStandings, List(
          Column(if (smallish) StandingsFinalShortToken else  StandingsFinalToken, 8),
          StandingsTeamColumn(ws),
          Column(EmptyToken, 3, "text-start", !smallish,
            Some((teamName: String) => span(text <-- I18n(teamName, TeamTranslationMap)))
          ),
          Column(if (smallish) StandingsPointsShortToken else StandingsPointsToken, 9),
          Column(if (smallish) StandingsMatchesShortToken else StandingsMatchesToken, 10),
          Column(if (smallish) StandingsWinsShortToken else StandingsWinsToken, 11),
          Column(if (smallish) StandingsDrawsShortToken else StandingsDrawsToken, 12),
          Column(if (smallish) StandingsLossesShortToken else StandingsLossesToken, 13),
          Column(if (smallish) StandingsGoalsScoredShortToken else StandingsGoalsScoredToken, 14),
          Column(if (smallish) StandingsGoalsConcededShortToken else StandingsGoalsConcededToken, 15),
          Column(if (smallish) StandingsGoalsDiffShortToken else StandingsGoalsDiffToken, 16)
        ))
      })
    )

end FinalStandingsTabContent