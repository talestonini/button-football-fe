package com.talestonini.component

import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.service.Lang

object Languages:

  def apply(vLang: Var[Lang], ptBrLang: Lang, enLang: Lang): Element =
    div(
      cls := "bnt-group",
      role := "group",
      input(
        typ := "radio",
        cls := "btn-check",
        nameAttr := "btnLang",
        idAttr := "btnPtBr",
        checked := true,
        onChange.mapToChecked --> (selected => {
          val currLang = vLang.now()
          vLang.update(_ => if (selected) ptBrLang else currLang)
        }),
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
        checked := false,
        onChange.mapToChecked --> (selected => {
          val currLang = vLang.now()
          vLang.update(_ => if (selected) enLang else currLang)
        }),
      ),
      label(
        cls := "btn btn-outline-primary",
        forId := "btnEn",
        "EN 🇬🇧"
      )
    )

end Languages