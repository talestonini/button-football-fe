package com.talestonini.component

import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.api.L.{*, given}

object Languages {

  def apply(): Element =
    div(
      cls := "bnt-group",
      role := "group",
      input(
        typ := "radio",
        cls := "btn-check",
        nameAttr := "btnLang",
        idAttr := "btnPtBr",
        checked := true
      ),
      label(
        cls := "btn btn-outline-primary",
        forId := "btnPtBr",
        "PT 🇧🇷"
      ),
      input(
        typ := "radio",
        cls := "btn-check",
        nameAttr := "btnLang",
        idAttr := "btnEn",
        checked := false
      ),
      label(
        cls := "btn btn-outline-primary",
        forId := "btnEn",
        "EN 🇬🇧"
      )
    )

}