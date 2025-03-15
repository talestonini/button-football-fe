package com.talestonini

import com.raquo.laminar.api.L.{*, given}

package object buttonfootball:

  def renderCardTitle(title: String, marginBottom: String = "mb-2"): Element =
    h6(
      cls := s"card-subtitle ${marginBottom} text-muted",
      b(title)
    )

  object Logo:

    private val EXTENSION = "bmp"

    def forChampionshipType(logoImgFile: String, isLarge: Boolean = false) =
      s"/img/championships/${if (isLarge) "125" else "45"}/${treat(logoImgFile)}.$EXTENSION"

    def forTrophy(logoImgFile: String) =
      s"/img/championships/trophies/125/${treat(logoImgFile)}.$EXTENSION"

    def forTeam(logoImgFile: String, isLarge: Boolean = false) =
      s"/img/teams/${if (isLarge) "150" else "40"}/${treat(logoImgFile)}.$EXTENSION"

    private def treat(name: String) = name.toLowerCase()

  end Logo

end buttonfootball