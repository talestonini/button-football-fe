package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.talestonini.buttonfootball.util.buildStyleAttr
import com.talestonini.buttonfootball.util.Logo
import org.scalajs.dom.HTMLImageElement
import com.talestonini.buttonfootball.util.Logo.*
import com.talestonini.buttonfootball.util.Window
import com.talestonini.buttonfootball.util.Window.Size

object LogoImage:

  def apply(source: String, pxSize: Option[Int] = None): ReactiveHtmlElement[HTMLImageElement] =
    img(
      buildStyleAttr(
        s"width: ${pxSize.getOrElse(size())}px",
        s"height: ${pxSize.getOrElse(size())}px",
        "object-fit: scale-down"
      ),
      src := source
    )

  private def size() = Window.size() match {
    case Size.Small  => SMALL_LOGO_PX_SIZE
    case Size.Medium => MEDIUM_LOGO_PX_SIZE
    case Size.Large  => LARGE_LOGO_PX_SIZE
  }

end LogoImage