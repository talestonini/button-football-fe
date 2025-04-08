package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.util.buildStyleAttr

object AccordionItem:

  def apply(id: String, header: String, content: Element): Element =
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
            header
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