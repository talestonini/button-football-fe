package com.talestonini.buttonfootball

import cats.effect.unsafe.implicits.global
import com.raquo.airstream.state.Var
import com.talestonini.buttonfootball.model.Championships.*
import com.talestonini.buttonfootball.model.ChampionshipTypes.*
import com.talestonini.buttonfootball.model.Matches.*
import com.talestonini.buttonfootball.model.Standings.*
import com.talestonini.buttonfootball.model.Teams.*
import com.talestonini.buttonfootball.model.TeamTypes.*
import com.talestonini.buttonfootball.service.*
import com.talestonini.buttonfootball.service.ChampionshipService.calcNumQualif
import com.raquo.airstream.core.Signal
import scala.scalajs.concurrent.JSExecutionContext.queue
import scala.util.{Failure, Success}

package object model:

  type Id   = Int
  type Code = String

  trait Model extends Product

  // --- constants -----------------------------------------------------------------------------------------------------

  val NO_CHAMPIONSHIP_EDITION  = 0
  val MIN_CHAMPIONSHIP_EDITION = 1
  val NUM_TEAMS_PER_GROUP      = 4

  val GROUP         = "Grupo"
  val FIRST_TAB     = s"$GROUP A"
  val FINALS_TAB    = "Finais"
  val LAST_TAB      = FINALS_TAB
  val NO_ACTIVE_TAB = "(no active tab)"

  // --- state ---------------------------------------------------------------------------------------------------------

  val teamTypes: Var[List[TeamType]] = Var(List.empty)
  val selectedTeamType: Var[Option[TeamType]] = Var(None)

  val championshipTypes: Var[List[ChampionshipType]] = Var(List.empty)
  val selectedChampionshipType: Var[Option[ChampionshipType]] = Var(None)

  val championships: Var[List[Championship]] = Var(List.empty)
  val selectedChampionship: Var[Option[Championship]] = Var(None)

  private val groupFilterFn = (matchType: String) => matchType.startsWith(GROUP)

  val matches: Var[List[Match]] = Var(List.empty)
  val groupsMatches: Signal[List[Match]] = matches.signal.map(ms => ms.filter(m => groupFilterFn(m.`type`)))
  val finalsMatches: Signal[List[Match]] = matches.signal.map(ms => ms.filter(m => !groupFilterFn(m.`type`)))
  val groups: Signal[List[String]] = groupsMatches.map(gms => gms.map(_.`type`).distinct)
  val numTeams: Signal[Int] = groups.signal.map(gm => gm.length * NUM_TEAMS_PER_GROUP)
  val numFinalsMatches: Signal[Int] = finalsMatches.map(fms => fms.length)
  val tabs: Signal[List[String]] = groups.signal.map(gs => gs :+ FINALS_TAB)
  val activeTab: Var[String] = Var(NO_ACTIVE_TAB)
  val numQualif: Signal[Int] = numTeams.map(nt => calcNumQualif(nt).getOrElse(0))
  val groupsStandings: Var[List[Standing]] = Var(List.empty)
  
  case class Qualified(pos: Int, team: String)
  val qualifiedTeams: Signal[List[Qualified]] = groupsStandings.signal.combineWith(numQualif).map { 
    case (gss, nq) => gss.filter(gs => gs.numExtraGrpPos.isDefined && gs.numExtraGrpPos.get <= nq)
      .map(gs => Qualified(gs.numExtraGrpPos.get, gs.team))
  }

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
          println(s"failed fetching team types: ${f.exception.getMessage}")
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
          println(s"failed fetching championship type: ${f.exception.getMessage}")
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
            val edition = if (championships.now().nonEmpty) s.value.length else NO_CHAMPIONSHIP_EDITION
            championships.now().find(_.numEdition == edition)
          )
          seGetMatches(selectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
          seGetGroupStandings(selectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
        }
        case f: Failure[List[Championship]] => {
          println(s"failed fetching championships: ${f.exception.getMessage}")
          championships.update(_ => List.empty)
          selectedChampionship.update(_ => None)
          matches.update(_ => List.empty)
        }
      })(queue)
  end seGetChampionships
  
  def seGetMatches(championshipId: Id): Unit =
    println(s"fetching matches with championship id '$championshipId'")
    ChampionshipService.getMatches(championshipId)
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[Match]] =>
          matches.update(_ => s.value)
          activeTab.update(_ => FIRST_TAB)
        case f: Failure[List[Match]] => {
          println(s"failed fetching matches: ${f.exception.getMessage}")
          matches.update(_ => List.empty)
          activeTab.update(_ => NO_ACTIVE_TAB)
        }
      })(queue)
  end seGetMatches

  def seGetGroupStandings(championshipId: Id): Unit =
    println(s"fetching group standings with championship id '$championshipId'")
    ChampionshipService.getGroupStandings(championshipId)
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[Standing]] =>
          groupsStandings.update(_ => s.value)
        case f: Failure[List[Standing]] => {
          println(s"failed fetching matches: ${f.exception.getMessage}")
          groupsStandings.update(_ => List.empty)
        }
      })(queue)
  end seGetGroupStandings

  def seGetTeams(name: String): Unit =
    println(s"fetching team with name '$name'")
    TeamService
      .getTeams(if (name.isBlank()) None else Some(name.trim()))
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[Team]] => teams.update(_ => s.value)
        case f: Failure[List[Team]] => println(s"failed fetching team: ${f.exception.getMessage}")
      })(queue)
  end seGetTeams

end model