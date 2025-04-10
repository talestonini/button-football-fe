package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.TeamTypes.*

object TeamTypesContent:

  def apply(): Element =
    div(
      children <-- vTeamTypes.signal.map(tts => tts.map(tt =>
        div(
          cls := "form-check form-check-inline",
          label(
            cls := "form-check-label",
            input(
              cls := "form-check-input",
              typ := "radio",
              nameAttr := "teamType",
              value := tt.code,
              onChange.mapToValue --> { code =>
                vSelectedTeamType.update(_ => vTeamTypes.now().find((tt) => tt.code == code))
                seGetChampionshipTypes(code)
              },
              checked <-- vSelectedTeamType.signal.map(_.getOrElse(NO_TEAM_TYPE).code == tt.code)
            ),
            tt.description
          )
        )
      ))
    )
  
end TeamTypesContent