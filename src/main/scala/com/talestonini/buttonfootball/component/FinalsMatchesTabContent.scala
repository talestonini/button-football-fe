package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.datastructure.*
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Matches.Match
import com.talestonini.buttonfootball.renderCardTitle
import org.scalajs.dom
import org.scalajs.dom.DOMRect

import java.lang.Math.{log, pow}
import scala.collection.immutable.NumericRange

/**
  * Renders the tab for the finals matches, which has its own unique layout.  Follow the schematic below for a general
  * understanding of the logic:
  * 
  *    | A        C        E        G        (cols B, D and F are empty)
  *  0 ------------------------------------- (empty row)
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
  * The funneling effect of a cup-format championship is represented by curves drawn from matches that qualify winners
  * of the round-of-sixteen to quarter-finals then to semi-finals and so on.  The table cells' bounding boxes are used
  * to calculate the positions of such curves, and we need empty columns B, D and F to help accomplish the effect.
  */
object FinalsMatchesTabContent:

  private def calcNumLevels(numQualif: Int): Int =
    if (numQualif == 0) 0
    else (log(numQualif)/log(2)).toInt

  /**
    * There are 'A' until last col columns in the table.  Last col is a function of the number of qualified teams.  For
    * example: if 16 teams qualify, then we need 4 columns to transition all the way from the round-of-sixteen matches
    * to the grand final.  But because we use an in-between col to draw the links in SVG, we double the number of cols.
    * In the example above, last col is 8.  Then there are basic conversions to Char involved.
    *
    * @param numQualif the number of qualified teams to the finals
    * @return the last column as a Char
    */
  private def lastCol(numQualif: Int): Char = ('A'.toInt + calcNumLevels(numQualif) * 2 - 1).toChar
  val cols: Signal[NumericRange.Exclusive[Char]] = numQualif.map(nq => ('A' until lastCol(nq)))
  val rows: Signal[Range.Inclusive] = numQualif.map(nq => 0 to nq)

  private val cellAddressFn = (col: Char, row: Int) => s"$col$row"
  case class Cell(col: Char, row: Int) {
    def address(): String = cellAddressFn(col, row)
    def rect(): DOMRect = dom.document.getElementById(address()).getBoundingClientRect()
    def render(): Element =
      div(
        cls := "card h-100 w-100",
        div(
          cls := "card-body",
          renderCardTitle(address())
        )
      )
  }
  
  /**
   * General algorithm:
   *
   * 1) Build the funneling tree deriving from the number of qualified teams and the qualified teams themselves (com-
   * bination of the 2 signals).  Build starts from the grand final match, and progresses to the first stage after
   * the groups stage (eg round of sixteen or quarter-finals).  Nodes contain:
   * a) match seeding, eg seed 1 vs 8, 2 vs 7, etc (calculated)
   * b) table cell where to render the node, eg A1, C2 (calculated)
   * c) cells where 'to' render linking curve, eg the grand final cell links to the semifinals cells (calculated)
   * d) teams A and B (when hitting the leaf, fetch the qualified team by the seed number of the node)
   *
   * 2) While building the table (main Element above), traverse the tree (map) for each cell to:
   * a) render cell links
   * b) render teams A and B (here we fetch the match by team names to get scores, etc)
   *
   * NOTES:
   * 1) As we traverse the tree, we must save the 'static' cell links into a list, so that curves can be re-rendered
   * when the window is scrolled or resized.
   * 2) The beauty of the algorithm lies in the fact that we can calculate everything at tree build time and have it
   * ready to be traversed (multiple times) when rendering the table.  It's not easy to avoid traversing the tree
   * many times, because the algorithm is essentially crossing a table (HTML Element) with the data plotted on it
   * from a tree.  We could render the table in one passage and then traverse the tree once, indexing the table
   * cells to "pinpoint" where to render elements from the tree, but that would involve naming cells with indexes
   * and doing "dom.document.getElementById", which is not native Laminar (so I'll limit that technique only to
   * rendering Bézier curves for now).  Plus, I'm betting traversing a small tree many times will be cheap.
   */

  private case class MatchCell(seedA: Int, seedB: Int, cell: Cell, toCells: List[Cell], `match`: Var[Option[Match]])

  private val funnelingTree: Signal[Tree[MatchCell]] =
    numQualif.combineWith(cols).combineWith(rows).map { case (nq, cs, rs) =>
      if (nq == 0) Empty
      else {
        val numLevels = calcNumLevels(nq)

        def insertNode(level: Int, seed: Int, fromCell: Cell): Tree[MatchCell] =
          if (level > numLevels)
            Empty
          else {
            val seedA   = seed
            val seedB   = pow(2, level).toInt - seed + 1
            val toCol   = (fromCell.col - 2).toChar  // there's a column gap
            val toRow1  = fromCell.row - nq/pow(2, level+1).toInt
            val toRow2  = fromCell.row + nq/pow(2, level+1).toInt
            val toCell1 = Cell(toCol, toRow1)
            val toCell2 = Cell(toCol, toRow2)
            val toCells = if (level < numLevels) List(toCell1, toCell2) else List.empty
            Node(
              MatchCell(seedA, seedB, fromCell, toCells, Var(None)),
              insertNode(level+1, seedA, toCell1),
              insertNode(level+1, seedB, toCell2)
            )
          }

        insertNode(1, 1, Cell(cs.last, rs.last/2))
      }
    }

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
                  child <-- funnelingTree.map(ft =>
                    if (ft.findFirst(mc => mc.cell == Cell(c, r)).isDefined) Cell(c, r).render() else ""
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

  /**
    * Used to re-render cell links when the window is scrolled or resized.  The first time cell links are rendered 
    * driven by signals, the static cell links are saved as a copy of the current cell links.  If then the window is
    * scrolled or resized, re-render of the cell links (curves) is driven by the saved static cell links, as opposed to
    * signals, which are not available then.
    */
  private var staticCellLinks: List[(Cell, Cell)] = List.empty

  private val cellLinkAddressFn = (fromCell: Cell, toCell: Cell) => s"${fromCell.address()}-${toCell.address()}"
  private def renderCellLinks(): Signal[List[Element]] =
    activeTab.signal.combineWith(funnelingTree).map { case (at, ft) => ft.map(n =>
      def saveStaticCellLinks() =
        staticCellLinks = ft.toList().flatMap(n =>
          if (n.toCells.isEmpty)
            List.empty
          else
            List((n.cell, n.toCells.head), (n.cell, n.toCells.tail.head))
        )
    
      def svgForCurve(fromCell: Cell, toCell: Cell): Element =
        import svg.*
        svg(
          style := "position: fixed; top: 0; left: 0; pointer-events: none;",
          width := "100%",
          height := "100%",
          path(
            idAttr := cellLinkAddressFn(fromCell, toCell),
            stroke := "lightgrey",
            strokeWidth := "3",
            fill := "transparent",
            d := bezierCurveCommands(fromCell, toCell)
          )
        )
      end svgForCurve

      saveStaticCellLinks()
      if (at != FINALS_TAB || n.toCells.isEmpty)
        div()
      else 
        div(
          svgForCurve(n.cell, n.toCells.head),
          svgForCurve(n.cell, n.toCells.tail.head)
        )
    ).toList()}
  
  /**
    * Sets up re-rendering of the SVG Bezier curves when the window is resized or scrolled.
    */
  def setupAutoReRenderOfCellLinksOnWindowScrollAndResize(): Unit = {
    dom.window.addEventListener("scroll", (_: dom.Event) => renderStaticCellLinks())
    dom.window.addEventListener("resize", (_: dom.Event) => renderStaticCellLinks())
  }
  
  private def renderStaticCellLinks(): Unit = staticCellLinks.map(cl => dom.document
    .getElementById(cellLinkAddressFn(cl._1, cl._2))
    .setAttribute("d", bezierCurveCommands(cl._1, cl._2))
  )

  private def bezierCurveCommands(fromCell: Cell, toCell: Cell): String = {
    val fromRect = fromCell.rect()
    val toRect = toCell.rect()
    val startingPoint = s"${fromRect.left},${fromRect.top + fromRect.height/2}"
    val controlPoint1 = s"${toRect.right},${fromRect.top + fromRect.height/2}"
    val controlPoint2 = s"${fromRect.left},${toRect.top + toRect.height/2}"
    val endingPoint   = s"${toRect.right},${toRect.top + toRect.height/2}"
    s"M$startingPoint C$controlPoint1 $controlPoint2 $endingPoint"
  }

end FinalsMatchesTabContent