package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.component.Table.Column
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Rankings.*
import com.talestonini.buttonfootball.model.Scorings.*
import com.talestonini.buttonfootball.service.*
import com.talestonini.util.*
import com.talestonini.util.Window.Size

object RankingTabContent:

  def apply(): Element =
    div(
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
      }),
      div(
        cls := "text-start",
        text <-- I18n(RankingUpToEditionNoteToken).combineWith(vSelectedEdition.signal)
          .map((token, edition) => token.format(edition)),
        span(
          cls := "p-2",
          text <-- I18n(RankingSeePointsTableToken),
          button(
            cls := "btn btn-link",
            buildStyleAttr("vertical-align: initial", "--bs-btn-padding-x: 0"),
            typ := "button",
            dataAttr("bs-toggle") := "modal",
            dataAttr("bs-target") := "#pointsTableModal",
            text <-- I18n(RankingPointsTableToken)
          ),
          "."
        ),
        div(
          cls := "modal fade",
          idAttr := "pointsTableModal",
          tabIndex := -1,
          div(
            cls := "modal-dialog",
            div(
              cls := "modal-content",
              div(
                cls := "modal-header",
                h1(cls := "modal-title fs-5", text <-- I18n(PointsTableTitleToken))
              ),
              div(
                cls := "modal-body",
                Table[Scoring](vScorings, List(
                  Column(PointsTableFinalStandingToken, 1),
                  Column(RankingPointsToken, 2)
                ))
              ),
              div(
                cls := "modal-footer",
                button(
                  cls := "btn btn-primary",
                  typ := "button",
                  dataAttr("bs-dismiss") := "modal",
                  text <-- I18n(PointsTableDismissButtonToken)
                )
              )
            )
          )
        )
      )
    )

end RankingTabContent