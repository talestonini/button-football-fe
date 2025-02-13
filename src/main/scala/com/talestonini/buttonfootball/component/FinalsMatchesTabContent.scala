package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.model.activeTab
import com.talestonini.buttonfootball.model.FINALS_TAB
import org.scalajs.dom

/**
  * Renders the tab for the finals matches, which has its own unique layout.  Follow the schematic below for a general
  * understanding of the logic:
  * 
  *    | A        C        E        G        (cols B, D and F are empty)
  *  0 ------------------------------------- (emtpy row)
  *  1 | match           |        |        |
  *  2 |          match  |        |        |
  *  3 | match           |        |        |
  *  4 |          match? | match  |        |
  *    -------------------        |        |
  *  5 | match                    |        |
  *  6 |          match           |        |
  *  7 | match                    |        |
  *  8 |          match?   match? | match  |
  *    ----------------------------        |
  *  9 | match                             |
  * 10 |          match                    |
  * 11 | match                             |
  * 12 |          match?   match           |
  * 13 | match                             |
  * 14 |          match                    |
  * 15 | match                             |
  * 16 |          match?   match?   match  |
  * 17 ------------------------------------- (empty row)
  *               
  * It's a table, and it has repeatable sections, depending on how many finals matches there are in a given
  * championship.  That in turn is determined by how many teams qualify from the groups stage, and this number comes
  * from field 'numQualif' in the Championship model.  Remember the table needs to be rendered top to bottom, and not by
  * indexing a cell.
  * 
  * The funelling effect of a cup-format championship is represented by curves drawn from matches that qualify winners
  * of the round-of-sixteen to quarter-finals then to semi-finals and so on.  The table cells' bounding boxes are used
  * to calculate the positions of such curves and we need empty columns B, D and F to help accomplish the effect.
  */
object FinalsMatchesTabContent:

  private val BLANK_COLS = List('B', 'D', 'F')
  private val cols = ('A' to 'G')
  private val rows = (0 to 16)

  private val cellLinks: List[CellLink] = List(
    // round of sixteen to quarter finals
    CellLink("link1", Cell('A', 1), Cell('C', 2)),
    CellLink("link2", Cell('A', 3), Cell('C', 2)),
    CellLink("link3", Cell('A', 5), Cell('C', 6)),
    CellLink("link4", Cell('A', 7), Cell('C', 6)),
    CellLink("link5", Cell('A', 9), Cell('C', 10)),
    CellLink("link6", Cell('A', 11), Cell('C', 10)),
    CellLink("link7", Cell('A', 13), Cell('C', 14)),
    CellLink("link8", Cell('A', 15), Cell('C', 14)),

    // quarter finals to semifinals
    CellLink("link9", Cell('C', 2), Cell('E', 4)),
    CellLink("link10", Cell('C', 6), Cell('E', 4)),
    CellLink("link11", Cell('C', 10), Cell('E', 12)),
    CellLink("link12", Cell('C', 14), Cell('E', 12)),
  
    // semifinals to final
    CellLink("link13", Cell('E', 4), Cell('G', 8)),
    CellLink("link14", Cell('E', 12), Cell('G', 8))
  )
  
  def apply() =
    div(
      table(
        cls := "table table-borderless",
        tbody(
          rows.map(r => 
            tr(
              cols.map(c =>
                td(
                  idAttr := cellAddressFn(c, r),
                  cls := "col text-center",
                  if (BLANK_COLS.contains(c)) "" else Cell(c, r).address()
                )
              )
            )
          )
        )
      ),
      renderSvgElements(), // SVG elements "host" Bezier curves that link cells, drawing the funelling of finals matches
      onMountCallback(context => renderSvgCurves()) // renders the curves only after the SVG elements are rendered
    )

  /**
    * Sets up redrawing of the SVG Bezier curves when the window is resized or scrolled.
    */
  def setupSvgCurvesAutoRender(): Unit = {
    dom.window.addEventListener("resize", (_: dom.Event) => renderSvgCurves())
    dom.window.addEventListener("scroll", (_: dom.Event) => renderSvgCurves())
  }
  
  private val cellAddressFn = (col: Char, row: Int) => s"$col$row"
  
  private case class Cell(col: Char, row: Int) {
    def address(): String = cellAddressFn(col, row)
    def rect() = dom.document.getElementById(address()).getBoundingClientRect()
  }
  
  private case class CellLink(id: String, fromCell: Cell, toCell: Cell)

  private def renderSvgElements(): List[Element] =
    import svg.*
    cellLinks.map(cl =>
      svg(
        style := "position: absolute; top: 0; left: 0; pointer-events: none;",
        width := "100%",
        height := "100%",
        path(
          idAttr := cl.id,
          stroke := "lightgrey",
          strokeWidth := "3",
          fill := "transparent"
        )
      )
    )
  end renderSvgElements
  
  private def renderSvgCurves(): Unit =
    def bezierCurveCommands(fromCell: Cell, toCell: Cell): String = {
      val fromRect = fromCell.rect()
      val toRect = toCell.rect()
      val startingPoint = s"${fromRect.right},${fromRect.top + fromRect.height/2}"
      val controlPoint1 = s"${toRect.left},${fromRect.top + fromRect.height/2}"
      val controlPoint2 = s"${fromRect.right},${toRect.top + toRect.height/2}"
      val endingPoint   = s"${toRect.left},${toRect.top + toRect.height/2}"
      s"M$startingPoint C$controlPoint1 $controlPoint2 $endingPoint"
    }

    if (activeTab.now() == FINALS_TAB) cellLinks.foreach(cl => 
      dom.document.getElementById(cl.id).setAttribute("d", bezierCurveCommands(cl.fromCell, cl.toCell))
    )
  end renderSvgCurves
  
end FinalsMatchesTabContent