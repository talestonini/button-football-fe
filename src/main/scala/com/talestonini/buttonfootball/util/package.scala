package com.talestonini.buttonfootball

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.model.vTeams
import com.talestonini.util.*
import org.scalajs.dom

package object util {
  
  object Logo:
    private val EXTENSION = "bmp"

    def forChampionshipTypeImgFile(logoImgFile: String): String =
      s"/img/championships/125/${treat(logoImgFile)}.$EXTENSION"

    def forTrophyImgFile(logoImgFile: String): String =
      s"/img/championships/trophies/125/${treat(logoImgFile)}.$EXTENSION"

    def forTeamImgFile(logoImgFile: String): String =
      s"/img/teams/150/${treat(logoImgFile)}.$EXTENSION"

    def forTeamName(teamName: String): Option[String] =
      vTeams.now().find(t => t.name == teamName)
        .map(t => forTeamImgFile(t.logoImgFile))

    private def treat(name: String) = name.toLowerCase()
  end Logo

  def cardTitle(title: String, marginBottom: String = "mb-2"): Element =
    h6(
      cls := s"card-subtitle ${marginBottom} text-muted",
      b(title)
    )

  def maybeScaleFontDown(): String =
    if (Window.size() == Window.Size.Small)
      "font-size: 0.6rem"
    else
      "font-size: 1rem"

  def maybeScaleFontUp(): String =
    if (Window.size() == Window.Size.Large)
      "font-size: 2rem"
    else if (Window.size() == Window.Size.Medium)
      "font-size: 1.5rem"
    else
      "font-size: 1rem"

  def spacingStyle(spacingType: String, min: Option[Int] = None, max: Option[Int] = None): String =
    val defaultLgSize = 3
    val defaultMdSize = 2
    val defaultSmSize = 1

    def styleSize(default: Int) =
      if (default < min.getOrElse(default)) min.getOrElse(default)
      else if (default > max.getOrElse(default)) max.getOrElse(default)
      else default

    val lgSize = styleSize(defaultLgSize)
    val mdSize = styleSize(defaultMdSize)
    val smSize = styleSize(defaultSmSize )

    s"$spacingType-lg-${lgSize} $spacingType-md-${mdSize} $spacingType-${smSize}"
  end spacingStyle

}