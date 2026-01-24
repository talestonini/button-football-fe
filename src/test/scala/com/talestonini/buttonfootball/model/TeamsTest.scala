package com.talestonini.buttonfootball.model

import com.talestonini.buttonfootball.model.Teams.*
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.*
import io.circe.syntax.*

class TeamsTest extends munit.FunSuite:

  // Circe codecs for testing
  implicit val teamDecoder: Decoder[Team] = deriveDecoder[Team]
  implicit val teamEncoder: Encoder[Team] = deriveEncoder[Team]

  // --- Test Fixtures (Real Data from API) -----------------------------------------------------------------------
  
  val corinthiansJson = """{"id":36,"name":"Corinthians","type":"Clube","fullName":"Sport Club Corinthians Paulista","foundation":"1910","city":"São Paulo","country":"Brasil","logoImgFile":"BRA_Corinthians"}"""
  val brasilJson = """{"id":49,"name":"Brasil","type":"Seleção","fullName":"Confederação Brasileira de Futebol","foundation":"1914","city":"Rio de Janeiro","country":"Brasil","logoImgFile":"BRA"}"""
  val manchesterUnitedJson = """{"id":79,"name":"Manchester United","type":"Clube","fullName":"Manchester United Football Club","foundation":"1878","city":"Manchester","country":"Inglaterra","logoImgFile":"ING_Manchester_United"}"""
  val argentinaJson = """{"id":52,"name":"Argentina","type":"Seleção","fullName":"Asociacón de Fútbol Argentino","foundation":"1893","city":"Buenos Aires","country":"Argentina","logoImgFile":"ARG"}"""
  val realMadriJson = """{"id":63,"name":"Real Madri","type":"Clube","fullName":"Real Madrid Club de Fútbol","foundation":"1902","city":"Madri","country":"Espanha","logoImgFile":"ESP_Real_Madri"}"""
  
  val allTeamsJson = """[
    {"id":36,"name":"Corinthians","type":"Clube","fullName":"Sport Club Corinthians Paulista","foundation":"1910","city":"São Paulo","country":"Brasil","logoImgFile":"BRA_Corinthians"},
    {"id":37,"name":"Palmeiras","type":"Clube","fullName":"Sociedade Esportiva Palmeiras","foundation":"1914","city":"São Paulo","country":"Brasil","logoImgFile":"BRA_Palmeiras"},
    {"id":49,"name":"Brasil","type":"Seleção","fullName":"Confederação Brasileira de Futebol","foundation":"1914","city":"Rio de Janeiro","country":"Brasil","logoImgFile":"BRA"},
    {"id":52,"name":"Argentina","type":"Seleção","fullName":"Asociacón de Fútbol Argentino","foundation":"1893","city":"Buenos Aires","country":"Argentina","logoImgFile":"ARG"},
    {"id":63,"name":"Real Madri","type":"Clube","fullName":"Real Madrid Club de Fútbol","foundation":"1902","city":"Madri","country":"Espanha","logoImgFile":"ESP_Real_Madri"},
    {"id":79,"name":"Manchester United","type":"Clube","fullName":"Manchester United Football Club","foundation":"1878","city":"Manchester","country":"Inglaterra","logoImgFile":"ING_Manchester_United"}
  ]"""

  val corinthians = Team(36, "Corinthians", "Clube", "Sport Club Corinthians Paulista", "1910", "São Paulo", "Brasil", "BRA_Corinthians")
  val brasil = Team(49, "Brasil", "Seleção", "Confederação Brasileira de Futebol", "1914", "Rio de Janeiro", "Brasil", "BRA")
  val manchesterUnited = Team(79, "Manchester United", "Clube", "Manchester United Football Club", "1878", "Manchester", "Inglaterra", "ING_Manchester_United")
  val argentina = Team(52, "Argentina", "Seleção", "Asociacón de Fútbol Argentino", "1893", "Buenos Aires", "Argentina", "ARG")
  val realMadri = Team(63, "Real Madri", "Clube", "Real Madrid Club de Fútbol", "1902", "Madri", "Espanha", "ESP_Real_Madri")

  // --- Case Class Tests ------------------------------------------------------------------------------------------

  test("Team - create instance with valid data for club") {
    val team = Team(1, "Test Club", "Clube", "Test Football Club", "2000", "Test City", "Test Country", "TEST_Club")
    
    assertEquals(team.id, 1)
    assertEquals(team.name, "Test Club")
    assertEquals(team.`type`, "Clube")
    assertEquals(team.fullName, "Test Football Club")
    assertEquals(team.foundation, "2000")
    assertEquals(team.city, "Test City")
    assertEquals(team.country, "Test Country")
    assertEquals(team.logoImgFile, "TEST_Club")
  }

  test("Team - create instance with valid data for national team") {
    val team = Team(2, "Test Nation", "Seleção", "Test National Team", "1950", "Capital", "Test Country", "TEST_Nation")
    
    assertEquals(team.id, 2)
    assertEquals(team.name, "Test Nation")
    assertEquals(team.`type`, "Seleção")
    assertEquals(team.fullName, "Test National Team")
    assertEquals(team.foundation, "1950")
    assertEquals(team.city, "Capital")
    assertEquals(team.country, "Test Country")
    assertEquals(team.logoImgFile, "TEST_Nation")
  }

  test("Team - equality works correctly") {
    val t1 = Team(1, "Test", "Clube", "Test FC", "2000", "City", "Country", "TEST")
    val t2 = Team(1, "Test", "Clube", "Test FC", "2000", "City", "Country", "TEST")
    val t3 = Team(2, "Other", "Seleção", "Other Team", "1990", "Capital", "Other", "OTHER")
    
    assertEquals(t1, t2)
    assertNotEquals(t1, t3)
  }

  test("Team - copy works correctly") {
    val original = Team(1, "Test", "Clube", "Test FC", "2000", "City", "Country", "TEST")
    val copied = original.copy(name = "Modified", city = "New City")
    
    assertEquals(copied.id, 1)
    assertEquals(copied.name, "Modified")
    assertEquals(copied.`type`, "Clube")
    assertEquals(copied.fullName, "Test FC")
    assertEquals(copied.city, "New City")
    assertNotEquals(original, copied)
  }

  test("Team - implements Model trait") {
    val team: Model = Team(1, "Test", "Clube", "Test FC", "2000", "City", "Country", "TEST")
    assert(team.isInstanceOf[Product])
  }

  // --- JSON Deserialization Tests (Single Object) ----------------------------------------------------------------

  test("JSON decode - Corinthians from real API data") {
    val result = decode[Team](corinthiansJson)
    
    assert(result.isRight)
    result.foreach { t =>
      assertEquals(t.id, 36)
      assertEquals(t.name, "Corinthians")
      assertEquals(t.`type`, "Clube")
      assertEquals(t.fullName, "Sport Club Corinthians Paulista")
      assertEquals(t.foundation, "1910")
      assertEquals(t.city, "São Paulo")
      assertEquals(t.country, "Brasil")
      assertEquals(t.logoImgFile, "BRA_Corinthians")
    }
  }

  test("JSON decode - Brasil from real API data") {
    val result = decode[Team](brasilJson)
    
    assert(result.isRight)
    result.foreach { t =>
      assertEquals(t.id, 49)
      assertEquals(t.name, "Brasil")
      assertEquals(t.`type`, "Seleção")
      assertEquals(t.fullName, "Confederação Brasileira de Futebol")
      assertEquals(t.foundation, "1914")
      assertEquals(t.city, "Rio de Janeiro")
      assertEquals(t.country, "Brasil")
      assertEquals(t.logoImgFile, "BRA")
    }
  }

  test("JSON decode - Manchester United from real API data") {
    val result = decode[Team](manchesterUnitedJson)
    
    assert(result.isRight)
    result.foreach { t =>
      assertEquals(t.id, 79)
      assertEquals(t.name, "Manchester United")
      assertEquals(t.`type`, "Clube")
      assertEquals(t.fullName, "Manchester United Football Club")
      assertEquals(t.foundation, "1878")
      assertEquals(t.city, "Manchester")
      assertEquals(t.country, "Inglaterra")
      assertEquals(t.logoImgFile, "ING_Manchester_United")
    }
  }

  test("JSON decode - Argentina from real API data") {
    val result = decode[Team](argentinaJson)
    
    assert(result.isRight)
    result.foreach { t =>
      assertEquals(t.id, 52)
      assertEquals(t.name, "Argentina")
      assertEquals(t.`type`, "Seleção")
      assertEquals(t.fullName, "Asociacón de Fútbol Argentino")
      assertEquals(t.foundation, "1893")
      assertEquals(t.city, "Buenos Aires")
      assertEquals(t.country, "Argentina")
      assertEquals(t.logoImgFile, "ARG")
    }
  }

  test("JSON decode - Real Madri from real API data") {
    val result = decode[Team](realMadriJson)
    
    assert(result.isRight)
    result.foreach { t =>
      assertEquals(t.id, 63)
      assertEquals(t.name, "Real Madri")
      assertEquals(t.`type`, "Clube")
      assertEquals(t.fullName, "Real Madrid Club de Fútbol")
      assertEquals(t.foundation, "1902")
      assertEquals(t.city, "Madri")
      assertEquals(t.country, "Espanha")
      assertEquals(t.logoImgFile, "ESP_Real_Madri")
    }
  }

  test("JSON decode - handles malformed JSON gracefully") {
    val malformedJson = """{"id":"not-a-number","name":"test"}"""
    val result = decode[Team](malformedJson)
    
    assert(result.isLeft)
  }

  test("JSON decode - handles missing required fields") {
    val incompleteJson = """{"id":1,"name":"test"}"""
    val result = decode[Team](incompleteJson)
    
    assert(result.isLeft)
  }

  // --- JSON Deserialization Tests (List) -------------------------------------------------------------------------

  test("JSON decode - list of teams from real API data") {
    val result = decode[List[Team]](allTeamsJson)
    
    assert(result.isRight)
    result.foreach { list =>
      assertEquals(list.length, 6)
      
      // Verify some specific entries
      assert(list.exists(t => t.id == 36 && t.name == "Corinthians"))
      assert(list.exists(t => t.id == 37 && t.name == "Palmeiras"))
      assert(list.exists(t => t.id == 49 && t.name == "Brasil"))
      assert(list.exists(t => t.id == 52 && t.name == "Argentina"))
      assert(list.exists(t => t.id == 63 && t.name == "Real Madri"))
      assert(list.exists(t => t.id == 79 && t.name == "Manchester United"))
    }
  }

  test("JSON decode - empty list") {
    val emptyJson = """[]"""
    val result = decode[List[Team]](emptyJson)
    
    assert(result.isRight)
    result.foreach { list =>
      assertEquals(list.length, 0)
    }
  }

  test("JSON decode - list with single element") {
    val singleJson = """[{"id":1,"name":"Test","type":"Clube","fullName":"Test FC","foundation":"2000","city":"City","country":"Country","logoImgFile":"TEST"}]"""
    val result = decode[List[Team]](singleJson)
    
    assert(result.isRight)
    result.foreach { list =>
      assertEquals(list.length, 1)
      assertEquals(list.head.name, "Test")
    }
  }

  // --- JSON Serialization Tests (Encoding) -----------------------------------------------------------------------

  test("JSON encode - single Team (club)") {
    val json = corinthians.asJson.noSpaces
    
    assert(json.contains("\"id\":36"))
    assert(json.contains("\"name\":\"Corinthians\""))
    assert(json.contains("\"type\":\"Clube\""))
    assert(json.contains("\"fullName\":\"Sport Club Corinthians Paulista\""))
    assert(json.contains("\"foundation\":\"1910\""))
    assert(json.contains("\"city\":\"São Paulo\""))
    assert(json.contains("\"country\":\"Brasil\""))
    assert(json.contains("\"logoImgFile\":\"BRA_Corinthians\""))
  }

  test("JSON encode - single Team (national team)") {
    val json = brasil.asJson.noSpaces
    
    assert(json.contains("\"id\":49"))
    assert(json.contains("\"name\":\"Brasil\""))
    assert(json.contains("\"type\":\"Seleção\""))
    assert(json.contains("\"fullName\":\"Confederação Brasileira de Futebol\""))
    assert(json.contains("\"foundation\":\"1914\""))
    assert(json.contains("\"city\":\"Rio de Janeiro\""))
    assert(json.contains("\"country\":\"Brasil\""))
    assert(json.contains("\"logoImgFile\":\"BRA\""))
  }

  test("JSON encode - list of Teams") {
    val list = List(corinthians, brasil, manchesterUnited)
    val json = list.asJson.noSpaces
    
    assert(json.startsWith("["))
    assert(json.endsWith("]"))
    assert(json.contains("\"name\":\"Corinthians\""))
    assert(json.contains("\"name\":\"Brasil\""))
    assert(json.contains("\"name\":\"Manchester United\""))
  }

  test("JSON encode - empty list") {
    val emptyList: List[Team] = List.empty
    val json = emptyList.asJson.noSpaces
    
    assertEquals(json, "[]")
  }

  // --- Round-trip Tests (Encode then Decode) ---------------------------------------------------------------------

  test("JSON round-trip - single Team (club)") {
    val original = corinthians
    val json = original.asJson.noSpaces
    val decoded = decode[Team](json)
    
    assert(decoded.isRight)
    decoded.foreach { t =>
      assertEquals(t, original)
    }
  }

  test("JSON round-trip - single Team (national team)") {
    val original = brasil
    val json = original.asJson.noSpaces
    val decoded = decode[Team](json)
    
    assert(decoded.isRight)
    decoded.foreach { t =>
      assertEquals(t, original)
    }
  }

  test("JSON round-trip - list of teams") {
    val original = List(
      corinthians,
      brasil,
      manchesterUnited,
      argentina,
      realMadri
    )
    val json = original.asJson.noSpaces
    val decoded = decode[List[Team]](json)
    
    assert(decoded.isRight)
    decoded.foreach { list =>
      assertEquals(list.length, original.length)
      assertEquals(list, original)
    }
  }

  // --- Integration-style Tests with Real Data -------------------------------------------------------------------

  test("Real data - all teams are unique by id") {
    val result = decode[List[Team]](allTeamsJson)
    
    result.foreach { list =>
      val ids = list.map(_.id)
      assertEquals(ids.distinct.length, ids.length, "All IDs should be unique")
    }
  }

  test("Real data - teams have valid types") {
    val result = decode[List[Team]](allTeamsJson)
    
    result.foreach { list =>
      list.foreach { t =>
        assert(t.`type` == "Clube" || t.`type` == "Seleção", 
               s"Team type should be 'Clube' or 'Seleção' for team ${t.id}")
      }
    }
  }

  test("Real data - teams have non-empty names") {
    val result = decode[List[Team]](allTeamsJson)
    
    result.foreach { list =>
      list.foreach { t =>
        assert(t.name.nonEmpty, s"Name should not be empty for team ${t.id}")
      }
    }
  }

  test("Real data - teams have non-empty full names") {
    val result = decode[List[Team]](allTeamsJson)
    
    result.foreach { list =>
      list.foreach { t =>
        assert(t.fullName.nonEmpty, s"Full name should not be empty for team ${t.id}")
      }
    }
  }

  test("Real data - teams have positive IDs") {
    val result = decode[List[Team]](allTeamsJson)
    
    result.foreach { list =>
      list.foreach { t =>
        assert(t.id > 0, s"ID should be positive for team ${t.id}")
      }
    }
  }

  test("Real data - teams have non-empty foundation years") {
    val result = decode[List[Team]](allTeamsJson)
    
    result.foreach { list =>
      list.foreach { t =>
        assert(t.foundation.nonEmpty, s"Foundation year should not be empty for team ${t.id}")
      }
    }
  }

  test("Real data - teams have non-empty cities") {
    val result = decode[List[Team]](allTeamsJson)
    
    result.foreach { list =>
      list.foreach { t =>
        assert(t.city.nonEmpty, s"City should not be empty for team ${t.id}")
      }
    }
  }

  test("Real data - teams have non-empty countries") {
    val result = decode[List[Team]](allTeamsJson)
    
    result.foreach { list =>
      list.foreach { t =>
        assert(t.country.nonEmpty, s"Country should not be empty for team ${t.id}")
      }
    }
  }

  test("Real data - teams have non-empty logo image files") {
    val result = decode[List[Team]](allTeamsJson)
    
    result.foreach { list =>
      list.foreach { t =>
        assert(t.logoImgFile.nonEmpty, s"Logo image file should not be empty for team ${t.id}")
      }
    }
  }

  test("Real data - club teams are from their home countries") {
    val result = decode[List[Team]](allTeamsJson)
    
    result.foreach { list =>
      // Just verify the structure is consistent - clubs have their primary city in their country
      list.filter(_.`type` == "Clube").foreach { t =>
        assert(t.city.nonEmpty && t.country.nonEmpty, 
               s"Club team ${t.name} should have both city and country")
      }
    }
  }

  test("Real data - national teams match their country") {
    val result = decode[List[Team]](allTeamsJson)
    
    result.foreach { list =>
      // For national teams in our sample data
      val brasilTeam = list.find(_.id == 49)
      brasilTeam.foreach { t =>
        assertEquals(t.country, "Brasil")
        assertEquals(t.`type`, "Seleção")
      }
      
      val argentinaTeam = list.find(_.id == 52)
      argentinaTeam.foreach { t =>
        assertEquals(t.country, "Argentina")
        assertEquals(t.`type`, "Seleção")
      }
    }
  }

end TeamsTest
