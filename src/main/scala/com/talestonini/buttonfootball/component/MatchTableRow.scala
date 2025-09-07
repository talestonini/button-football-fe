package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.model.Matches.*
import com.talestonini.buttonfootball.service.*
import com.talestonini.buttonfootball.util.Logo.*
import com.talestonini.buttonfootball.util.*
import com.talestonini.util.*

object MatchTableRow:

  def apply(m: Match, isFinalsStage: Boolean = false): Element =
    def displayInFinals(condition: Boolean) =
      display(if (isFinalsStage && condition) "table-cell" else "none")

    def logo(logoImgFile: String): Element = {
      td(cls := "col-1", LogoImage(forTeamImgFile(logoImgFile)))
    }

    tr(
      if (isFinalsStage)
        ""
      else
        td(cls := "col-3 text-end", buildStyleAttr(maybeScaleFontDown()), text <-- I18n(m.teamA, TeamTranslationMap)),
      logo(m.teamALogoImgFile),
      td(
        cls := "col-1", buildStyleAttr(maybeScaleFontUp(), "font-weight: bold"),
        table(
          cls := "table align-middle mb-0 text-center",
          tr(td(m.numGoalsTeamA)),
          tr(td(m.numGoalsExtraA, displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB))),
          tr(td(
            m.numGoalsPntA,
            displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB && m.numGoalsExtraA == m.numGoalsExtraB)
          ))
        )
      ),
      td(
        cls := "col-2", buildStyleAttr(maybeScaleFontUp(), "font-weight: bold"),
        " x "
      ),
      td(
        cls := "col-1", buildStyleAttr(maybeScaleFontUp(), "font-weight: bold"),
        table(
          cls := "table align-middle mb-0 text-center",
          tr(td(m.numGoalsTeamB)),
          tr(td(m.numGoalsExtraB, displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB))),
          tr(td(
            m.numGoalsPntB,
            displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB && m.numGoalsExtraA == m.numGoalsExtraB)
          ))
        )
      ),
      logo(m.teamBLogoImgFile),
      if (isFinalsStage)
        ""
      else
        td(cls := "col-3 text-start", buildStyleAttr(maybeScaleFontDown()), text <-- I18n(m.teamB, TeamTranslationMap))
    )
  end apply 

end MatchTableRow