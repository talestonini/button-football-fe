package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.component.Table.Column
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Rankings.*
import com.talestonini.buttonfootball.service.*
import com.talestonini.util.*
import com.talestonini.util.Window.Size

object RankingTabContent:

  def apply(): Element =
    div(
      div(cls := "text-start", text <-- I18n(RankingNoteToken)),
      cls := "container border bg-white p-3 text-end",
      buildStyleAttr("overflow-x: auto"),
      child <-- vRankings.signal.map(rs => {
        val vRankings: Var[List[Ranking]] = Var(rs)
        val ws = Window.size()
        val smallish = ws == Size.Small || ws == Size.Medium
        Table[Ranking](vRankings, List(
          Column(if (smallish) RankingPositionShortToken else RankingPositionToken, 9),
          TeamColumn(3),
          Column(EmptyToken, 2, "text-start", !smallish,
            Some((teamName: String) => span(text <-- I18n(teamName, TeamTranslationMap)))
          ),
          Column(if (smallish) RankingPointsShortToken else RankingPointsToken, 8),
          Column(if (smallish) RankingChampionshipsShortToken else RankingChampionshipsToken, 18),
          Column(if (smallish) RankingParticipationsShortToken else RankingParticipationsToken, 7),
          Column(if (smallish) RankingAveragePositionShortToken else RankingAveragePositionToken, 6),
          // Decided not to have these fields in the table:
          // Column(if (smallish) RankingBestPositionShortToken else RankingBestPositionToken, 4),
          // Column(if (smallish) RankingWorstPositionShortToken else RankingWorstPositionToken, 5),
          Column(if (smallish) StandingsPointsShortToken else StandingsPointsToken, 10),
          Column(if (smallish) StandingsMatchesShortToken else StandingsMatchesToken, 11),
          Column(if (smallish) StandingsWinsShortToken else StandingsWinsToken, 12),
          Column(if (smallish) StandingsDrawsShortToken else StandingsDrawsToken, 13),
          Column(if (smallish) StandingsLossesShortToken else StandingsLossesToken, 14),
          Column(if (smallish) StandingsGoalsScoredShortToken else StandingsGoalsScoredToken, 15),
          Column(if (smallish) StandingsGoalsConcededShortToken else StandingsGoalsConcededToken, 16),
          Column(if (smallish) StandingsGoalsDiffShortToken else StandingsGoalsDiffToken, 17)
        ))
      })
    )

end RankingTabContent