package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.model.Matches.Match
import com.talestonini.buttonfootball.util.buildStyleAttr
import com.talestonini.buttonfootball.util.Logo
import com.talestonini.buttonfootball.util.scaleFont

object MatchElement:

  def apply(m: Match, isFinalsStage: Boolean = false): Element =
    def displayInFinals(condition: Boolean) = display(if (isFinalsStage && condition) "table-cell" else "none")
    def logo(logoImgFile: String): Element = td(img(cls := "logo-40", src := Logo.forTeam(logoImgFile)))

    tr(
      if (isFinalsStage) "" else td(cls := "col text-end", buildStyleAttr(scaleFont()), m.teamA),
      logo(m.teamALogoImgFile),
      td(table(
        cls := "table align-middle mb-0 text-center",
        tr(td(m.numGoalsTeamA)),
        tr(td(m.numGoalsExtraA, displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB))),
        tr(td(
          m.numGoalsPntA,
          displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB && m.numGoalsExtraA == m.numGoalsExtraB)
        ))
      )),
      td(" x "),
      td(table(
        cls := "table align-middle mb-0 text-center",
        tr(td(m.numGoalsTeamB)),
        tr(td(m.numGoalsExtraB, displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB))),
        tr(td(
          m.numGoalsPntB,
          displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB && m.numGoalsExtraA == m.numGoalsExtraB)
        ))
      )),
      logo(m.teamBLogoImgFile),
      if (isFinalsStage) "" else td(cls := "col text-start", buildStyleAttr(scaleFont()), m.teamB)
    )
  end apply 

end MatchElement