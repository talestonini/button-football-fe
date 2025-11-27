package com.talestonini.buttonfootball.livechart

import com.raquo.laminar.api.L.{*, given}
import scala.scalajs.js
import scala.scalajs.js.annotation.*
import scala.scalajs.js.Dynamic.{literal => jsLiteral}

import org.scalajs.dom
import com.talestonini.buttonfootball.livechart.model.Model
import com.talestonini.buttonfootball.livechart.model.DataItemID
import com.talestonini.buttonfootball.livechart.model.DataItem
import typings.chartJs.mod.Chart
import typings.chartJs.distTypesIndexMod.{ChartConfiguration, ChartItem}

object LiveChart:
  val model = new Model
  import model.*

  def appElement(): Element =
    div(
      h1("Live Chart"),
      renderDataTable(),
      renderDataChart(),
      renderDataList(),
    )

  def renderDataTable(): Element =
    table(
      cls := "table",
      thead(cls := "thead-light", tr(th("Label"), th("Price"), th("Count"), th("Full Price"), th("Action"))),
      tbody(
        children <-- dataSignal.split(_.id) { (id, initial, itemSignal) => renderDataItem(id, itemSignal) }
      ),
      tfoot(tr(
        td(button(cls := "btn btn-primary", "➕", onClick --> (_ => addDataItem(DataItem())))),
        td(),
        td(),
        td(child.text <-- dataSignal.map(data => "%.2f".format(data.map(_.fullPrice).sum))),
      ))
    )
  end renderDataTable

  def renderDataItem(id: DataItemID, itemSignal: Signal[DataItem]): Element =
    tr(
      td(inputForString(
        itemSignal.map(_.label),
        makeDataItemUpdater[String](id, (item, newLabel) => item.copy(label = newLabel))
      )),
      td(inputForDouble(
        itemSignal.map(_.price),
        makeDataItemUpdater(id, { (item, newPrice) => item.copy(price = newPrice) })
      )),
      td(inputForInt(
        itemSignal.map(_.count),
        makeDataItemUpdater(id, { (item, newCount) => item.copy(count = newCount) })
      )),
      td(child.text <-- itemSignal.map(item => "%.2f".format(item.fullPrice))),
      td(button(cls := "btn btn-primary", "🗑️", onClick --> (_ => removeDataItem(id)))),
    )
  end renderDataItem

  def makeDataItemUpdater[A](id: DataItemID, f: (DataItem, A) => DataItem): Observer[A] =
    dataVar.updater { (data, newValue) =>
      data.map { item => if item.id == id then f(item, newValue) else item }
    }
  end makeDataItemUpdater

  def inputForString(valueSignal: Signal[String], valueUpdater: Observer[String]): Input =
    input(
      typ := "text",
      value <-- valueSignal,
      onInput.mapToValue --> valueUpdater
    )

  def inputForDouble(valueSignal: Signal[Double], valueUpdater: Observer[Double]): Input =
    val strValue = Var[String]("")
    input(
      typ := "text",
      value <-- strValue.signal,
      onInput.mapToValue --> strValue,
      valueSignal --> strValue.updater[Double] { (prevStr, newValue) =>
        if prevStr.toDoubleOption.contains(newValue) then prevStr
        else newValue.toString
      },
      strValue.signal --> { valueStr =>
        valueStr.toDoubleOption.foreach(valueUpdater.onNext)
      },
    )
  end inputForDouble

  def inputForInt(valueSignal: Signal[Int], valueUpdater: Observer[Int]): Input =
    input(
      typ := "text",
      controlled(
        value <-- valueSignal.map(_.toString),
        onInput.mapToValue.map(_.toIntOption).collect {
          case Some(newCount) => newCount
        } --> valueUpdater,
      ),
    )
  end inputForInt

  def renderDataList(): Element =
    ul(
      children <-- dataSignal.split(_.id) { (id, initial, itemSignal) =>
        li(child.text <-- itemSignal.map(item => s"${item.count} ${item.label}"))
      }
    )

  val chartConfig: ChartConfiguration[Any, Any, Any] =
    jsLiteral(
      "type" -> "bar",
      "data" -> jsLiteral(
        "labels" -> js.Array[String](),
        "datasets" -> js.Array(
          jsLiteral(
            "label" -> "Price",
            "data" -> js.Array[Double](),
            "borderWidth" -> 1,
            "backgroundColor" -> "rgba(75, 192, 192, 0.6)"
          ),
          jsLiteral(
            "label" -> "Full Price",
            "data" -> js.Array[Double](),
            "borderWidth" -> 1,
            "backgroundColor" -> "rgba(54, 162, 235, 0.6)"
          )
        )
      ),
      "options" -> jsLiteral(
        "scales" -> jsLiteral(
          "y" -> jsLiteral(
            "beginAtZero" -> true
          )
        )
      )
    ).asInstanceOf[ChartConfiguration[Any, Any, Any]]

  def renderDataChart(): Element =
    import scala.scalajs.js.JSConverters.*

    var optChart: Option[Chart[Any, Any, Any]] = None

    canvasTag(
      // Regular properties of the canvas
      width := "100%",
      height := "200px",

      // onMountUnmount callback to bridge the Laminar world and the Chart.js world
      onMountUnmountCallback(
        // on mount, create the `Chart` instance and store it in optChart
        mount = { nodeCtx =>
          val domCanvas: ChartItem = nodeCtx.thisNode.ref
          val chart = new Chart[Any, Any, Any](domCanvas, chartConfig)
          optChart = Some(chart)
        },
        // on unmount, destroy the `Chart` instance
        unmount = { thisNode =>
          for (chart <- optChart)
            chart.destroy()
          optChart = None
        }
      ),

      // Bridge the FRP world of dataSignal to the imperative world of the `chart.data`
      dataSignal --> { data =>
        for (chart <- optChart) {
          val chartData = chart.data.asInstanceOf[js.Dynamic]
          val datasets = chartData.selectDynamic("datasets").asInstanceOf[js.Array[js.Dynamic]]
          
          // Update labels
          chartData.updateDynamic("labels")(data.map(_.label).toJSArray)
          
          // Update dataset 0 (Price)
          datasets(0).updateDynamic("data")(data.map(_.price).toJSArray)
          
          // Update dataset 1 (Full Price)
          datasets(1).updateDynamic("data")(data.map(_.fullPrice).toJSArray)
          
          // Update the chart
          chart.update()
        }
      },
    )
