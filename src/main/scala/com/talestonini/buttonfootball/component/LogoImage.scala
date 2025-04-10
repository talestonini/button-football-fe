package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.util.*
import com.talestonini.util.Window
import com.talestonini.util.Window.Size

object LogoImage:

  val XSMALL_LOGO_PX_SIZE = 30
  val SMALL_LOGO_PX_SIZE  = 40
  val MEDIUM_LOGO_PX_SIZE = 50
  val LARGE_LOGO_PX_SIZE  = 60

  private def size() = Window.size() match {
    case Size.Small  => SMALL_LOGO_PX_SIZE
    case Size.Medium => MEDIUM_LOGO_PX_SIZE
    case Size.Large  => LARGE_LOGO_PX_SIZE
  }

  def apply(source: String, pxSize: Option[Int] = None): Element =
    img(
      buildStyleAttr(
        s"width: ${pxSize.getOrElse(size())}px",
        s"height: ${pxSize.getOrElse(size())}px",
        "object-fit: scale-down"
      ),
      src := source
    )

end LogoImage