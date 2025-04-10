package com.talestonini

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.modifiers.KeySetter.HtmlAttrSetter
import org.scalajs.dom
import org.scalajs.dom.DOMRect

package object util {
  
  object Bounding:
    case class Box(top: Double, left: Double, bottom: Double, right: Double, width: Double, height: Double)

    def box(elemId: String, relativeToElemId: Option[String] = None): Box =
      val elemRect = rect(elemId)
      val elemBox = Box(elemRect.top, elemRect.left, elemRect.bottom, elemRect.right, elemRect.width, elemRect.height)
      if (relativeToElemId.isEmpty)
        elemBox
      else
        val refElemRect = rect(relativeToElemId.get)
        Box(
          elemRect.top - refElemRect.top,
          elemRect.left - refElemRect.left,
          elemRect.bottom - refElemRect.top,
          elemRect.right - refElemRect.left,
          elemRect.width,
          elemRect.height
        )
    end box

    private def rect(elemId: String): DOMRect =
      Elem.byId(elemId).getBoundingClientRect()
  end Bounding

  object Elem:
    def byId(elemId: String): org.scalajs.dom.Element =
      dom.document.getElementById(elemId)
  
    def height(elemId: String): String =
      rectProp(elemId, (rect) => rect.height)

    def scrollHeight(elemId: String): String =
      prop(elemId, (rect) => rect.scrollHeight)

    def scrollWidth(elemId: String): String =
      prop(elemId, (rect) => rect.scrollWidth)
  
    def width(elemId: String): String =
      rectProp(elemId, (rect) => rect.width)

    private def prop(elemId: String, propFn: org.scalajs.dom.Element => Double): String =
      val elem = byId(elemId)
      val prop = if (elem != null) propFn(elem) else 0
      prop.toString
    end prop
  
    private def rectProp(elemId: String, propFn: DOMRect => Double): String =
      val elem = byId(elemId)
      val prop = if (elem != null) propFn(elem.getBoundingClientRect()) else 0
      prop.toString
    end rectProp
  end Elem

  def buildStyleAttr(styles: String*): HtmlAttrSetter[String] =
    styleAttr(styles.reduce(_ + "; " + _))

  object Window:
    enum Size:
      case Large, Medium, Small

    def size(): Size =
      val w = dom.window.innerWidth
      if (w >= 992)
        Size.Large
      else if (w >= 768)
        Size.Medium
      else
        Size.Small
    end size
  end Window

  extension (elem: Element)
    def wrapInDiv(className: String = ""): Element = div(cls := className, elem)

}