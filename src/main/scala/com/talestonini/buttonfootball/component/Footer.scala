package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.api.L.{*, given}
import com.talestonini.BuildInfo
import com.talestonini.util.buildStyleAttr
import java.time.Year
import java.time.ZoneId

object Footer {

  def apply(): Element =
    div(
      div(buildStyleAttr("font-size: 0.8rem"), p(footerText()))
    )

  private def footerText(): String =
    s"© Tales Tonini & Marcel Tonini, 2024-${Year.now(ZoneId.of("UTC")).getValue()} \u2014 v${BuildInfo.version}"

}
