package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.renderCardTitle
import com.talestonini.buttonfootball.service.ChampionshipService.calcNumQualif
import java.lang.Math.log
import org.scalajs.dom
import scala.collection.immutable.NumericRange

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
  * 16 |          match?   match?   match? |
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

  /**
    * There are 'A' until last col columns in the table.  Last col is a function of the number of qualified teams.  For
    * example: if 16 teams qualify, then we need 4 colums to transition all the way from the round-of-sixteen matches to
    * the grand final.  But because we use an in-between col to draw the links in SVG, we double the number of cols.  In
    * the example above, last col is 8.  Then there are basic conversions to Char involved.
    *
    * @param numQualif the number of qualified teams to the finals
    * @return the last column as a Char
    */
  private def lastCol(numQualif: Int): Char = ('A'.toInt + (log(numQualif)/log(2)).toInt * 2 - 1).toChar
  val cols: Signal[NumericRange.Exclusive[Char]] = numQualif.map(nq => ('A' until lastCol(nq)))
  val rows: Signal[Range.Inclusive] = numQualif.map(nq => 0 to nq)

  private val cellAddressFn = (col: Char, row: Int) => s"$col$row"
  case class Cell(col: Char, row: Int) {
    def address(): String = cellAddressFn(col, row)
    def rect() = dom.document.getElementById(address()).getBoundingClientRect()
    def render(): Element =
      div(
        cls := "card h-100 w-100",
        div(
          cls := "card-body",
          renderCardTitle(address())
        )
      )
  }
  
  case class CellLink(id: String, fromCell: Cell, toCell: Cell)
  private val INIT_CELL_LINKS: List[CellLink] = List(
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
  val cellLinks: Signal[List[CellLink]] = Var(INIT_CELL_LINKS).signal.combineWith(rows).combineWith(cols).map {
    case (cls, rs, cs) => cls.filter(cl => rs.contains(cl.toCell.row) && cs.contains(cl.toCell.col))
  }
  
  /**
    * Used to re-render cell links when the window is scrolled or resized.  The first time cell links are rendered 
    * driven by signals, the static cell links are saved as a copy of the current cell links.  If then the window is
    * scrolled or resized, re-render of the cell links (curves) is driven by the saved static cell links, as opposed to
    * signals, which are not available then.
    */
  private var staticCellLinks: List[CellLink] = List.empty

  private val matchCells: Signal[List[Cell]] = cellLinks.map(cls => {
    (cls.map(cl => cl.fromCell) :++ cls.map(cl => cl.toCell)).distinct
  })
  
  def apply(): Element =
    div(
      table(
        cls := "table table-borderless",
        tbody(
          children <-- rows.map(rs => rs.map(r =>
            tr(
              children <-- cols.map(cs => cs.map(c =>
                td(
                  idAttr := cellAddressFn(c, r),
                  cls := "col text-center",
                  child <-- matchCells.map(mcs =>
                    if (mcs.exists(cell => cell == Cell(c, r))) Cell(c, r).render() else ""
                  )
                )
              ))
            )
          ))
        )
      ),
      // SVG elements "host" Bezier curves that link cells, drawing the funelling of finals matches
      children <-- renderCellLinks()
    )

  private def renderCellLinks(): Signal[List[Element]] =
    activeTab.signal.combineWith(cellLinks).map { case (at, cls) => cls.map(cl =>
      // save "static" cell links
      staticCellLinks = cls

      import svg.*
      if (at != FINALS_TAB) 
        div()
      else
        svg(
          style := "position: fixed; top: 0; left: 0; pointer-events: none;",
          width := "100%",
          height := "100%",
          path(
            idAttr := cl.id,
            stroke := "lightgrey",
            strokeWidth := "3",
            fill := "transparent",
            d := bezierCurveCommands(cl.fromCell, cl.toCell)
          )
        )
    )}
  
  /**
    * Sets up re-rendering of the SVG Bezier curves when the window is resized or scrolled.
    */
  def setupAutoReRenderOfCellLinksOnWindowScrollAndResize(): Unit = {
    dom.window.addEventListener("scroll", (_: dom.Event) => renderStaticCellLinks())
    dom.window.addEventListener("resize", (_: dom.Event) => renderStaticCellLinks())
  }
  
  private def renderStaticCellLinks(): Unit =
    staticCellLinks.map(cl => 
      dom.document.getElementById(cl.id).setAttribute("d", bezierCurveCommands(cl.fromCell, cl.toCell))
    )

  private def bezierCurveCommands(fromCell: Cell, toCell: Cell): String = {
    val fromRect = fromCell.rect()
    val toRect = toCell.rect()
    val startingPoint = s"${fromRect.right},${fromRect.top + fromRect.height/2}"
    val controlPoint1 = s"${toRect.left},${fromRect.top + fromRect.height/2}"
    val controlPoint2 = s"${fromRect.right},${toRect.top + toRect.height/2}"
    val endingPoint   = s"${toRect.left},${toRect.top + toRect.height/2}"
    s"M$startingPoint C$controlPoint1 $controlPoint2 $endingPoint"
  }

end FinalsMatchesTabContent