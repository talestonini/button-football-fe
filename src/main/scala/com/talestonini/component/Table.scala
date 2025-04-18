package com.talestonini.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.model.Model
import scala.math.Ordering
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.HTMLTableCellElement

object Table:

  sealed trait Sorting
  case object NoSorting extends Sorting
  case object Asc extends Sorting
  case object Desc extends Sorting

  case class Column(header: String, modelFieldPos: Int, align: String = "text-end", isRender: Boolean = true,
                    elem: Option[String => Element] = None, sorting: Var[Sorting] = Var(NoSorting)):

    private val headerVar: Var[String] = Var(header)

    implicit val anyOrdering: Ordering[Any] = new Ordering[Any]:
      val stringOrdering: Ordering[String] = Ordering.String
      val numberOrdering: Ordering[BigDecimal] = Ordering.BigDecimal

      override def compare(x: Any, y: Any): Int = (x, y) match {
        case (a: String, b: String) => stringOrdering.compare(a, b)
        case (a: Int, b: Int) => numberOrdering.compare(a, b)
        case (Some(a), Some(b)) => (a, b) match {
          case (a: Number, b: Number) => numberOrdering.compare(a.doubleValue(), b.doubleValue())
          case (a: String, b: String) => stringOrdering.compare(a, b)
        }
        case _ => throw new IllegalArgumentException("unsupported column type")
      }
    end anyOrdering

    def tableHeader[M <: Model](models: Var[List[M]]): Element =
      th(
        cls := s"$align text-muted",
        text <-- headerVar,
        onClick --> (ev => {
          val currSorting = sorting.signal.now()
          if (currSorting == NoSorting || currSorting == Desc)
            headerVar.update(_ => s"$header ↑")
            models.update(_ => models.now().sortBy(_.productElement(modelFieldPos)))
            sorting.update(_ => Asc)
          else
            headerVar.update(_ => s"$header ↓")
            models.update(_ => models.now().sortBy(_.productElement(modelFieldPos)).reverse)
            sorting.update(_ => Desc)
        })
      )

  end Column

  def apply[M <: Model](models: Var[List[M]], headers: List[Column]): Element =
    def tableRow(m: M): Element =
      tr(headers.filter(h => h.isRender).map(h => {
        def valOrApplyElemFnToVal(value: String): Modifier[ReactiveHtmlElement[HTMLTableCellElement]] =
          if (h.elem.isEmpty) value else h.elem.get(value)

        td(
          cls := h.align,
          m.productElement(h.modelFieldPos) match {
            case Some(optionalVal) => valOrApplyElemFnToVal(optionalVal.toString)
            case None              => ""
            case anythingElse: Any => valOrApplyElemFnToVal(anythingElse.toString())
          }
        )
      }))

    table(
      cls := "table align-middle",
      thead(
        cls := "thead-light",
        tr(headers.filter(h => h.isRender).map(h => Column(h.header, h.modelFieldPos).tableHeader(models)))
      ),
      tbody(
        children <-- models.signal.map(ms => ms.map(m => tableRow(m)))
      )
    )
  end apply 

end Table