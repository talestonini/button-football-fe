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
case object TeamTypeToken extends Token("Tipo de Time", "Team Type")

object I18n {
  def apply(token: Token): Signal[String] = vLang.signal.map(lang => token.in(lang))
}