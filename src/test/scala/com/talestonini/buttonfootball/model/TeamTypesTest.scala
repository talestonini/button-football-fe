package com.talestonini.buttonfootball.model

import com.talestonini.buttonfootball.model.TeamTypes.*
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.*
import io.circe.syntax.*

class TeamTypesTest extends munit.FunSuite:

  // Circe codecs for testing
  implicit val teamTypeDecoder: Decoder[TeamType] = deriveDecoder[TeamType]
  implicit val teamTypeEncoder: Encoder[TeamType] = deriveEncoder[TeamType]

  // --- Test Fixtures (Real Data from API) -----------------------------------------------------------------------
  
  val clubeJson = """{"id":1,"code":"c","description":"Clube"}"""
  val selecaoJson = """{"id":2,"code":"s","description":"Seleção"}"""
  
  val allTeamTypesJson = """[
    {"id":1,"code":"c","description":"Clube"},
    {"id":2,"code":"s","description":"Seleção"}
  ]"""

  val clube = TeamType(1, "c", "Clube")
  val selecao = TeamType(2, "s", "Seleção")

  // --- Case Class Tests ------------------------------------------------------------------------------------------

  test("TeamType - create instance with valid data") {
    val teamType = TeamType(1, "test", "Test Team Type")
    
    assertEquals(teamType.id, 1)
    assertEquals(teamType.code, "test")
    assertEquals(teamType.description, "Test Team Type")
  }

  test("TeamType - equality works correctly") {
    val tt1 = TeamType(1, "c", "Clube")
    val tt2 = TeamType(1, "c", "Clube")
    val tt3 = TeamType(2, "s", "Seleção")
    
    assertEquals(tt1, tt2)
    assertNotEquals(tt1, tt3)
  }

  test("TeamType - copy works correctly") {
    val original = TeamType(1, "test", "Test")
    val copied = original.copy(description = "Modified Test")
    
    assertEquals(copied.id, 1)
    assertEquals(copied.code, "test")
    assertEquals(copied.description, "Modified Test")
    assertNotEquals(original, copied)
  }

  test("TeamType - implements Model trait") {
    val teamType: Model = TeamType(1, "test", "Test")
    assert(teamType.isInstanceOf[Product])
  }

  // --- NO_TEAM_TYPE Constant Tests -------------------------------------------------------------------------------

  test("NO_TEAM_TYPE - has correct default values") {
    assertEquals(NO_TEAM_TYPE.id, -1)
    assertEquals(NO_TEAM_TYPE.code, "-")
    assertEquals(NO_TEAM_TYPE.description, "-")
  }

  test("NO_TEAM_TYPE - can be used as sentinel value") {
    val validType = TeamType(1, "test", "Test")
    
    assertNotEquals(validType, NO_TEAM_TYPE)
    assert(validType.id != NO_TEAM_TYPE.id)
  }

  test("NO_TEAM_TYPE - is a valid TeamType instance") {
    val noType: TeamType = NO_TEAM_TYPE
    assert(noType.isInstanceOf[TeamType])
  }

  // --- JSON Deserialization Tests (Single Object) ----------------------------------------------------------------

  test("JSON decode - Clube from real API data") {
    val result = decode[TeamType](clubeJson)
    
    assert(result.isRight)
    result.foreach { tt =>
      assertEquals(tt.id, 1)
      assertEquals(tt.code, "c")
      assertEquals(tt.description, "Clube")
    }
  }

  test("JSON decode - Seleção from real API data") {
    val result = decode[TeamType](selecaoJson)
    
    assert(result.isRight)
    result.foreach { tt =>
      assertEquals(tt.id, 2)
      assertEquals(tt.code, "s")
      assertEquals(tt.description, "Seleção")
    }
  }

  test("JSON decode - handles malformed JSON gracefully") {
    val malformedJson = """{"id":"not-a-number","code":"test"}"""
    val result = decode[TeamType](malformedJson)
    
    assert(result.isLeft)
  }

  test("JSON decode - handles missing required fields") {
    val incompleteJson = """{"id":1,"code":"test"}"""
    val result = decode[TeamType](incompleteJson)
    
    assert(result.isLeft)
  }

  // --- JSON Deserialization Tests (List) -------------------------------------------------------------------------

  test("JSON decode - list of all team types from real API data") {
    val result = decode[List[TeamType]](allTeamTypesJson)
    
    assert(result.isRight)
    result.foreach { list =>
      assertEquals(list.length, 2)
      
      // Verify specific entries
      assert(list.exists(tt => tt.code == "c" && tt.description == "Clube"))
      assert(list.exists(tt => tt.code == "s" && tt.description == "Seleção"))
    }
  }

  test("JSON decode - empty list") {
    val emptyJson = """[]"""
    val result = decode[List[TeamType]](emptyJson)
    
    assert(result.isRight)
    result.foreach { list =>
      assertEquals(list.length, 0)
    }
  }

  test("JSON decode - list with single element") {
    val singleJson = """[{"id":1,"code":"test","description":"Test"}]"""
    val result = decode[List[TeamType]](singleJson)
    
    assert(result.isRight)
    result.foreach { list =>
      assertEquals(list.length, 1)
      assertEquals(list.head.code, "test")
    }
  }

  // --- JSON Serialization Tests (Encoding) -----------------------------------------------------------------------

  test("JSON encode - single TeamType") {
    val json = clube.asJson.noSpaces
    
    assert(json.contains("\"id\":1"))
    assert(json.contains("\"code\":\"c\""))
    assert(json.contains("\"description\":\"Clube\""))
  }

  test("JSON encode - NO_TEAM_TYPE") {
    val json = NO_TEAM_TYPE.asJson.noSpaces
    
    assert(json.contains("\"id\":-1"))
    assert(json.contains("\"code\":\"-\""))
    assert(json.contains("\"description\":\"-\""))
  }

  test("JSON encode - list of TeamTypes") {
    val list = List(clube, selecao)
    val json = list.asJson.noSpaces
    
    assert(json.startsWith("["))
    assert(json.endsWith("]"))
    assert(json.contains("\"code\":\"c\""))
    assert(json.contains("\"code\":\"s\""))
  }

  test("JSON encode - empty list") {
    val emptyList: List[TeamType] = List.empty
    val json = emptyList.asJson.noSpaces
    
    assertEquals(json, "[]")
  }

  // --- Round-trip Tests (Encode then Decode) ---------------------------------------------------------------------

  test("JSON round-trip - single TeamType") {
    val original = clube
    val json = original.asJson.noSpaces
    val decoded = decode[TeamType](json)
    
    assert(decoded.isRight)
    decoded.foreach { tt =>
      assertEquals(tt, original)
    }
  }

  test("JSON round-trip - NO_TEAM_TYPE") {
    val original = NO_TEAM_TYPE
    val json = original.asJson.noSpaces
    val decoded = decode[TeamType](json)
    
    assert(decoded.isRight)
    decoded.foreach { tt =>
      assertEquals(tt, original)
    }
  }

  test("JSON round-trip - list of all team types") {
    val original = List(clube, selecao)
    val json = original.asJson.noSpaces
    val decoded = decode[List[TeamType]](json)
    
    assert(decoded.isRight)
    decoded.foreach { list =>
      assertEquals(list.length, original.length)
      assertEquals(list, original)
    }
  }

  // --- Integration-style Tests with Real Data -------------------------------------------------------------------

  test("Real data - all team types are unique by id") {
    val result = decode[List[TeamType]](allTeamTypesJson)
    
    result.foreach { list =>
      val ids = list.map(_.id)
      assertEquals(ids.distinct.length, ids.length, "All IDs should be unique")
    }
  }

  test("Real data - all team types are unique by code") {
    val result = decode[List[TeamType]](allTeamTypesJson)
    
    result.foreach { list =>
      val codes = list.map(_.code)
      assertEquals(codes.distinct.length, codes.length, "All codes should be unique")
    }
  }

  test("Real data - all team types have non-empty descriptions") {
    val result = decode[List[TeamType]](allTeamTypesJson)
    
    result.foreach { list =>
      list.foreach { tt =>
        assert(tt.description.nonEmpty, s"Description should not be empty for ${tt.code}")
        assert(tt.description != "-", s"Description should not be default value for ${tt.code}")
      }
    }
  }

  test("Real data - IDs are positive integers") {
    val result = decode[List[TeamType]](allTeamTypesJson)
    
    result.foreach { list =>
      list.foreach { tt =>
        assert(tt.id > 0, s"ID should be positive for ${tt.code}")
      }
    }
  }

  test("Real data - codes are single characters") {
    val result = decode[List[TeamType]](allTeamTypesJson)
    
    result.foreach { list =>
      list.foreach { tt =>
        assertEquals(tt.code.length, 1, s"Code should be single character for ${tt.description}")
      }
    }
  }

end TeamTypesTest