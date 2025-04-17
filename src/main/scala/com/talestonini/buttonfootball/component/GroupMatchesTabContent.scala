package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Standings.*
import com.talestonini.component.Table
import com.talestonini.component.Table.*
import com.talestonini.util.*
import com.talestonini.util.Window.Size

object GroupMatchesTabContent:

  def apply(tabName: String): Element =
    div(
      div(
        cls := "container border bg-white p-3",
        buildStyleAttr("overflow-x: auto"),
        table(
          cls := "table align-middle mb-0",
          tbody(
            children <-- sGroupsMatches.signal.map(
              gms => gms.filter(gm => gm.`type` == tabName).map(gm => MatchTableRow(gm))
            )
          )
        ),
      ),
      div(
        cls := "container border border-top-0 bg-white p-3 text-end",
        buildStyleAttr("overflow-x: auto"),
        child <-- vGroupStandings.signal.map(gss => {
          val vGroupStandings: Var[List[Standing]] = Var(gss.filter(gs => gs.`type` == tabName))
          val ws = Window.size()
          val smallish = ws == Size.Small || ws == Size.Medium
          Table[Standing](vGroupStandings, List(
            Column(if (smallish) "" else "Intra-Grupo", 6),
            StandingsTeamColumn(ws),
            Column(if (smallish) "P" else "Pontos", 9),
            Column(if (smallish) "J" else "Jogos", 10),
            Column(if (smallish) "V" else "Vitórias", 11),
            Column(if (smallish) "E" else "Empates", 12),
            Column(if (smallish) "D" else "Derrotas", 13),
            Column(if (smallish) "GM" else "Gols Marcados", 14),
            Column(if (smallish) "GS" else "Gols Sofridos", 15),
            Column(if (smallish) "S" else "Saldo de Gols", 16),
            Column(if (smallish) "EG" else "Extra-Grupo", 7)
          ))
        })
      )
    )

end GroupMatchesTabContent