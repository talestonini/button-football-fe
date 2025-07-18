package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.datastructure.*
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Matches.Match
import com.talestonini.buttonfootball.util.*
import com.talestonini.util.*

import java.lang.Math.{log, pow}
import scala.collection.immutable.NumericRange

/**
  * Renders the tab for the finals matches, which has its own unique layout.
  * 
  *    | A       C       E       G       |  (cols B, D and F are used to render linking curves)
  *  0 -----------------------------------  (empty row)
  *  1 | match                           |
  *  2 |         match                   |
  *  3 | match                           |
  *  4 |                 match           |
  *  5 | match                           |
  *  6 |         match                   |
  *  7 | match                           |
  *  8 |                         match   |
  *  9 | match                           |
  * 10 |         match                   |
  * 11 | match                           |
  * 12 |                 match           |
  * 13 | match                           |
  * 14 |         match                   |
  * 15 | match                           |
  * 16 |                         match   |
  *
  * It's a table with dimensions deriving off the number of qualified teams from the groups stage (this number is
  * calculated and needs to match field 'numQualif' in the Championship model).  Remember the table needs to be rendered
  * top to bottom, left to right (not by indexing/pinpointing a cell).
  * 
  * There is an algorithm that builds a tree structure to represent the games in the finals stages.  The tree root is
  * the grand-final match and nodes are recursively created to calculate the cell where they must be rendered within the
  * table mentioned above.  The rendering process scans the table top-bottom, left-right and for each cell it tries to
  * find a node in the tree whose position matches the current cell.  Where there is a match, the finals match is
  * rendered.
  * 
  * The funneling effect of a cup-format championship is represented by curves linking matches that qualify winners of
  * the round-of-sixteen to quarter-finals then to semi-finals and so on.  The table cells' bounding boxes are used to
  * calculate the positions of such curves (Bézier curves).  We need empty columns B, D and F to help accomplish the
  * effect (this is for a championship where 16 teams qualify - these gap columns as well as anything in this tab derive
  * from the number of qualified teams).
  */
object FinalsMatchesTabContent:

  private val coordsAnchorElemId = "coordinatesAnchorElementId"

  // --- funneling tree number of levels -------------------------------------------------------------------------------

  private val numLevelsFn = (numQualif: Int) =>
    if (numQualif == 0) 0
    else (log(numQualif)/log(2)).toInt

  // --- table cols and rows -------------------------------------------------------------------------------------------

  /**
    * There are 'A' until last col columns in the table.  Last col is a function of the number of qualified teams.  For
    * example: if 16 teams qualify, then we need 4 columns to transition all the way from the round-of-sixteen matches
    * to the grand final.  But because we use an in-between col to draw the links in SVG, we double the number of cols.
    * In the example above, last col is 8.  Then there are basic conversions to Char involved.
    */
  private val minCol: Char = 'A'
  private val minRow: Int = 0
  private val lastColFn = (numQualif: Int) => (minCol.toInt + numLevelsFn(numQualif) * 2 - 1).toChar
  val sCols: Signal[NumericRange.Exclusive[Char]] = sNumQualif.map(nq => minCol until lastColFn(nq))
  val sRows: Signal[Range] = sNumQualif.map(nq => minRow until nq)
  private val sMaxCol: Signal[Char] = sCols.map(cs => if (!cs.isEmpty) cs.last else minCol)
  private val sMaxRow: Signal[Int] = sRows.map(rs => if (!rs.isEmpty) rs.last else minRow)

  private val cellAddressFn = (col: Char, row: Int) => s"$col$row"
  case class Cell(col: Char, row: Int) {
    def address(): String = cellAddressFn(col, row)
  }
  
  // --- the funneling tree --------------------------------------------------------------------------------------------

  /**
   * General algorithm:
   *
   * 1) Build the funneling tree deriving from the number of qualified teams and the qualified teams themselves
   * (combination of the 2 signals).  Build starts from the grand final match, and progresses to the first stage after
   * the groups stage (eg round of sixteen or quarter-finals).  Nodes contain:
   * a) match seeding, eg seed 1 vs 8, 2 vs 7, etc (calculated)
   * b) table cell where to render the node, eg A1, C2 (calculated)
   * c) cells where 'to' render linking curve, eg the grand final cell links to the semifinals cells (calculated)
   * d) teams A and B (when hitting the leaf, fetch the qualified teams by the seed numbers of the leaf)
   *
   * 2) While building the table (main Element returned by `apply`), traverse the tree (map) for each cell to:
   * a) render cell links
   * b) render teams A and B (here we fetch the match by team names to get scores, etc)
   *
   * NOTES:
   * 1) As we traverse the tree, we must save the 'static' cell links into a list, so that curves can be re-rendered
   * when the window is scrolled or resized or double-clicked.
   * 2) The beauty of the algorithm lies in the fact that we can calculate everything at tree build time and have it
   * ready to be traversed (multiple times) when rendering the table.  It's not easy to avoid traversing the tree many
   * times, because the algorithm is essentially crossing a table (HTML Element) with the data plotted on it from a
   * tree.  We could render the table in one passage and then traverse the tree once, indexing the table cells to
   * "pinpoint" where to render elements from the tree, but that would involve naming cells with indexes and doing
   * "dom.document.getElementById", which is not native Laminar (so I'll limit that technique only to rendering Bézier
   * curves).  Plus, traversing a small tree many times is cheap.
   */
  private case class MatchCell(seed: Int, otherSeed: Int, cell: Cell, toCells: List[Cell], var `match`: Option[Match])
  private val sFunnelingTree: Signal[Tree[MatchCell]] =
    sNumQualif
      .combineWith(sCols)
      .combineWith(sRows)
      .combineWith(sQualifiedTeams)
      .combineWith(sFinalsMatches).map { case (nq, cs, rs, qts, fms) =>
        // build the tree
        val tree = if (nq == 0) Empty
        else {
          val numLevels = numLevelsFn(nq)

          def insertNode(level: Int, seed: Int, fromCell: Cell): Tree[MatchCell] =
            if (level > numLevels)
              Empty
            else {
              val otherSeed = pow(2, level).toInt - seed + 1
              val toCol     = (fromCell.col - 2).toChar  // there's a column gap
              val rowDiff   = nq/pow(2, level + 1).toInt
              val toCell1   = Cell(toCol, fromCell.row - rowDiff)
              val toCell2   = Cell(toCol, fromCell.row + rowDiff)
              val (toCells, theMatch) = 
                if (level == numLevels) {
                  // there must be a match between qualified teams (this is the level right after the group stage)
                  val teamA = qts.find(qt => qt.pos == seed).map(_.team).getOrElse("")
                  val teamB = qts.find(qt => qt.pos == otherSeed).map(_.team).getOrElse("")
                  (List.empty, fms.find(m => m.teamA == teamA && m.teamB == teamB))
                } else {
                  (List(toCell1, toCell2), None)
                }
              Node(
                MatchCell(seed, otherSeed, fromCell, toCells, theMatch),
                insertNode(level + 1, seed, toCell1),
                insertNode(level + 1, otherSeed, toCell2)
              )
            }

          insertNode(1, 1, Cell(cs.last, rs.last/2))
        }

        // traverse the tree to set all remaining matches (needs to be done after building the tree as opposed to on the
        // same passage, because we only know about each match winner and loser by navigating from the leaves)
        def setTreeRootFinalMatch(tree: Tree[MatchCell]): Unit = tree match {
          case Node(value, left, right) => 
            if (value.`match`.isEmpty) {
              setTreeRootFinalMatch(left)
              setTreeRootFinalMatch(right)
              val leftWinner  = getFromSubtreeRootMatch(left, m => m.winner()).getOrElse("")
              val rightWinner = getFromSubtreeRootMatch(right, m => m.winner()).getOrElse("")
              val finalMatch  = fms.find(m => m.teamA == leftWinner && m.teamB == rightWinner)
              value.`match`   = finalMatch  // match is mutable
            }
        }

        setTreeRootFinalMatch(tree)
        tree
      }

  private def thirdPlacePlayoff(tree: Tree[MatchCell], finalsMatches: List[Match]): Option[Match] =
    tree match {
      case Empty => None
      case Node(value, left, right) => 
        if (value.`match`.isEmpty) None
        else {
          val leftLoser  = getFromSubtreeRootMatch(left, m => m.loser()).getOrElse("")
          val rightLoser = getFromSubtreeRootMatch(right, m => m.loser()).getOrElse("")
          finalsMatches.find(m => m.teamA == leftLoser && m.teamB == rightLoser)
        }
    }

  private def getFromSubtreeRootMatch[T](subtree: Tree[MatchCell], whatToGet: Match => Option[T]): Option[T] =
    subtree match {
      case Empty => None
      case Node(value, left, right) =>
        if (value.`match`.isEmpty) None
        else whatToGet(value.`match`.get)
    }

  // --- cell links ----------------------------------------------------------------------------------------------------

  private val cellLinkAddressFn = (fromCell: Cell, toCell: Cell) => s"${fromCell.address()}-${toCell.address()}"
  private def sCellLinks(): Signal[List[Element]] =
    vActiveTab.signal.combineWith(sFunnelingTree).map { case (at, ft) => ft.map(n =>
      def svgForCurve(fromCell: Cell, toCell: Cell): Element =
        import svg.*
        svg(
          style := "position: absolute; top: 0; left: 0; pointer-events: none;",
          width := Elem.scrollWidth(coordsAnchorElemId),
          height := Elem.scrollHeight(coordsAnchorElemId),
          path(
            idAttr := cellLinkAddressFn(fromCell, toCell),
            stroke := "lightgrey",
            strokeWidth := "3",
            fill := "transparent",
            d := bezierCurveCommands(fromCell, toCell)
          )
        )
      end svgForCurve

      if (at != FINALS_TAB || n.toCells.isEmpty)
        div()
      else 
        div(
          svgForCurve(n.cell, n.toCells.head),
          svgForCurve(n.cell, n.toCells.tail.head)
        )
    ).toList()}
  
  private def bezierCurveCommands(fromCell: Cell, toCell: Cell): String = {
    val anchorElem    = Some(coordsAnchorElemId)
    val fromBox       = Bounding.box(fromCell.address(), anchorElem)
    val toBox         = Bounding.box(toCell.address(), anchorElem)
    val startingPoint = s"${fromBox.left},${fromBox.top + fromBox.height/2}"
    val controlPoint1 = s"${toBox.right},${fromBox.top + fromBox.height/2}"
    val controlPoint2 = s"${fromBox.left},${toBox.top + toBox.height/2}"
    val endingPoint   = s"${toBox.right},${toBox.top + toBox.height/2}"
    s"M$startingPoint C$controlPoint1 $controlPoint2 $endingPoint"
  }

  // --- public API ----------------------------------------------------------------------------------------------------

  def apply(): Element =
    def finalsMatch(m: Match): Element =
      div(
        cls := "card",
        div(
          cls := "card-body p-2",
          cardTitle(m.`type`, "mb-0"),
          table(
            cls := "table mb-0 table-borderless align-middle",
            tbody(MatchTableRow(m, isFinalsStage = true))
          )
        )
      )

    div(
      cls := "border d-flex justify-content-center",
      child <-- sNumQualif.map(nq => if (nq <= 0) div() else div(
        idAttr := coordsAnchorElemId,
        buildStyleAttr("overflow-x: auto", "position: relative"),
        table(
          cls := "table table-borderless mt-3",
          styleAttr := "width: 0%",
          tbody(
            children <-- sRows.combineWith(sMaxRow).map((rs, maxr) => rs.map(r =>
              tr(
                children <-- sCols.combineWith(sMaxCol).map((cs, maxc) => cs.map(c =>
                  td(
                    idAttr := cellAddressFn(c, r),
                    cls := "col text-center bg-light",
                    child <-- sFunnelingTree.combineWith(sFinalsMatches).map((ft, fms) => {
                      if (c == maxc && r == maxr) {
                        val tpp = thirdPlacePlayoff(ft, fms)
                        if (tpp.isEmpty) ""
                        else finalsMatch(tpp.get)
                      } else {
                        val mc = ft.findFirst(mc => mc.cell == Cell(c, r))
                        if (mc.isEmpty || mc.get.`match`.isEmpty) ""
                        else finalsMatch(mc.get.`match`.get)
                      }
                    })
                  )
                ))
              )
            ))
          )
        ),
        // SVG elements "host" Bézier curves that link cells, drawing the funneling of finals matches
        children <-- sCellLinks()
      ))
    )
  end apply

end FinalsMatchesTabContent