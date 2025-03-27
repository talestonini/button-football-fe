package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.model.Matches.Match
import com.talestonini.buttonfootball.util.buildStyleAttr
import com.talestonini.buttonfootball.util.Logo.*
import com.talestonini.buttonfootball.util.scaleFont
import com.talestonini.buttonfootball.util.Window
import com.talestonini.buttonfootball.util.Window.Size

object MatchTableRow:

  def apply(m: Match, isFinalsStage: Boolean = false): Element =
    def displayInFinals(condition: Boolean) =
      display(if (isFinalsStage && condition) "table-cell" else "none")

    def logo(logoImgFile: String): Element = {
      val size = Window.size() match {
        case Size.Small  => SMALL_TEAM_LOGO_PX_SIZE
        case Size.Medium => MEDIUM_TEAM_LOGO_PX_SIZE
        case Size.Large  => LARGE_TEAM_LOGO_PX_SIZE
      }
      td(cls := "col-1", LogoImage(forTeamImgFile(logoImgFile), size))
    }

    tr(
      if (isFinalsStage) "" else td(cls := "col-3 text-end", buildStyleAttr(scaleFont()), m.teamA),
      logo(m.teamALogoImgFile),
      td(cls := "col-1", table(
        cls := "table align-middle mb-0 text-center",
        tr(td(m.numGoalsTeamA)),
        tr(td(m.numGoalsExtraA, displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB))),
        tr(td(
          m.numGoalsPntA,
          displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB && m.numGoalsExtraA == m.numGoalsExtraB)
        ))
      )),
      td(cls := "col-2", " x "),
      td(cls := "col-1", table(
        cls := "table align-middle mb-0 text-center",
        tr(td(m.numGoalsTeamB)),
        tr(td(m.numGoalsExtraB, displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB))),
        tr(td(
          m.numGoalsPntB,
          displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB && m.numGoalsExtraA == m.numGoalsExtraB)
        ))
      )),
      logo(m.teamBLogoImgFile),
      if (isFinalsStage) "" else td(cls := "col-3 text-start", buildStyleAttr(scaleFont()), m.teamB)
    )
  end apply 

end MatchTableRow