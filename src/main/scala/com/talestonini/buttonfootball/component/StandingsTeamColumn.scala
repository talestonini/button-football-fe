package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.component.LogoImage.*
import com.talestonini.buttonfootball.util.Logo
import com.talestonini.component.Table.Column
import com.talestonini.util.Window.Size

object StandingsTeamColumn:

  def apply(windowSize: Size): Column =
    Column("", 4, "text-center", true, Some((teamLogoImgFile: String) =>
      LogoImage(Logo.forTeamImgFile(teamLogoImgFile), Some(XSMALL_LOGO_PX_SIZE))
    ))

end StandingsTeamColumn