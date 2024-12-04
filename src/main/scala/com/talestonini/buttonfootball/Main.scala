package com.talestonini.buttonfootball

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

@main
def ButtonFootballFrontEnd(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    LiveChart.appElement()
  )