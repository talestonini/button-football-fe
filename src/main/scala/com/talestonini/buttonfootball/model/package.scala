package com.talestonini.buttonfootball

import cats.effect.unsafe.implicits.global
import com.raquo.airstream.state.Var
import com.talestonini.buttonfootball.model.Championships.Championship
import com.talestonini.buttonfootball.model.ChampionshipTypes.ChampionshipType
import com.talestonini.buttonfootball.model.Teams.Team
import com.talestonini.buttonfootball.model.TeamTypes.TeamType
import com.talestonini.buttonfootball.service.*
import scala.scalajs.concurrent.JSExecutionContext.queue
import scala.util.{Failure, Success}

package object model:

  // --- types ---------------------------------------------------------------------------------------------------------

  type Id = Int
  type Code = String

  // --- constants -----------------------------------------------------------------------------------------------------

  val NO_CODE = ""
  val NO_CHAMPIONSHIP_EDITION = 0
  val MIN_CHAMPIONSHIP_EDITION = 1

  // --- model ---------------------------------------------------------------------------------------------------------

  trait Model extends Product

  def selectTeamType(teamTypeCode: Code): Unit = {
    selectedTeamType.update(_ => teamTypes.now().find((tt) => tt.code == teamTypeCode))
    seGetChampionshipTypes(teamTypeCode)
  }

  def selectChampionshipType(championshipTypeCode: Code): Unit = {
    selectedChampionshipType.update(_ => championshipTypes.now().find((ct) => ct.code == championshipTypeCode))
    seGetChampionships(championshipTypeCode)
  }

  def selectChampionshipEdition(championshipEdition: String): Unit =
    selectedChampionship.update(_ => championships.now().find((ce) => ce.numEdition == championshipEdition.toInt))

  // --- state ---------------------------------------------------------------------------------------------------------

  val teamTypes: Var[List[TeamType]] = Var(List.empty)
  val selectedTeamType: Var[Option[TeamType]] = Var(None)

  val championshipTypes: Var[List[ChampionshipType]] = Var(List.empty)
  val selectedChampionshipType: Var[Option[ChampionshipType]] = Var(None)

  val championships: Var[List[Championship]] = Var(List.empty)
  val selectedChampionship: Var[Option[Championship]] = Var(None)

  val teams: Var[List[Team]] = Var(List.empty)
  val teamName: Var[String] = Var("")

  // --- side-effect functions -------------------------------------------------------------------------------------------
  
  def seGetTeamTypes(): Unit =
    println(s"fetching team types")
    TeamTypeService
      .getTeamTypes()
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[TeamType]] => {
          teamTypes.update(_ => s.value)
          selectedTeamType.update(_ => Some(s.value.head))
          seGetChampionshipTypes(s.value.head.code)
        }
        case f: Failure[List[TeamType]] => {
          println(s"failed fetching team types: ${f.exception.getMessage()}")
          teamTypes.update(_ => List.empty)
          selectedTeamType.update(_ => None)
        }
      })(queue)
  end seGetTeamTypes
  
  def seGetChampionshipTypes(codTeamType: String): Unit =
    println(s"fetching championship types with team type code '${codTeamType}'")
    ChampionshipTypeService
      .getChampionshipTypes(Some(codTeamType))
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[ChampionshipType]] => {
          championshipTypes.update(_ => s.value)
          selectedChampionshipType.update(_ => Some(s.value.head))
          seGetChampionships(s.value.head.code)
        }
        case f: Failure[List[ChampionshipType]] => {
          println(s"failed fetching championship type: ${f.exception.getMessage()}")
          championshipTypes.update(_ => List.empty)
          selectedChampionshipType.update(_ => None)
        }
      })(queue)
  end seGetChampionshipTypes
  
  def seGetChampionships(codChampionshipType: String): Unit =
    println(s"fetching championships with championship type code '${codChampionshipType}'")
    ChampionshipService
      .getChampionships(Some(codChampionshipType))
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[Championship]] => {
          championships.update(_ => s.value)
          selectedChampionship.update(_ => 
            val edition = if (!championships.now().isEmpty) s.value.length else NO_CHAMPIONSHIP_EDITION
            championships.now().find(_.numEdition == edition)
          )
        }
        case f: Failure[List[Championship]] => {
          println(s"failed fetching championships: ${f.exception.getMessage()}")
          championships.update(_ => List.empty)
          selectedChampionship.update(_ => None)
        }
      })(queue)
  end seGetChampionships
  
  def seGetChampionships(championshipTypeId: Int): Unit =
    println(s"fetching championships with championship type id '${championshipTypeId}'")
    ChampionshipTypeService
      .getChampionships(championshipTypeId)
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[Championship]] => {
          championships.update(_ => s.value)
          selectedChampionship.update(_ => 
            val edition = if (!championships.now().isEmpty) s.value.length else NO_CHAMPIONSHIP_EDITION
            championships.now().find(_.numEdition == edition)
          )
        }
        case f: Failure[List[Championship]] => {
          println(s"failed fetching championships: ${f.exception.getMessage()}")
          championships.update(_ => List.empty)
          selectedChampionship.update(_ => None)
        }
      })(queue)
  end seGetChampionships
  
  def seGetTeams(name: String): Unit =
    println(s"fetching team with name '${name}'")
    TeamService
      .getTeams(if (name.isBlank()) None else Some(name.trim()))
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[Team]] => teams.update(_ => s.value)
        case f: Failure[List[Team]] => println(s"failed fetching team: ${f.exception.getMessage()}")
      })(queue)
  end seGetTeams

end model