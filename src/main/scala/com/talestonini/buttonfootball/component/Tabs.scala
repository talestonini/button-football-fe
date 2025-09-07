package com.talestonini.buttonfootball.component

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.features.unitArrows
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.service.*

object Tabs:

  private def tabContent(tabName: String, isLoading: Boolean): Element =
    div(
      cls := "text-center",
      Spinner(),
      if (isLoading)
        div()
      else 
        if (tabName.startsWith(GROUP))
          GroupMatchesTabContent(tabName)
        else if (tabName == FINALS_TAB)
          FinalsMatchesTabContent()
        else if (tabName == FINAL_STANDINGS_TAB)
          FinalStandingsTabContent()
        else
          div()
    )

  def apply(): Element =
    div(
      cls := "border rounded shadow",
      ul(
        cls := "nav nav-tabs",
        children <-- sTabs.map(ts => ts.map(t =>
          li(
            cls := "nav-item",
            button(
              cls := "nav-link text-muted fw-bold",
              cls <-- vActiveTab.signal.map(at => if (t == at) "active" else ""),
              onClick --> vActiveTab.update(ev => t),
              text <-- I18n(t, TabTranslationMap)
            )
          )
        )),
      ),
      div(
        cls := "tab-content p-0",
        child <-- vActiveTab.signal.combineWith(vIsLoading.signal).map((activeTab, isLoading) =>
          tabContent(activeTab, isLoading)
        )
      )
    )
  
end Tabs