package com.talestonini.buttonfootball

import com.raquo.airstream.state.Var
import com.talestonini.buttonfootball.model.Teams.Team

package object model {
  val teamName: Var[String] = Var("")
  val teams: Var[List[Team]] = Var(List.empty)
}