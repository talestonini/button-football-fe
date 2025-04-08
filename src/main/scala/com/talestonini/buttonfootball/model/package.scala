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

  val GROUP               = "Grupo"
  val FINALS_TAB          = "Finais"
  val FINAL_STANDINGS_TAB = "Classificação"
  val FIRST_TAB           = s"$GROUP A"
  val LAST_TAB            = FINAL_STANDINGS_TAB
  val NO_ACTIVE_TAB       = "(no active tab)"

  // --- state ---------------------------------------------------------------------------------------------------------

  val vTeamTypes: Var[List[TeamType]] = Var(List.empty)
  val vSelectedTeamType: Var[Option[TeamType]] = Var(None)

  val vChampionshipTypes: Var[List[ChampionshipType]] = Var(List.empty)
  val vSelectedChampionshipType: Var[Option[ChampionshipType]] = Var(None)

  val vChampionships: Var[List[Championship]] = Var(List.empty)
  val vSelectedChampionship: Var[Option[Championship]] = Var(None)
  val vSelectedEdition: Var[Int] = Var(NO_CHAMPIONSHIP_EDITION)

  private val groupFilterFn = (matchType: String) => matchType.startsWith(GROUP)

  val vMatches: Var[List[Match]] = Var(List.empty)
  val sGroupsMatches: Signal[List[Match]] = vMatches.signal.map(ms => ms.filter(m => groupFilterFn(m.`type`)))
  val sFinalsMatches: Signal[List[Match]] = vMatches.signal.map(ms => ms.filter(m => !groupFilterFn(m.`type`)))
  val sGroups: Signal[List[String]] = sGroupsMatches.map(gms => gms.map(_.`type`).distinct)
  val sNumTeams: Signal[Int] = sGroups.signal.map(gm => gm.length * NUM_TEAMS_PER_GROUP)
  val sNumFinalsMatches: Signal[Int] = sFinalsMatches.map(fms => fms.length)
  val sTabs: Signal[List[String]] = sGroups.signal.map(gs => gs :+ FINALS_TAB :+ FINAL_STANDINGS_TAB)
  val vActiveTab: Var[String] = Var(NO_ACTIVE_TAB)
  val sNumQualif: Signal[Int] = sNumTeams.map(nt => calcNumQualif(nt).getOrElse(0))
  val vGroupStandings: Var[List[Standing]] = Var(List.empty)
  val vFinalStandings: Var[List[Standing]] = Var(List.empty)
  
  case class Qualified(pos: Int, team: String)
  val sQualifiedTeams: Signal[List[Qualified]] = vGroupStandings.signal.combineWith(sNumQualif).map { 
    case (gss, nq) => gss.filter(gs => gs.numExtraGrpPos.isDefined && gs.numExtraGrpPos.get <= nq)
      .map(gs => Qualified(gs.numExtraGrpPos.get, gs.team))
  }

  val vTeams: Var[List[Team]] = Var(List.empty)

  val vIsLoading: Var[Boolean] = Var(false)
  def setLoading() = vIsLoading.update(_ => true)
  def unsetLoading() = vIsLoading.update(_ => false)

  // --- side-effect functions -------------------------------------------------------------------------------------------
  
  def seGetTeamTypes(): Unit =
    setLoading()
    println(s"fetching team types")
    TeamTypeService.getTeamTypes()
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[TeamType]] =>
          vTeamTypes.update(_ => s.value)
          vSelectedTeamType.update(_ => Some(s.value.head))
          seGetChampionshipTypes(s.value.head.code)
          unsetLoading()
        case f: Failure[List[TeamType]] =>
          println(s"failed fetching team types: ${f.exception.getMessage}")
          vTeamTypes.update(_ => List.empty)
          vSelectedTeamType.update(_ => None)
          unsetLoading()
      })(queue)
  end seGetTeamTypes
  
  def seGetChampionshipTypes(codTeamType: String): Unit =
    setLoading()
    println(s"fetching championship types with team type code '$codTeamType'")
    ChampionshipTypeService.getChampionshipTypes(Some(codTeamType))
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[ChampionshipType]] =>
          vChampionshipTypes.update(_ => s.value)
          vSelectedChampionshipType.update(_ => Some(s.value.head))
          seGetChampionships(s.value.head.code)
          unsetLoading()
        case f: Failure[List[ChampionshipType]] =>
          println(s"failed fetching championship type: ${f.exception.getMessage}")
          vChampionshipTypes.update(_ => List.empty)
          vSelectedChampionshipType.update(_ => None)
          unsetLoading()
      })(queue)
  end seGetChampionshipTypes
  
  def seGetChampionships(codChampionshipType: String): Unit =
    setLoading()
    println(s"fetching championships with championship type code '$codChampionshipType'")
    ChampionshipService.getChampionships(Some(codChampionshipType))
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[Championship]] =>
          vChampionships.update(_ => s.value)
          vSelectedChampionship.update(_ => 
            val editionToUpdateWith =
              if (vChampionships.now().isEmpty) NO_CHAMPIONSHIP_EDITION
              else
                if (vSelectedEdition.now() == NO_CHAMPIONSHIP_EDITION) s.value.length
                else vSelectedEdition.now()
            vChampionships.now().find(_.numEdition == editionToUpdateWith)
          )
          seGetMatches(vSelectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
          // TODO: check whether using 2 APIs is a cleaner design - this is currently not working as the following
          //       quick succession of requests result in backend ConcurrentModificationException
          // seGetGroupStandings(selectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
          // seGetFinalStandings(selectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
          seGetStandings(vSelectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
          unsetLoading()
        case f: Failure[List[Championship]] =>
          println(s"failed fetching championships: ${f.exception.getMessage}")
          vChampionships.update(_ => List.empty)
          vSelectedChampionship.update(_ => None)
          vMatches.update(_ => List.empty)
          vGroupStandings.update(_ => List.empty)
          vFinalStandings.update(_ => List.empty)
          unsetLoading()
      })(queue)
  end seGetChampionships
  
  def seGetMatches(championshipId: Id): Unit =
    setLoading()
    println(s"fetching matches with championship id '$championshipId'")
    ChampionshipService.getMatches(championshipId)
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[Match]] =>
          vMatches.update(_ => s.value)
          vActiveTab.update(_ => 
            if (vMatches.now().isEmpty) NO_ACTIVE_TAB
            else
              if (vActiveTab.now().startsWith(GROUP)) s"$GROUP A"
              else if (vActiveTab.now() == FINALS_TAB) FINALS_TAB
              else vSelectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).status match {
                case "Primeira Fase" => s"$GROUP A"
                case "Encerrado"     => FINAL_STANDINGS_TAB
                case _               => FINALS_TAB
              }
          )
          unsetLoading()
        case f: Failure[List[Match]] =>
          println(s"failed fetching matches: ${f.exception.getMessage}")
          vMatches.update(_ => List.empty)
          vActiveTab.update(_ => NO_ACTIVE_TAB)
          unsetLoading()
      })(queue)
  end seGetMatches

  def seGetStandings(championshipId: Id): Unit =
    setLoading()
    println(s"fetching standings with championship id '$championshipId'")
    ChampionshipService.getStandings(championshipId)
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[Standing]] =>
          vGroupStandings.update(_ => s.value.filter(st => st.`type`.startsWith(GROUP)))
          vFinalStandings.update(_ => s.value.filter(st => !st.`type`.startsWith(GROUP)))
          unsetLoading()
        case f: Failure[List[Standing]] =>
          println(s"failed fetching standings: ${f.exception.getMessage}")
          vGroupStandings.update(_ => List.empty)
          vFinalStandings.update(_ => List.empty)
          unsetLoading()
      })(queue)
  end seGetStandings

  def seGetGroupStandings(championshipId: Id): Unit =
    setLoading()
    println(s"fetching group standings with championship id '$championshipId'")
    ChampionshipService.getGroupStandings(championshipId)
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[Standing]] =>
          vGroupStandings.update(_ => s.value)
          unsetLoading()
        case f: Failure[List[Standing]] =>
          println(s"failed fetching group standings: ${f.exception.getMessage}")
          vGroupStandings.update(_ => List.empty)
          unsetLoading()
      })(queue)
  end seGetGroupStandings

  def seGetFinalStandings(championshipId: Id): Unit =
    setLoading()
    println(s"fetching final standings with championship id '$championshipId'")
    ChampionshipService.getFinalStandings(championshipId)
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[Standing]] =>
          vFinalStandings.update(_ => s.value)
          unsetLoading()
        case f: Failure[List[Standing]] =>
          println(s"failed fetching final standings: ${f.exception.getMessage}")
          vFinalStandings.update(_ => List.empty)
          unsetLoading()
      })(queue)
  end seGetFinalStandings

  def seGetTeams(name: String = ""): Unit =
    setLoading()
    println(s"fetching team with name '$name'")
    TeamService
      .getTeams(if (name.isBlank()) None else Some(name.trim()))
      .unsafeToFuture()
      .onComplete({
        case s: Success[List[Team]] =>
          vTeams.update(_ => s.value)
          unsetLoading()
        case f: Failure[List[Team]] =>
          println(s"failed fetching team: ${f.exception.getMessage}")
          unsetLoading()
      })(queue)
  end seGetTeams

end model