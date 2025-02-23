package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.model.Matches.Match

object MatchElement:

  def apply(m: Match, isFinalsStage: Boolean = false): Element =
    def displayInFinals(condition: Boolean) = display(if (isFinalsStage && condition) "table-cell" else "none")
    tr(
      td(cls := "col text-end", styleAttr := "width: 200px;", m.teamA),
      td(cls := "col-auto text-center", m.numGoalsPntA,
        displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB && m.numGoalsExtraA == m.numGoalsExtraB)
      ),
      td(cls := "col-auto text-center", m.numGoalsExtraA, displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB)),
      td(cls := "col-auto text-center", m.numGoalsTeamA),
      td(cls := "col-auto text-center", " x "),
      td(cls := "col-auto text-center", m.numGoalsTeamB),
      td(cls := "col-auto text-center", m.numGoalsExtraB, displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB)),
      td(cls := "col-auto text-center", m.numGoalsPntB,
        displayInFinals(m.numGoalsTeamA == m.numGoalsTeamB && m.numGoalsExtraA == m.numGoalsExtraB)
      ),
      td(cls := "col text-start", styleAttr := "width: 200px;", m.teamB)
    )
  end apply 

end MatchElement