package com.talestonini.component

import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.api.L.{*, given}
import com.talestonini.BuildInfo
import java.time.Year

object Footer {

  def apply(): Element =
    div(
      div(className := "w3-small", p(footerText()))
    )

  private def footerText(): String =
    s"© Tales Tonini, 2024-${Year.now().getValue()} \u2014 v${BuildInfo.version}"

}
