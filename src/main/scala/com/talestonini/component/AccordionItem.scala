package com.talestonini.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.util.buildStyleAttr
import com.talestonini.buttonfootball.service.{I18n, Token}

object AccordionItem:

  def apply(id: String, header: Token, content: Element): Element =
    div(
      cls := "accordion-item shadow",
      h2(
        cls := "accordion-header",
        button(
          cls := "accordion-button",
          typ := "button",
          buildStyleAttr("background-color: rgb(248 249 250)"),
          dataAttr("bs-toggle") := "collapse",
          dataAttr("bs-target") := s"#$id",
          b(
            cls := "text-muted",
            text <-- I18n(header)
          )
        )
      ),
      div(
        idAttr := id,
        cls := "accordion-collapse collapse",
        div(
          cls := "accordion-body",
          content
        )
      )
    )

end AccordionItem