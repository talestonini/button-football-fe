package com.talestonini.buttonfootball

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.modifiers.KeySetter.HtmlAttrSetter
import org.scalajs.dom

package object util {
  
  extension (elem: Element)
    def wrapInDiv(className: String = ""): Element = div(cls := className, elem)

  enum WindowSize:
    case Large, Medium, Small

  def windowSize(): WindowSize =
    val w = dom.window.innerWidth
    if (w >= 992)
      WindowSize.Large
    else if (w >= 768)
      WindowSize.Medium
    else
      WindowSize.Small
  end windowSize

  def scaleFont(): String =
    if (windowSize() == WindowSize.Small)
      "font-size: 0.6rem"
    else
      "font-size: 1rem"

  def buildStyleAttr(styles: String*): HtmlAttrSetter[String] =
    styleAttr(styles.reduce(_ + "; " + _))

  def spacingStyle(spacingType: String): String =
    s"$spacingType-lg-3 $spacingType-md-2 $spacingType-1"

}
