package com.talestonini.buttonfootball

import com.raquo.airstream.state.Var
import com.talestonini.buttonfootball.model.Championships.Championship
import com.talestonini.buttonfootball.model.ChampionshipTypes.ChampionshipType
import com.talestonini.buttonfootball.model.Teams.Team
import com.talestonini.buttonfootball.model.TeamTypes.TeamType

package object model:

  type Id = Int
  type Code = String

  trait Model extends Product

  // --- constants -----------------------------------------------------------------------------------------------------

  val NO_CODE = ""
  val NO_CHAMPIONSHIP_EDITION = "0"
  val MIN_CHAMPIONSHIP_EDITION = "1"

  // --- data model per-se ---------------------------------------------------------------------------------------------

  val teamTypes: Var[List[TeamType]] = Var(List.empty)
  val selectedTeamTypeCode: Var[Code] = Var(NO_CODE)
  val selectedTeamType: Var[Option[TeamType]] = Var(None)

  val championshipTypes: Var[List[ChampionshipType]] = Var(List.empty)
  val selectedChampionshipTypeCode: Var[Code] = Var(NO_CODE)
  val selectedChampionshipType: Var[Option[ChampionshipType]] = Var(None)

  val championships: Var[List[Championship]] = Var(List.empty)
  val selectedChampionshipEdition: Var[String] = Var(NO_CHAMPIONSHIP_EDITION)
  val selectedChampionship: Var[Option[Championship]] = Var(None)

  val teams: Var[List[Team]] = Var(List.empty)
  val teamName: Var[String] = Var("")

end model