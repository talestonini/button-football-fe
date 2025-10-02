package com.talestonini.buttonfootball.service

import com.raquo.airstream.core.Signal
import com.talestonini.buttonfootball.model.vLang

sealed trait Lang
case object PT_BR extends Lang
case object EN extends Lang

sealed trait Token(inPtBr: String, inEn: String) {
  def in(lang: Lang): String = lang match {
    case PT_BR => inPtBr
    case EN => inEn
  }
}
case object AppTitleToken extends Token("Jogo de Botão", "Button Football")
case object ChampionshipToken extends Token("Campeonato", "Championship")
case object ChampionshipCreationToken extends Token("Criação", "Creation")
case object ChampionshipStageToken extends Token("Fase", "Stage")
case object ChampionshipEditionToken extends Token("Edição", "Edition")
case object EmptyToken extends Token("", "")
case object PointsTableDismissButtonToken extends Token("Ok", "Ok")
case object PointsTableFinalStandingToken extends Token("Colocação Final", "Final Standing")
case object PointsTableTitleToken extends Token("Tabela de Pontos", "Points Table")
case object RankingAveragePositionShortToken extends Token("CFMéd", "AvgFS")
case object RankingAveragePositionToken extends Token("Classificação Final Média", "Average Final Standing")
case object RankingBestPositionShortToken extends Token("Me", "Be")
case object RankingBestPositionToken extends Token("Melhor", "Best")
case object RankingChampionshipsShortToken extends Token("T", "T")
case object RankingChampionshipsToken extends Token("Títulos", "Titles")
case object RankingParticipationsShortToken extends Token("Pt", "Pt")
case object RankingParticipationsToken extends Token("Participações", "Participations")
case object RankingPointsShortToken extends Token("PR", "RPts")
case object RankingPointsToken extends Token("Pontos de Ranking", "Ranking Points")
case object RankingPointsTableToken extends Token("tabela de pontos", "points table")
case object RankingPositionToken extends Token("Posição", "Position")
case object RankingPositionShortToken extends Token("", "")
case object RankingSeePointsTableToken extends Token("Veja a ", "See the ")
case object RankingUpToEditionNoteToken extends Token("* Até a edição %s do campeonato selecionado.",
  "* Up to edition %s of the selected championship.")
case object RankingWorstPositionShortToken extends Token("Pi", "Wo")
case object RankingWorstPositionToken extends Token("Pior", "Worst")
case object StandingsDrawsToken extends Token("Empates", "Draws")
case object StandingsDrawsShortToken extends Token("E", "D")
case object StandingsExtraGroupToken extends Token("Extra-Grupo", "Extra-Group")
case object StandingsExtraGroupShortToken extends Token("EG", "EG")
case object StandingsFinalToken extends Token("Final", "Final")
case object StandingsFinalShortToken extends Token("", "")
case object StandingsGoalsConcededToken extends Token("Gols Sofridos", "Goals Against")
case object StandingsGoalsConcededShortToken extends Token("GS", "GA")
case object StandingsGoalsDiffToken extends Token("Saldo de Gols", "Goals Diff")
case object StandingsGoalsDiffShortToken extends Token("S", "GD")
case object StandingsGoalsScoredToken extends Token("Gols Marcados", "Goals For")
case object StandingsGoalsScoredShortToken extends Token("GM", "GF")
case object StandingsIntraGroupToken extends Token("Intra-Grupo", "Intra-Group")
case object StandingsIntraGroupShortToken extends Token("", "")
case object StandingsLossesToken extends Token("Derrotas", "Losses")
case object StandingsLossesShortToken extends Token("D", "L")
case object StandingsMatchesToken extends Token("Jogos", "Matches Played")
case object StandingsMatchesShortToken extends Token("J", "MP")
case object StandingsPointsToken extends Token("Pontos", "Points")
case object StandingsPointsShortToken extends Token("P", "Pts")
case object StandingsWinsToken extends Token("Vitórias", "Wins")
case object StandingsWinsShortToken extends Token("V", "W")
case object TeamTypeToken extends Token("Tipo de Time", "Team Type")

val TeamTypeTranslationMap: Map[Lang, Map[String, String]] = Map(
  PT_BR -> Map.empty,
  EN -> Map(
    "Clube" -> "Club",
    "Seleção" -> "National",
  )
)

val ChampionshipTypeTranslationMap: Map[Lang, Map[String, String]] = Map(
  PT_BR -> Map.empty,
  EN -> Map(
    "Campeonato Brasileiro" -> "Campeonato Brasileiro",
    "Copa Libertadores da América" -> "Libertadores",
    "Liga dos Campeões da Europa" -> "Champions League",
    "Mundial de Clubes" -> "Club World Cup",
    "Copa do Mundo" -> "World Cup",
    "Eurocopa" -> "European Football Championship",
  )
)

val ChampionshipStatusTranslationMap: Map[Lang, Map[String, String]] = Map(
  PT_BR -> Map.empty,
  EN -> Map(
    "Primeira Fase" -> "Groups",
    "Oitavas de Final" -> "Round of Sixteen",
    "Quartas de Final" -> "Quarter-finals",
    "Semifinais" -> "Semi-finals",
    "Finais" -> "Finals",
    "Encerrado" -> "Finished",
  )
)

val TabTranslationMap: Map[Lang, Map[String, String]] = Map(
  PT_BR -> Map.empty,
  EN -> Map(
    "Grupo A" -> "Group A",
    "Grupo B" -> "Group B",
    "Grupo C" -> "Group C",
    "Grupo D" -> "Group D",
    "Grupo E" -> "Group E",
    "Grupo F" -> "Group F",
    "Grupo G" -> "Group H",
    "Grupo H" -> "Group G",
    "Finais" -> "Finals",
    "Classificação" -> "Standings",
    "Ranking" -> "Ranking"
  )
)

val MatchTypeTranslationMap: Map[Lang, Map[String, String]] = Map(
  PT_BR -> Map.empty,
  EN -> Map(
    "Oitava de Final 1" -> "Round of Sixteen 1",
    "Oitava de Final 2" -> "Round of Sixteen 2",
    "Oitava de Final 3" -> "Round of Sixteen 3",
    "Oitava de Final 4" -> "Round of Sixteen 4",
    "Oitava de Final 5" -> "Round of Sixteen 5",
    "Oitava de Final 6" -> "Round of Sixteen 6",
    "Oitava de Final 7" -> "Round of Sixteen 7",
    "Oitava de Final 8" -> "Round of Sixteen 8",
    "Quarta de Final A" -> "Quarter-final A",
    "Quarta de Final B" -> "Quarter-final B",
    "Quarta de Final C" -> "Quarter-final C",
    "Quarta de Final D" -> "Quarter-final D",
    "Semifinal AD" -> "Semi-final AD",
    "Semifinal BC" -> "Semi-final BC",
    "Decisão do 3º Lugar" -> "Third-place Playoff",
    "Final" -> "Grand-final",
  )
)

val TeamTranslationMap: Map[Lang, Map[String, String]] = Map(
  PT_BR -> Map.empty,
  EN -> Map(
    "Alemanha" -> "Germany",
    "Arábia Saudita" -> "Saudi Arabia",
    "Barcelona de Guaiaquil" -> "Barcelona de Guayaquil",
    "Bayern de Munique" -> "Bayern Munich",
    "Bélgica" -> "Belgium",
    "Brasil" -> "Brazil",
    "Camarões" -> "Cameroon",
    "Colômbia" -> "Colombia",
    "Coréia do Sul" -> "South Korea",
    "Dinamarca" -> "Denmark",
    "Espanha" -> "Spain",
    "Estados Unidos" -> "United States",
    "França" -> "France",
    "Holanda" -> "Netherlands",
    "Inglaterra" -> "England",
    "Inter de Milão" -> "Inter Milan",
    "Itália" -> "Italy",
    "México" -> "Mexico",
    "Nigéria" -> "Nigeria",
    "Olympique de Marselha" -> "Olympique de Marseille",
    "Paraguai" -> "Paraguay",
    "Polônia" -> "Poland",
    "Real Madri" -> "Real Madrid",
    "Rússia" -> "Russia",
    "Spartak Moscou" -> "Spartak Moscow",
    "Sporting Lisboa" -> "Sporting",
    "Steaua Bucareste" -> "Steua Bucharest",
    "Suécia" -> "Sweden",
    "Uruguai" -> "Uruguay",
  )
)

object I18n {

  def apply(token: Token): Signal[String] =
    vLang.signal.map(lang => token.in(lang))

  def apply(text: String, translationMap: Map[Lang, Map[String, String]]): Signal[String] =
    vLang.signal.map(lang => translationMap.get(lang).get.getOrElse(text, text))

}