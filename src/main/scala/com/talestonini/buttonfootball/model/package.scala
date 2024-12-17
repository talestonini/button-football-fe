package com.talestonini.buttonfootball

import com.raquo.airstream.state.Var
import com.talestonini.buttonfootball.model.Teams.Team
import com.talestonini.buttonfootball.model.TeamTypes.TeamType
import com.talestonini.buttonfootball.model.ChampionshipTypes.ChampionshipType
import com.talestonini.buttonfootball.model.Championships.Championship

package object model:

  type Id = Int
  type Code = String

  trait Model extends Product

  // --- data model per-se ---------------------------------------------------------------------------------------------

  val teamTypes: Var[List[TeamType]] = Var(List.empty)
  val selectedTeamType: Var[Code] = Var("")

  val championshipTypes: Var[List[ChampionshipType]] = Var(List.empty)
  val selectedChampionshipType: Var[Code] = Var("")

  val championships: Var[List[Championship]] = Var(List.empty)
  val selectedChampionshipEdition: Var[Int] = Var(0)

  val teams: Var[List[Team]] = Var(List.empty)
  val teamName: Var[String] = Var("")

end model