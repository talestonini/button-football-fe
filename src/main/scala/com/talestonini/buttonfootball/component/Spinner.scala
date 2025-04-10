package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.model.*

object Spinner:

  def apply(): Element =
    div(
      cls := "d-flex justify-content-center",
      child <-- vIsLoading.signal.map(isLoading =>
        if (isLoading)
          div(
            cls := s"spinner-border text-muted m-4",
            role := "status",
            span(cls := "visually-hidden", "Carregando...")
          )
        else
          div()
      )
    )

end Spinner