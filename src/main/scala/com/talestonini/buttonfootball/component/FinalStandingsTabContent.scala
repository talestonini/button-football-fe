package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Standings.*
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
          Column(if (smallish) "" else "Final", 6),
          StandingsTeamColumn(ws),
          Column("", 2, "text-start", !smallish),
          Column(if (smallish) "P" else "Pontos", 7),
          Column(if (smallish) "J" else "Jogos", 8),
          Column(if (smallish) "V" else "Vitórias", 9),
          Column(if (smallish) "E" else "Empates", 10),
          Column(if (smallish) "D" else "Derrotas", 11),
          Column(if (smallish) "GM" else "Gols Marcados", 12),
          Column(if (smallish) "GS" else "Gols Sofridos", 13),
          Column(if (smallish) "S" else "Saldo de Gols", 14)
        ))
      })
    )

end FinalStandingsTabContent