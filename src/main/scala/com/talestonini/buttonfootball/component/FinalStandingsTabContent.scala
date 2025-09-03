package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Standings.*
import com.talestonini.buttonfootball.service.*
import com.talestonini.component.Table
import com.talestonini.component.Table.*
import com.talestonini.util.*
import com.talestonini.util.Window.Size

object FinalStandingsTabContent:

  def apply(): Element =
    div(
      cls := "container border bg-white p-3 text-end",
      buildStyleAttr("overflow-x: auto"),
      child <-- vFinalStandings.signal.map(fss => {
        val vFinalStandings: Var[List[Standing]] = Var(fss)
        val ws = Window.size()
        val smallish = ws == Size.Small || ws == Size.Medium
        Table[Standing](vFinalStandings, List(
          Column(if (smallish) "" else "Final", 8),
          StandingsTeamColumn(ws),
          Column("", 3, "text-start", !smallish,
            Some((teamName: String) => span(text <-- I18n(teamName, TeamTranslationMap)))
          ),
          Column(if (smallish) "P" else "Pontos", 9),
          Column(if (smallish) "J" else "Jogos", 10),
          Column(if (smallish) "V" else "Vitórias", 11),
          Column(if (smallish) "E" else "Empates", 12),
          Column(if (smallish) "D" else "Derrotas", 13),
          Column(if (smallish) "GM" else "Gols Marcados", 14),
          Column(if (smallish) "GS" else "Gols Sofridos", 15),
          Column(if (smallish) "S" else "Saldo de Gols", 16)
        ))
      })
    )

end FinalStandingsTabContent