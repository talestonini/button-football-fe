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
case object TeamTypeToken extends Token("Tipo de Time", "Team Type")

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

  def apply(text: String, translationMap: Option[Map[Lang, Map[String, String]]] = None): Signal[String] =
    vLang.signal.map(lang =>
      if (translationMap.isEmpty) text
      else translationMap.get.get(lang).get.getOrElse(text, text)
    )

}