package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.model.Matches.Match
import com.talestonini.buttonfootball.Logo

object MatchElement:

  def apply(m: Match, isFinalsStage: Boolean = false): Element =
    def displayInFinals(condition: Boolean) = display(if (isFinalsStage && condition) "table-cell" else "none")
    def logo(logoImgFile: String): Element = td(cls := "img-container-40", img(src := Logo.forTeam(logoImgFile)))

    tr(
      cls := "text-center",
      if (isFinalsStage) "" else td(cls := "col text-end", styleAttr := "width: 200px;", m.teamA),
      logo(m.teamALogoImgFile),
      td(
        table(
          cls := "table",
          styleAttr := "vertical-align: middle; margin-bottom: 0",
          tr(
            cls := "text-center",
            td(cls := "text-center", m.numGoalsTeamA)
          ),
          tr(
            cls := "text-center",
            td(cls := "text-center", m.numGoalsExtraA, displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB))
          ),
          tr(
            cls := "text-center",
            td(cls := "text-center", m.numGoalsPntA,
              displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB && m.numGoalsExtraA == m.numGoalsExtraB)
            )
          )
        )
      ),
      td(" x "),
      td(
        table(
          cls := "table",
          styleAttr := "vertical-align: middle; margin-bottom: 0",
          tr(
            cls := "text-center",
            td(cls := "text-center", m.numGoalsTeamB)
          ),
          tr(
            cls := "text-center",
            td(cls := "text-center", m.numGoalsExtraB, displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB))
          ),
          tr(
            cls := "text-center",
            td(cls := "text-center", m.numGoalsPntB,
              displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB && m.numGoalsExtraA == m.numGoalsExtraB)
            )
          ),
        )
      ),
      logo(m.teamBLogoImgFile),
      if (isFinalsStage) "" else td(cls := "col text-start", styleAttr := "width: 200px;", m.teamB)
    )
  end apply 

end MatchElement