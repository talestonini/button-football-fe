package com.talestonini.buttonfootball

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.modifiers.KeySetter.HtmlAttrSetter
import org.scalajs.dom
import org.scalajs.dom.DOMRect

package object util {
  
  case class Box(top: Double, left: Double, bottom: Double, right: Double, width: Double, height: Double)

  def boundingBox(elemId: String, relativeToElemId: Option[String] = None): Box =
    val elemRect = boundingRect(elemId)
    val elemBox = Box(elemRect.top, elemRect.left, elemRect.bottom, elemRect.right, elemRect.width, elemRect.height)
    if (relativeToElemId.isEmpty)
      elemBox
    else
      val refElemRect = boundingRect(relativeToElemId.get)
      Box(
        elemRect.top - refElemRect.top,
        elemRect.left - refElemRect.left,
        elemRect.bottom - refElemRect.top,
        elemRect.right - refElemRect.left,
        elemRect.width,
        elemRect.height
      )
  end boundingBox

  def buildStyleAttr(styles: String*): HtmlAttrSetter[String] =
    styleAttr(styles.reduce(_ + "; " + _))

  def elemById(elemId: String): org.scalajs.dom.Element =
    dom.document.getElementById(elemId)

  def heightOfElem(elemId: String): String =
    propOfElemRect(elemId, (rect) => rect.height)

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

  private def boundingRect(elemId: String): DOMRect =
    elemById(elemId).getBoundingClientRect()

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

  def scrollHeightOfElem(elemId: String): String =
    propOfElem(elemId, (rect) => rect.scrollHeight)

  def scrollWidthOfElem(elemId: String): String =
    propOfElem(elemId, (rect) => rect.scrollWidth)
  
  def spacingStyle(spacingType: String): String =
    s"$spacingType-lg-3 $spacingType-md-2 $spacingType-1"

  def widthOfElem(elemId: String): String =
    propOfElemRect(elemId, (rect) => rect.width)

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

  private def propOfElem(elemId: String, propFn: org.scalajs.dom.Element => Double): String =
    val elem = elemById(elemId)
    val prop = if (elem != null) propFn(elem) else 0
    prop.toString
  end propOfElem

  private def propOfElemRect(elemId: String, propFn: DOMRect => Double): String =
    val elem = elemById(elemId)
    val prop = if (elem != null) propFn(elem.getBoundingClientRect()) else 0
    prop.toString
  end propOfElemRect

}
