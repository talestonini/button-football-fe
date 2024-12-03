package com.talestonini.buttonfootball

import com.raquo.laminar.api.L.{*, given}
import scala.scalajs.js
import scala.scalajs.js.annotation.*

import org.scalajs.dom
import com.talestonini.buttonfootball.model.Model
import com.talestonini.buttonfootball.model.DataItemID
import com.talestonini.buttonfootball.model.DataItem

// import javascriptLogo from "/javascript.svg"
@js.native @JSImport("/javascript.svg", JSImport.Default)
val javascriptLogo: String = js.native

@main
def ButtonFootballFrontEnd(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Main.appElement()
  )

object Main:
  val model = new Model
  import model.* 

  def appElement(): Element =
    div(
      h1("Button Football"),
      renderDataTable(),
      renderDataList(),
    )

  def renderDataTable(): Element =
    table(
      thead(tr(th("Label"), th("Price"), th("Count"), th("Full Price"), th("Action"))),
      tbody(
        children <-- dataSignal.split(_.id) { (id, initial, itemSignal) => renderDataItem(id, itemSignal) }
      ),
      tfoot(tr(
        td(button("➕", onClick --> (_ => addDataItem(DataItem())))),
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
      td(button("🗑️", onClick --> (_ => removeDataItem(id)))),
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
end Main