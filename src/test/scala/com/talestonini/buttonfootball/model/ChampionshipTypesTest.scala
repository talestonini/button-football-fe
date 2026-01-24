package com.talestonini.buttonfootball.model

import com.talestonini.buttonfootball.model.ChampionshipTypes.*
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.*
import io.circe.syntax.*

class ChampionshipTypesTest extends munit.FunSuite:

  // Circe codecs for testing
  implicit val championshipTypeDecoder: Decoder[ChampionshipType] = deriveDecoder[ChampionshipType]
  implicit val championshipTypeEncoder: Encoder[ChampionshipType] = deriveEncoder[ChampionshipType]

  // --- Test Fixtures (Real Data from API) -----------------------------------------------------------------------
  
  val campeonatoBrasileiroJson = """{"id":2,"code":"cb","description":"Campeonato Brasileiro","logoImgFile":"Campeonato_Brasileiro"}"""
  val copaDoMundoJson = """{"id":3,"code":"cm","description":"Copa do Mundo","logoImgFile":"Copa_do_Mundo"}"""
  val copaAmericaJson = """{"id":1,"code":"ca","description":"Copa América","logoImgFile":"Copa_America"}"""
  
  val allChampionshipTypesJson = """[
    {"id":2,"code":"cb","description":"Campeonato Brasileiro","logoImgFile":"Campeonato_Brasileiro"},
    {"id":3,"code":"cm","description":"Copa do Mundo","logoImgFile":"Copa_do_Mundo"},
    {"id":1,"code":"ca","description":"Copa América","logoImgFile":"Copa_America"},
    {"id":5,"code":"la","description":"Copa Libertadores da América","logoImgFile":"Copa_Libertadores_da_America"},
    {"id":4,"code":"eu","description":"Eurocopa","logoImgFile":"Eurocopa"},
    {"id":6,"code":"lc","description":"Liga dos Campeões da Europa","logoImgFile":"Liga_dos_Campeoes_da_Europa"},
    {"id":7,"code":"mc","description":"Mundial de Clubes","logoImgFile":"Mundial_de_Clubes"}
  ]"""

  val campeonatoBrasileiro = ChampionshipType(2, "cb", "Campeonato Brasileiro", "Campeonato_Brasileiro")
  val copaDoMundo = ChampionshipType(3, "cm", "Copa do Mundo", "Copa_do_Mundo")
  val copaAmerica = ChampionshipType(1, "ca", "Copa América", "Copa_America")
  val libertadores = ChampionshipType(5, "la", "Copa Libertadores da América", "Copa_Libertadores_da_America")
  val eurocopa = ChampionshipType(4, "eu", "Eurocopa", "Eurocopa")
  val ligaDosCampeoes = ChampionshipType(6, "lc", "Liga dos Campeões da Europa", "Liga_dos_Campeoes_da_Europa")
  val mundialDeClubes = ChampionshipType(7, "mc", "Mundial de Clubes", "Mundial_de_Clubes")

  // --- Case Class Tests ------------------------------------------------------------------------------------------

  test("ChampionshipType - create instance with valid data") {
    val championshipType = ChampionshipType(1, "test", "Test Championship", "test_logo.png")
    
    assertEquals(championshipType.id, 1)
    assertEquals(championshipType.code, "test")
    assertEquals(championshipType.description, "Test Championship")
    assertEquals(championshipType.logoImgFile, "test_logo.png")
  }

  test("ChampionshipType - equality works correctly") {
    val ct1 = ChampionshipType(1, "cb", "Campeonato Brasileiro", "logo.png")
    val ct2 = ChampionshipType(1, "cb", "Campeonato Brasileiro", "logo.png")
    val ct3 = ChampionshipType(2, "cm", "Copa do Mundo", "other.png")
    
    assertEquals(ct1, ct2)
    assertNotEquals(ct1, ct3)
  }

  test("ChampionshipType - copy works correctly") {
    val original = ChampionshipType(1, "test", "Test", "logo.png")
    val copied = original.copy(description = "Modified Test")
    
    assertEquals(copied.id, 1)
    assertEquals(copied.code, "test")
    assertEquals(copied.description, "Modified Test")
    assertEquals(copied.logoImgFile, "logo.png")
    assertNotEquals(original, copied)
  }

  test("ChampionshipType - implements Model trait") {
    val championshipType: Model = ChampionshipType(1, "test", "Test", "logo.png")
    assert(championshipType.isInstanceOf[Product])
  }

  // --- NO_CHAMPIONSHIP_TYPE Constant Tests ----------------------------------------------------------------------

  test("NO_CHAMPIONSHIP_TYPE - has correct default values") {
    assertEquals(NO_CHAMPIONSHIP_TYPE.id, -1)
    assertEquals(NO_CHAMPIONSHIP_TYPE.code, "-")
    assertEquals(NO_CHAMPIONSHIP_TYPE.description, "-")
    assertEquals(NO_CHAMPIONSHIP_TYPE.logoImgFile, "-")
  }

  test("NO_CHAMPIONSHIP_TYPE - can be used as sentinel value") {
    val validType = ChampionshipType(1, "test", "Test", "logo.png")
    
    assertNotEquals(validType, NO_CHAMPIONSHIP_TYPE)
    assert(validType.id != NO_CHAMPIONSHIP_TYPE.id)
  }

  test("NO_CHAMPIONSHIP_TYPE - is a valid ChampionshipType instance") {
    val noType: ChampionshipType = NO_CHAMPIONSHIP_TYPE
    assert(noType.isInstanceOf[ChampionshipType])
  }

  // --- JSON Deserialization Tests (Single Object) ----------------------------------------------------------------

  test("JSON decode - Campeonato Brasileiro from real API data") {
    val result = decode[ChampionshipType](campeonatoBrasileiroJson)
    
    assert(result.isRight)
    result.foreach { ct =>
      assertEquals(ct.id, 2)
      assertEquals(ct.code, "cb")
      assertEquals(ct.description, "Campeonato Brasileiro")
      assertEquals(ct.logoImgFile, "Campeonato_Brasileiro")
    }
  }

  test("JSON decode - Copa do Mundo from real API data") {
    val result = decode[ChampionshipType](copaDoMundoJson)
    
    assert(result.isRight)
    result.foreach { ct =>
      assertEquals(ct.id, 3)
      assertEquals(ct.code, "cm")
      assertEquals(ct.description, "Copa do Mundo")
      assertEquals(ct.logoImgFile, "Copa_do_Mundo")
    }
  }

  test("JSON decode - Copa América from real API data") {
    val result = decode[ChampionshipType](copaAmericaJson)
    
    assert(result.isRight)
    result.foreach { ct =>
      assertEquals(ct.id, 1)
      assertEquals(ct.code, "ca")
      assertEquals(ct.description, "Copa América")
      assertEquals(ct.logoImgFile, "Copa_America")
    }
  }

  test("JSON decode - handles malformed JSON gracefully") {
    val malformedJson = """{"id":"not-a-number","code":"test"}"""
    val result = decode[ChampionshipType](malformedJson)
    
    assert(result.isLeft)
  }

  test("JSON decode - handles missing required fields") {
    val incompleteJson = """{"id":1,"code":"test"}"""
    val result = decode[ChampionshipType](incompleteJson)
    
    assert(result.isLeft)
  }

  // --- JSON Deserialization Tests (List) -------------------------------------------------------------------------

  test("JSON decode - list of all championship types from real API data") {
    val result = decode[List[ChampionshipType]](allChampionshipTypesJson)
    
    assert(result.isRight)
    result.foreach { list =>
      assertEquals(list.length, 7)
      
      // Verify some specific entries
      assert(list.exists(ct => ct.code == "cb" && ct.description == "Campeonato Brasileiro"))
      assert(list.exists(ct => ct.code == "cm" && ct.description == "Copa do Mundo"))
      assert(list.exists(ct => ct.code == "ca" && ct.description == "Copa América"))
      assert(list.exists(ct => ct.code == "la" && ct.description == "Copa Libertadores da América"))
      assert(list.exists(ct => ct.code == "eu" && ct.description == "Eurocopa"))
      assert(list.exists(ct => ct.code == "lc" && ct.description == "Liga dos Campeões da Europa"))
      assert(list.exists(ct => ct.code == "mc" && ct.description == "Mundial de Clubes"))
    }
  }

  test("JSON decode - empty list") {
    val emptyJson = """[]"""
    val result = decode[List[ChampionshipType]](emptyJson)
    
    assert(result.isRight)
    result.foreach { list =>
      assertEquals(list.length, 0)
    }
  }

  test("JSON decode - list with single element") {
    val singleJson = """[{"id":1,"code":"test","description":"Test","logoImgFile":"test.png"}]"""
    val result = decode[List[ChampionshipType]](singleJson)
    
    assert(result.isRight)
    result.foreach { list =>
      assertEquals(list.length, 1)
      assertEquals(list.head.code, "test")
    }
  }

  // --- JSON Serialization Tests (Encoding) -----------------------------------------------------------------------

  test("JSON encode - single ChampionshipType") {
    val json = campeonatoBrasileiro.asJson.noSpaces
    
    assert(json.contains("\"id\":2"))
    assert(json.contains("\"code\":\"cb\""))
    assert(json.contains("\"description\":\"Campeonato Brasileiro\""))
    assert(json.contains("\"logoImgFile\":\"Campeonato_Brasileiro\""))
  }

  test("JSON encode - NO_CHAMPIONSHIP_TYPE") {
    val json = NO_CHAMPIONSHIP_TYPE.asJson.noSpaces
    
    assert(json.contains("\"id\":-1"))
    assert(json.contains("\"code\":\"-\""))
    assert(json.contains("\"description\":\"-\""))
    assert(json.contains("\"logoImgFile\":\"-\""))
  }

  test("JSON encode - list of ChampionshipTypes") {
    val list = List(campeonatoBrasileiro, copaDoMundo, copaAmerica)
    val json = list.asJson.noSpaces
    
    assert(json.startsWith("["))
    assert(json.endsWith("]"))
    assert(json.contains("\"code\":\"cb\""))
    assert(json.contains("\"code\":\"cm\""))
    assert(json.contains("\"code\":\"ca\""))
  }

  test("JSON encode - empty list") {
    val emptyList: List[ChampionshipType] = List.empty
    val json = emptyList.asJson.noSpaces
    
    assertEquals(json, "[]")
  }

  // --- Round-trip Tests (Encode then Decode) ---------------------------------------------------------------------

  test("JSON round-trip - single ChampionshipType") {
    val original = campeonatoBrasileiro
    val json = original.asJson.noSpaces
    val decoded = decode[ChampionshipType](json)
    
    assert(decoded.isRight)
    decoded.foreach { ct =>
      assertEquals(ct, original)
    }
  }

  test("JSON round-trip - NO_CHAMPIONSHIP_TYPE") {
    val original = NO_CHAMPIONSHIP_TYPE
    val json = original.asJson.noSpaces
    val decoded = decode[ChampionshipType](json)
    
    assert(decoded.isRight)
    decoded.foreach { ct =>
      assertEquals(ct, original)
    }
  }

  test("JSON round-trip - list of all championship types") {
    val original = List(
      campeonatoBrasileiro,
      copaDoMundo,
      copaAmerica,
      libertadores,
      eurocopa,
      ligaDosCampeoes,
      mundialDeClubes
    )
    val json = original.asJson.noSpaces
    val decoded = decode[List[ChampionshipType]](json)
    
    assert(decoded.isRight)
    decoded.foreach { list =>
      assertEquals(list.length, original.length)
      assertEquals(list, original)
    }
  }

  // --- Integration-style Tests with Real Data -------------------------------------------------------------------

  test("Real data - all championship types are unique by id") {
    val result = decode[List[ChampionshipType]](allChampionshipTypesJson)
    
    result.foreach { list =>
      val ids = list.map(_.id)
      assertEquals(ids.distinct.length, ids.length, "All IDs should be unique")
    }
  }

  test("Real data - all championship types are unique by code") {
    val result = decode[List[ChampionshipType]](allChampionshipTypesJson)
    
    result.foreach { list =>
      val codes = list.map(_.code)
      assertEquals(codes.distinct.length, codes.length, "All codes should be unique")
    }
  }

  test("Real data - all championship types have non-empty descriptions") {
    val result = decode[List[ChampionshipType]](allChampionshipTypesJson)
    
    result.foreach { list =>
      list.foreach { ct =>
        assert(ct.description.nonEmpty, s"Description should not be empty for ${ct.code}")
        assert(ct.description != "-", s"Description should not be default value for ${ct.code}")
      }
    }
  }

  test("Real data - all championship types have valid logo file references") {
    val result = decode[List[ChampionshipType]](allChampionshipTypesJson)
    
    result.foreach { list =>
      list.foreach { ct =>
        assert(ct.logoImgFile.nonEmpty, s"Logo file should not be empty for ${ct.code}")
        assert(ct.logoImgFile != "-", s"Logo file should not be default value for ${ct.code}")
        assert(!ct.logoImgFile.contains(" "), s"Logo file should not contain spaces for ${ct.code}")
      }
    }
  }

  test("Real data - IDs are positive integers") {
    val result = decode[List[ChampionshipType]](allChampionshipTypesJson)
    
    result.foreach { list =>
      list.foreach { ct =>
        assert(ct.id > 0, s"ID should be positive for ${ct.code}")
      }
    }
  }

end ChampionshipTypesTest
  