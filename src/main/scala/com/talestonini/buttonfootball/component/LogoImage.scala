package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.talestonini.buttonfootball.util.buildStyleAttr
import com.talestonini.buttonfootball.util.Logo
import org.scalajs.dom.HTMLImageElement

object LogoImage:

  def apply(source: String, pxSize: Int = Logo.SMALL_TEAM_LOGO_PX_SIZE): ReactiveHtmlElement[HTMLImageElement] =
    img(
      buildStyleAttr(s"width: ${pxSize}px", s"height: ${pxSize}px", "object-fit: none"),
      src := source
    )

end LogoImage