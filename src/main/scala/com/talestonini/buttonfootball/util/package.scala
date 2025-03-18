package com.talestonini.buttonfootball

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.modifiers.KeySetter.HtmlAttrSetter
import org.scalajs.dom

package object util {
  
  def buildStyleAttr(styles: String*): HtmlAttrSetter[String] =
    styleAttr(styles.reduce(_ + "; " + _))

  object Logo:
    private val EXTENSION = "bmp"

    def forChampionshipType(logoImgFile: String, isLarge: Boolean = false) =
      s"/img/championships/${if (isLarge) "125" else "45"}/${treat(logoImgFile)}.$EXTENSION"

    def forTrophy(logoImgFile: String) =
      s"/img/championships/trophies/125/${treat(logoImgFile)}.$EXTENSION"

    def forTeam(logoImgFile: String, isLarge: Boolean = false) =
      s"/img/teams/${if (isLarge) "150" else "40"}/${treat(logoImgFile)}.$EXTENSION"

    private def treat(name: String) = name.toLowerCase()
  end Logo

  def renderCardTitle(title: String, marginBottom: String = "mb-2"): Element =
    h6(
      cls := s"card-subtitle ${marginBottom} text-muted",
      b(title)
    )

  def scaleFont(): String =
    if (windowSize() == WindowSize.Small)
      "font-size: 0.6rem"
    else
      "font-size: 1rem"

  def spacingStyle(spacingType: String): String =
    s"$spacingType-lg-3 $spacingType-md-2 $spacingType-1"

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

  extension (elem: Element)
    def wrapInDiv(className: String = ""): Element = div(cls := className, elem)

}
