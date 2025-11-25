package com.talestonini.buttonfootball

import munit.FunSuite
import io.circe.parser.decode
import io.circe.syntax._
import com.talestonini.buttonfootball.model.TeamTypes
import com.talestonini.buttonfootball.model.TeamTypes.{TeamType, NO_TEAM_TYPE}
import cats.effect.IO
import io.circe.generic.auto._

class TeamTypesTest extends FunSuite:

  test("TeamType case class should be properly constructed") {
    val teamType = TeamType(1, "TEST", "Test Team Type")
    
    assertEquals(teamType.id, 1)
    assertEquals(teamType.code, "TEST")
    assertEquals(teamType.description, "Test Team Type")
  }

  test("NO_TEAM_TYPE should have correct default values") {
    assertEquals(NO_TEAM_TYPE.id, -1)
    assertEquals(NO_TEAM_TYPE.code, "-")
    assertEquals(NO_TEAM_TYPE.description, "-")
  }

  test("TeamType should encode to JSON correctly") {
    val teamType = TeamType(1, "TEST", "Test Team Type")
    
    val json = teamType.asJson.noSpaces
    
    // Check that the JSON contains all expected fields
    assert(json.contains("\"id\":1"))
    assert(json.contains("\"code\":\"TEST\""))
    assert(json.contains("\"description\":\"Test Team Type\""))
  }

  test("TeamType should decode from JSON correctly") {
    val json = """{"id": 2, "code": "REG", "description": "Regular Team Type"}"""
    
    val result = decode[TeamType](json)
    
    assert(result.isRight)
    val teamType = result.toOption.get
    
    assertEquals(teamType.id, 2)
    assertEquals(teamType.code, "REG")
    assertEquals(teamType.description, "Regular Team Type")
  }

  test("NO_TEAM_TYPE should encode to JSON correctly") {
    val json = NO_TEAM_TYPE.asJson.noSpaces
    
    // Check that the JSON contains all expected fields
    assert(json.contains("\"id\":-1"))
    assert(json.contains("\"code\":\"-\""))
    assert(json.contains("\"description\":\"-\""))
  }

  test("NO_TEAM_TYPE should decode from JSON correctly") {
    val json = """{"id": -1, "code": "-", "description": "-"}"""
    
    val result = decode[TeamType](json)
    
    assert(result.isRight)
    val teamType = result.toOption.get
    
    assertEquals(teamType, NO_TEAM_TYPE)
  }

  test("List of TeamTypes should encode/decode correctly") {
    val teamTypes = List(
      TeamType(1, "TEST", "Test Team Type"),
      TeamType(2, "REG", "Regular Team Type")
    )
    
    val json = teamTypes.asJson.noSpaces
    val result = decode[List[TeamType]](json)
    
    assert(result.isRight)
    val decodedTeamTypes = result.toOption.get
    
    assertEquals(decodedTeamTypes.length, 2)
    assertEquals(decodedTeamTypes(0).id, 1)
    assertEquals(decodedTeamTypes(0).code, "TEST")
    assertEquals(decodedTeamTypes(0).description, "Test Team Type")
    assertEquals(decodedTeamTypes(1).id, 2)
    assertEquals(decodedTeamTypes(1).code, "REG")
    assertEquals(decodedTeamTypes(1).description, "Regular Team Type")
  }

end TeamTypesTest
