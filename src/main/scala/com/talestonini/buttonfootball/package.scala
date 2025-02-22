package com.talestonini

import com.raquo.laminar.api.L.{*, given}

package object buttonfootball:

  def renderCardTitle(title: String): Element =
    h6(
      cls := "card-subtitle mb-2 text-muted",
      b(title)
    )

end buttonfootball