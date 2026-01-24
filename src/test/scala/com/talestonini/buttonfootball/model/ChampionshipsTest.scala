package com.talestonini.buttonfootball.model

import com.talestonini.buttonfootball.model.Championships.*
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.*
import io.circe.syntax.*

class ChampionshipsTest extends munit.FunSuite:

  // Circe codecs for testing
  implicit val championshipDecoder: Decoder[Championship] = deriveDecoder[Championship]
  implicit val championshipEncoder: Encoder[Championship] = deriveEncoder[Championship]

  // --- Test Fixtures (Real Data from API) -----------------------------------------------------------------------
  
  val ligaDosCampeoesJson = """{"id":5,"type":"Liga dos Campeões da Europa","teamType":"Clube","numEdition":1,"dtCreation":"4/2/2001","dtEnd":"10/12/2003","numTeams":12,"numQualif":8,"status":"Encerrado"}"""
  val copaDoMundoJson = """{"id":6,"type":"Copa do Mundo","teamType":"Seleção","numEdition":1,"dtCreation":"10/2/2001","dtEnd":"10/12/2003","numTeams":12,"numQualif":8,"status":"Encerrado"}"""
  val campeonatoBrasileiroJson = """{"id":7,"type":"Campeonato Brasileiro","teamType":"Clube","numEdition":1,"dtCreation":"4/3/2001","dtEnd":"09/12/2003","numTeams":16,"numQualif":8,"status":"Encerrado"}"""
  val mundialDeClubesJson = """{"id":9,"type":"Mundial de Clubes","teamType":"Clube","numEdition":1,"dtCreation":"7/7/2001","dtEnd":"10/12/2003","numTeams":8,"numQualif":4,"status":"Encerrado"}"""
  val copaDoMundoOngoingJson = """{"id":67,"type":"Copa do Mundo","teamType":"Seleção","numEdition":7,"dtCreation":"03/01/2008","dtEnd":null,"numTeams":24,"numQualif":16,"status":"Primeira Fase"}"""
  
  val allChampionshipsJson = """[
    {"id":5,"type":"Liga dos Campeões da Europa","teamType":"Clube","numEdition":1,"dtCreation":"4/2/2001","dtEnd":"10/12/2003","numTeams":12,"numQualif":8,"status":"Encerrado"},
    {"id":6,"type":"Copa do Mundo","teamType":"Seleção","numEdition":1,"dtCreation":"10/2/2001","dtEnd":"10/12/2003","numTeams":12,"numQualif":8,"status":"Encerrado"},
    {"id":7,"type":"Campeonato Brasileiro","teamType":"Clube","numEdition":1,"dtCreation":"4/3/2001","dtEnd":"09/12/2003","numTeams":16,"numQualif":8,"status":"Encerrado"},
    {"id":8,"type":"Copa Libertadores da América","teamType":"Clube","numEdition":1,"dtCreation":"19/3/2001","dtEnd":"10/12/2003","numTeams":12,"numQualif":8,"status":"Encerrado"},
    {"id":9,"type":"Mundial de Clubes","teamType":"Clube","numEdition":1,"dtCreation":"7/7/2001","dtEnd":"10/12/2003","numTeams":8,"numQualif":4,"status":"Encerrado"}
  ]"""

  val ligaDosCampeoes = Championship(5, "Liga dos Campeões da Europa", "Clube", 1, "4/2/2001", Some("10/12/2003"), 12, 8, "Encerrado")
  val copaDoMundo = Championship(6, "Copa do Mundo", "Seleção", 1, "10/2/2001", Some("10/12/2003"), 12, 8, "Encerrado")
  val campeonatoBrasileiro = Championship(7, "Campeonato Brasileiro", "Clube", 1, "4/3/2001", Some("09/12/2003"), 16, 8, "Encerrado")
  val mundialDeClubes = Championship(9, "Mundial de Clubes", "Clube", 1, "7/7/2001", Some("10/12/2003"), 8, 4, "Encerrado")
  val copaDoMundoOngoing = Championship(67, "Copa do Mundo", "Seleção", 7, "03/01/2008", None, 24, 16, "Primeira Fase")

  // --- Case Class Tests ------------------------------------------------------------------------------------------

  test("Championship - create instance with valid data") {
    val championship = Championship(1, "Test Championship", "Clube", 1, "01/01/2024", Some("31/12/2024"), 20, 8, "Encerrado")
    
    assertEquals(championship.id, 1)
    assertEquals(championship.`type`, "Test Championship")
    assertEquals(championship.teamType, "Clube")
    assertEquals(championship.numEdition, 1)
    assertEquals(championship.dtCreation, "01/01/2024")
    assertEquals(championship.dtEnd, Some("31/12/2024"))
    assertEquals(championship.numTeams, 20)
    assertEquals(championship.numQualif, 8)
    assertEquals(championship.status, "Encerrado")
  }

  test("Championship - create instance with null dtEnd") {
    val championship = Championship(1, "Test Championship", "Seleção", 1, "01/01/2024", None, 20, 8, "Primeira Fase")
    
    assertEquals(championship.dtEnd, None)
    assertEquals(championship.status, "Primeira Fase")
  }

  test("Championship - equality works correctly") {
    val c1 = Championship(1, "Test", "Clube", 1, "01/01/2024", Some("31/12/2024"), 20, 8, "Encerrado")
    val c2 = Championship(1, "Test", "Clube", 1, "01/01/2024", Some("31/12/2024"), 20, 8, "Encerrado")
    val c3 = Championship(2, "Other", "Seleção", 2, "01/01/2024", None, 24, 16, "Primeira Fase")
    
    assertEquals(c1, c2)
    assertNotEquals(c1, c3)
  }

  test("Championship - copy works correctly") {
    val original = Championship(1, "Test", "Clube", 1, "01/01/2024", Some("31/12/2024"), 20, 8, "Encerrado")
    val copied = original.copy(status = "Em Andamento", dtEnd = None)
    
    assertEquals(copied.id, 1)
    assertEquals(copied.`type`, "Test")
    assertEquals(copied.status, "Em Andamento")
    assertEquals(copied.dtEnd, None)
    assertNotEquals(original, copied)
  }

  test("Championship - implements Model trait") {
    val championship: Model = Championship(1, "Test", "Clube", 1, "01/01/2024", Some("31/12/2024"), 20, 8, "Encerrado")
    assert(championship.isInstanceOf[Product])
  }

  // --- NO_CHAMPIONSHIP Constant Tests ---------------------------------------------------------------------------

  test("NO_CHAMPIONSHIP - has correct default values") {
    assertEquals(NO_CHAMPIONSHIP.id, -1)
    assertEquals(NO_CHAMPIONSHIP.`type`, "-")
    assertEquals(NO_CHAMPIONSHIP.teamType, "-")
    assertEquals(NO_CHAMPIONSHIP.numEdition, NO_CHAMPIONSHIP_EDITION)
    assertEquals(NO_CHAMPIONSHIP.dtCreation, "-")
    assertEquals(NO_CHAMPIONSHIP.dtEnd, None)
    assertEquals(NO_CHAMPIONSHIP.numTeams, -1)
    assertEquals(NO_CHAMPIONSHIP.numQualif, -1)
    assertEquals(NO_CHAMPIONSHIP.status, "-")
  }

  test("NO_CHAMPIONSHIP - can be used as sentinel value") {
    val validChampionship = Championship(1, "Test", "Clube", 1, "01/01/2024", Some("31/12/2024"), 20, 8, "Encerrado")
    
    assertNotEquals(validChampionship, NO_CHAMPIONSHIP)
    assert(validChampionship.id != NO_CHAMPIONSHIP.id)
  }

  test("NO_CHAMPIONSHIP - is a valid Championship instance") {
    val noChampionship: Championship = NO_CHAMPIONSHIP
    assert(noChampionship.isInstanceOf[Championship])
  }

  // --- JSON Deserialization Tests (Single Object) ----------------------------------------------------------------

  test("JSON decode - Liga dos Campeões from real API data") {
    val result = decode[Championship](ligaDosCampeoesJson)
    
    assert(result.isRight)
    result.foreach { c =>
      assertEquals(c.id, 5)
      assertEquals(c.`type`, "Liga dos Campeões da Europa")
      assertEquals(c.teamType, "Clube")
      assertEquals(c.numEdition, 1)
      assertEquals(c.dtCreation, "4/2/2001")
      assertEquals(c.dtEnd, Some("10/12/2003"))
      assertEquals(c.numTeams, 12)
      assertEquals(c.numQualif, 8)
      assertEquals(c.status, "Encerrado")
    }
  }

  test("JSON decode - Copa do Mundo from real API data") {
    val result = decode[Championship](copaDoMundoJson)
    
    assert(result.isRight)
    result.foreach { c =>
      assertEquals(c.id, 6)
      assertEquals(c.`type`, "Copa do Mundo")
      assertEquals(c.teamType, "Seleção")
      assertEquals(c.numEdition, 1)
      assertEquals(c.dtCreation, "10/2/2001")
      assertEquals(c.dtEnd, Some("10/12/2003"))
      assertEquals(c.numTeams, 12)
      assertEquals(c.numQualif, 8)
      assertEquals(c.status, "Encerrado")
    }
  }

  test("JSON decode - Campeonato Brasileiro from real API data") {
    val result = decode[Championship](campeonatoBrasileiroJson)
    
    assert(result.isRight)
    result.foreach { c =>
      assertEquals(c.id, 7)
      assertEquals(c.`type`, "Campeonato Brasileiro")
      assertEquals(c.teamType, "Clube")
      assertEquals(c.numEdition, 1)
      assertEquals(c.dtCreation, "4/3/2001")
      assertEquals(c.dtEnd, Some("09/12/2003"))
      assertEquals(c.numTeams, 16)
      assertEquals(c.numQualif, 8)
      assertEquals(c.status, "Encerrado")
    }
  }

  test("JSON decode - Mundial de Clubes from real API data") {
    val result = decode[Championship](mundialDeClubesJson)
    
    assert(result.isRight)
    result.foreach { c =>
      assertEquals(c.id, 9)
      assertEquals(c.`type`, "Mundial de Clubes")
      assertEquals(c.teamType, "Clube")
      assertEquals(c.numEdition, 1)
      assertEquals(c.dtCreation, "7/7/2001")
      assertEquals(c.dtEnd, Some("10/12/2003"))
      assertEquals(c.numTeams, 8)
      assertEquals(c.numQualif, 4)
      assertEquals(c.status, "Encerrado")
    }
  }

  test("JSON decode - Copa do Mundo ongoing with null dtEnd from real API data") {
    val result = decode[Championship](copaDoMundoOngoingJson)
    
    assert(result.isRight)
    result.foreach { c =>
      assertEquals(c.id, 67)
      assertEquals(c.`type`, "Copa do Mundo")
      assertEquals(c.teamType, "Seleção")
      assertEquals(c.numEdition, 7)
      assertEquals(c.dtCreation, "03/01/2008")
      assertEquals(c.dtEnd, None)
      assertEquals(c.numTeams, 24)
      assertEquals(c.numQualif, 16)
      assertEquals(c.status, "Primeira Fase")
    }
  }

  test("JSON decode - handles malformed JSON gracefully") {
    val malformedJson = """{"id":"not-a-number","type":"test"}"""
    val result = decode[Championship](malformedJson)
    
    assert(result.isLeft)
  }

  test("JSON decode - handles missing required fields") {
    val incompleteJson = """{"id":1,"type":"test"}"""
    val result = decode[Championship](incompleteJson)
    
    assert(result.isLeft)
  }

  // --- JSON Deserialization Tests (List) -------------------------------------------------------------------------

  test("JSON decode - list of championships from real API data") {
    val result = decode[List[Championship]](allChampionshipsJson)
    
    assert(result.isRight)
    result.foreach { list =>
      assertEquals(list.length, 5)
      
      // Verify some specific entries
      assert(list.exists(c => c.id == 5 && c.`type` == "Liga dos Campeões da Europa"))
      assert(list.exists(c => c.id == 6 && c.`type` == "Copa do Mundo"))
      assert(list.exists(c => c.id == 7 && c.`type` == "Campeonato Brasileiro"))
      assert(list.exists(c => c.id == 8 && c.`type` == "Copa Libertadores da América"))
      assert(list.exists(c => c.id == 9 && c.`type` == "Mundial de Clubes"))
    }
  }

  test("JSON decode - empty list") {
    val emptyJson = """[]"""
    val result = decode[List[Championship]](emptyJson)
    
    assert(result.isRight)
    result.foreach { list =>
      assertEquals(list.length, 0)
    }
  }

  test("JSON decode - list with single element") {
    val singleJson = """[{"id":1,"type":"Test","teamType":"Clube","numEdition":1,"dtCreation":"01/01/2024","dtEnd":"31/12/2024","numTeams":20,"numQualif":8,"status":"Encerrado"}]"""
    val result = decode[List[Championship]](singleJson)
    
    assert(result.isRight)
    result.foreach { list =>
      assertEquals(list.length, 1)
      assertEquals(list.head.`type`, "Test")
    }
  }

  // --- JSON Serialization Tests (Encoding) -----------------------------------------------------------------------

  test("JSON encode - single Championship with dtEnd") {
    val json = ligaDosCampeoes.asJson.noSpaces
    
    assert(json.contains("\"id\":5"))
    assert(json.contains("\"type\":\"Liga dos Campeões da Europa\""))
    assert(json.contains("\"teamType\":\"Clube\""))
    assert(json.contains("\"numEdition\":1"))
    assert(json.contains("\"dtCreation\":\"4/2/2001\""))
    assert(json.contains("\"dtEnd\":\"10/12/2003\""))
    assert(json.contains("\"numTeams\":12"))
    assert(json.contains("\"numQualif\":8"))
    assert(json.contains("\"status\":\"Encerrado\""))
  }

  test("JSON encode - Championship with null dtEnd") {
    val json = copaDoMundoOngoing.asJson.noSpaces
    
    assert(json.contains("\"id\":67"))
    assert(json.contains("\"type\":\"Copa do Mundo\""))
    assert(json.contains("\"dtEnd\":null"))
    assert(json.contains("\"status\":\"Primeira Fase\""))
  }

  test("JSON encode - NO_CHAMPIONSHIP") {
    val json = NO_CHAMPIONSHIP.asJson.noSpaces
    
    assert(json.contains("\"id\":-1"))
    assert(json.contains("\"type\":\"-\""))
    assert(json.contains("\"teamType\":\"-\""))
    assert(json.contains("\"status\":\"-\""))
  }

  test("JSON encode - list of Championships") {
    val list = List(ligaDosCampeoes, copaDoMundo, campeonatoBrasileiro)
    val json = list.asJson.noSpaces
    
    assert(json.startsWith("["))
    assert(json.endsWith("]"))
    assert(json.contains("\"type\":\"Liga dos Campeões da Europa\""))
    assert(json.contains("\"type\":\"Copa do Mundo\""))
    assert(json.contains("\"type\":\"Campeonato Brasileiro\""))
  }

  test("JSON encode - empty list") {
    val emptyList: List[Championship] = List.empty
    val json = emptyList.asJson.noSpaces
    
    assertEquals(json, "[]")
  }

  // --- Round-trip Tests (Encode then Decode) ---------------------------------------------------------------------

  test("JSON round-trip - single Championship") {
    val original = ligaDosCampeoes
    val json = original.asJson.noSpaces
    val decoded = decode[Championship](json)
    
    assert(decoded.isRight)
    decoded.foreach { c =>
      assertEquals(c, original)
    }
  }

  test("JSON round-trip - Championship with null dtEnd") {
    val original = copaDoMundoOngoing
    val json = original.asJson.noSpaces
    val decoded = decode[Championship](json)
    
    assert(decoded.isRight)
    decoded.foreach { c =>
      assertEquals(c, original)
    }
  }

  test("JSON round-trip - NO_CHAMPIONSHIP") {
    val original = NO_CHAMPIONSHIP
    val json = original.asJson.noSpaces
    val decoded = decode[Championship](json)
    
    assert(decoded.isRight)
    decoded.foreach { c =>
      assertEquals(c, original)
    }
  }

  test("JSON round-trip - list of championships") {
    val original = List(
      ligaDosCampeoes,
      copaDoMundo,
      campeonatoBrasileiro,
      mundialDeClubes
    )
    val json = original.asJson.noSpaces
    val decoded = decode[List[Championship]](json)
    
    assert(decoded.isRight)
    decoded.foreach { list =>
      assertEquals(list.length, original.length)
      assertEquals(list, original)
    }
  }

  // --- Integration-style Tests with Real Data -------------------------------------------------------------------

  test("Real data - all championships are unique by id") {
    val result = decode[List[Championship]](allChampionshipsJson)
    
    result.foreach { list =>
      val ids = list.map(_.id)
      assertEquals(ids.distinct.length, ids.length, "All IDs should be unique")
    }
  }

  test("Real data - championships have valid team types") {
    val result = decode[List[Championship]](allChampionshipsJson)
    
    result.foreach { list =>
      list.foreach { c =>
        assert(c.teamType == "Clube" || c.teamType == "Seleção", 
               s"Team type should be 'Clube' or 'Seleção' for championship ${c.id}")
      }
    }
  }

  test("Real data - championships have non-empty types") {
    val result = decode[List[Championship]](allChampionshipsJson)
    
    result.foreach { list =>
      list.foreach { c =>
        assert(c.`type`.nonEmpty, s"Type should not be empty for championship ${c.id}")
        assert(c.`type` != "-", s"Type should not be default value for championship ${c.id}")
      }
    }
  }

  test("Real data - championships have positive IDs") {
    val result = decode[List[Championship]](allChampionshipsJson)
    
    result.foreach { list =>
      list.foreach { c =>
        assert(c.id > 0, s"ID should be positive for championship ${c.id}")
      }
    }
  }

  test("Real data - championships have positive edition numbers") {
    val result = decode[List[Championship]](allChampionshipsJson)
    
    result.foreach { list =>
      list.foreach { c =>
        assert(c.numEdition > 0, s"Edition number should be positive for championship ${c.id}")
      }
    }
  }

  test("Real data - championships have positive number of teams") {
    val result = decode[List[Championship]](allChampionshipsJson)
    
    result.foreach { list =>
      list.foreach { c =>
        assert(c.numTeams > 0, s"Number of teams should be positive for championship ${c.id}")
      }
    }
  }

  test("Real data - championships have positive number of qualifiers") {
    val result = decode[List[Championship]](allChampionshipsJson)
    
    result.foreach { list =>
      list.foreach { c =>
        assert(c.numQualif > 0, s"Number of qualifiers should be positive for championship ${c.id}")
      }
    }
  }

  test("Real data - number of qualifiers is less than or equal to number of teams") {
    val result = decode[List[Championship]](allChampionshipsJson)
    
    result.foreach { list =>
      list.foreach { c =>
        assert(c.numQualif <= c.numTeams, 
               s"Number of qualifiers should not exceed number of teams for championship ${c.id}")
      }
    }
  }

  test("Real data - championships have valid status values") {
    val result = decode[List[Championship]](allChampionshipsJson)
    val validStatuses = Set("Encerrado", "Primeira Fase", "Em Andamento")
    
    result.foreach { list =>
      list.foreach { c =>
        assert(c.status.nonEmpty, s"Status should not be empty for championship ${c.id}")
      }
    }
  }

  test("Real data - championships have valid date formats") {
    val result = decode[List[Championship]](allChampionshipsJson)
    
    result.foreach { list =>
      list.foreach { c =>
        assert(c.dtCreation.nonEmpty, s"Creation date should not be empty for championship ${c.id}")
        assert(c.dtCreation != "-", s"Creation date should not be default value for championship ${c.id}")
      }
    }
  }

end ChampionshipsTest
