package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.model.Model
import scala.math.Ordering

object Table:

  sealed trait Sorting
  case object None extends Sorting
  case object Asc extends Sorting
  case object Desc extends Sorting

  case class Header(label: String, modelFieldPos: Int, sorting: Var[Sorting] = Var(None)):

    private val labelVar: Var[String] = Var(label)

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
        case _ => throw new IllegalArgumentException("unsupported header type")
      }
    end anyOrdering

    def renderTh[M <: Model](data: Var[List[M]]): Element =
      th(
        text <-- labelVar,
        onClick --> (ev => {
          val currSorting = sorting.signal.now()
          if (currSorting == None || currSorting == Desc)
            labelVar.update(_ => s"$label ↑")
            data.update(_ => data.now().sortBy(_.productElement(modelFieldPos)))
            sorting.update(_ => Asc)
          else
            labelVar.update(_ => s"$label ↓")
            data.update(_ => data.now().sortBy(_.productElement(modelFieldPos)).reverse)
            sorting.update(_ => Desc)
        })
      )

  end Header

  def apply[M <: Model](data: Var[List[M]], headers: List[Header]): Element =
    def renderTr(r: M): Element =
      tr(headers.map(h => td(r.productElement(h.modelFieldPos) match {
        case Some(o) => o.toString
        case None    => ""
        case a: Any  => a.toString
      })))

    table(
      cls := "table",
      thead(
        cls := "thead-light",
        tr(headers.map(h => Header(h.label, h.modelFieldPos).renderTh(data)))
      ),
      tbody(
        children <-- data.signal.map(ms => ms.map(r => renderTr(r)))
      )
    )
  end apply 

end Table