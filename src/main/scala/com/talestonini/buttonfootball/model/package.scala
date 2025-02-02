package com.talestonini.buttonfootball

import cats.effect.unsafe.implicits.global
import com.raquo.airstream.state.Var
import com.talestonini.buttonfootball.model.Championships.*
import com.talestonini.buttonfootball.model.ChampionshipTypes.*
import com.talestonini.buttonfootball.model.Matches.*
import com.talestonini.buttonfootball.model.Teams.*
import com.talestonini.buttonfootball.model.TeamTypes.*
import com.talestonini.buttonfootball.service.*
import scala.scalajs.concurrent.JSExecutionContext.queue
import scala.util.{Failure, Success}
import com.raquo.airstream.core.Signal

package object model:

  type Id = Int
  type Code = String

  trait Model extends Product

  // --- state ---------------------------------------------------------------------------------------------------------

  val teamTypes: Var[List[TeamType]] = Var(List.empty)
  val selectedTeamType: Var[Option[TeamType]] = Var(None)

  val championshipTypes: Var[List[ChampionshipType]] = Var(List.empty)
  val selectedChampionshipType: Var[Option[ChampionshipType]] = Var(None)

  val championships: Var[List[Championship]] = Var(List.empty)
  val selectedChampionship: Var[Option[Championship]] = Var(None)

  val matches: Var[List[Match]] = Var(List.empty)
  val groups: Signal[List[String]] = matches.signal.map(ms => ms.map(_.`type`)
    .filter(t => t.startsWith("Grupo")).distinct)

  val teams: Var[List[Team]] = Var(List.empty)
  val teamName: Var[String] = Var("")

  // --- side-effect functions -------------------------------------------------------------------------------------------
  
  def seGetTeamTypes(): Unit =
    println(s"fetching team types")
    TeamTypeService.getTeamTypes()
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
    println(s"fetching championship types with team type code '$codTeamType'")
    ChampionshipTypeService.getChampionshipTypes(Some(codTeamType))
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
    println(s"fetching championships with championship type code '$codChampionshipType'")
    ChampionshipService.getChampionships(Some(codChampionshipType))
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[Championship]] => {
          championships.update(_ => s.value)
          selectedChampionship.update(_ => 
            val edition = if (!championships.now().isEmpty) s.value.length else NO_CHAMPIONSHIP_EDITION
            championships.now().find(_.numEdition == edition)
          )
          seGetMatches(selectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
        }
        case f: Failure[List[Championship]] => {
          println(s"failed fetching championships: ${f.exception.getMessage()}")
          championships.update(_ => List.empty)
          selectedChampionship.update(_ => None)
        }
      })(queue)
  end seGetChampionships
  
  // def seGetChampionships(championshipTypeId: Id): Unit =
  //   println(s"fetching championships with championship type id '$championshipTypeId'")
  //   ChampionshipTypeService.getChampionships(championshipTypeId)
  //     .unsafeToFuture()
  //     .onComplete({
  //       case s: Success[List[Championship]] => {
  //         championships.update(_ => s.value)
  //         selectedChampionship.update(_ => 
  //           val edition = if (!championships.now().isEmpty) s.value.length else NO_CHAMPIONSHIP_EDITION
  //           championships.now().find(_.numEdition == edition)
  //         )
  //         seGetMatches(selectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
  //       }
  //       case f: Failure[List[Championship]] => {
  //         println(s"failed fetching championships: ${f.exception.getMessage()}")
  //         championships.update(_ => List.empty)
  //         selectedChampionship.update(_ => None)
  //       }
  //     })(queue)
  // end seGetChampionships
  
  def seGetMatches(championshipId: Id): Unit =
    println(s"fetching matches with championship id '$championshipId'")
    ChampionshipService.getMatches(championshipId)
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[Match]] =>
          matches.update(_ => s.value)
        case f: Failure[List[Match]] => {
          println(s"failed fetching matches: ${f.exception.getMessage()}")
          matches.update(_ => List.empty)
        }
      })(queue)
  end seGetMatches

  def seGetTeams(name: String): Unit =
    println(s"fetching team with name '$name'")
    TeamService
      .getTeams(if (name.isBlank()) None else Some(name.trim()))
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[Team]] => teams.update(_ => s.value)
        case f: Failure[List[Team]] => println(s"failed fetching team: ${f.exception.getMessage()}")
      })(queue)
  end seGetTeams

end model