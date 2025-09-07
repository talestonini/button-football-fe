package com.talestonini.buttonfootball.component

import com.raquo.airstream.flatten.FlattenStrategy.allowFlatMap
import com.raquo.laminar.api.L.{*, given}
import com.talestonini.buttonfootball.model.*
import com.talestonini.buttonfootball.model.Championships.*
import com.talestonini.buttonfootball.service.*
import com.talestonini.buttonfootball.util.*
import com.talestonini.buttonfootball.util.Logo.*

object ChampionshipsContent:

  private def championshipTypesRow(): Element =
    div(
      cls := s"row ${spacingStyle("pb", Some(3))} ${spacingStyle("gx")}",
      div(
        cls := "col d-flex align-items-center",
        select(
          cls := "form-select",
          children <-- vChampionshipTypes.signal.map(cts => cts.map(ct =>
            option(
              value := ct.code,
              text <-- I18n(ct.description, ChampionshipTypeTranslationMap)
            )
          )),
          onChange.mapToValue --> { code =>
            vSelectedChampionshipType.update(_ => vChampionshipTypes.now().find((ct) => ct.code == code))
            seGetChampionships(code)
          },
        )
      ),
      div(
        cls := "col-auto",
        child <-- vSelectedChampionshipType.signal.map({
          case Some(ct) => LogoImage(forChampionshipTypeImgFile(ct.logoImgFile))
          case None     => div()
        })
      )
    )

  private def championshipEditionsRangeRow(): Element = 
    div(
      cls := s"row ${spacingStyle("pb")}",
      label(
        cls := "form-label text-muted",
        forId := "championshipEditionRange",
        b(text <-- I18n(ChampionshipEditionToken))
      ),
      div(
        cls := "col",
        input(
          idAttr := "championshipEditionsRange",
          cls := "form-range",
          typ := "range",
          minAttr <-- vChampionships.signal.map(cs =>
            (if (cs.nonEmpty) MIN_CHAMPIONSHIP_EDITION else NO_CHAMPIONSHIP_EDITION).toString
          ),
          maxAttr <-- vChampionships.signal.map(cs => 
            (if (cs.nonEmpty) cs.length else NO_CHAMPIONSHIP_EDITION).toString),
          onChange.mapToValue --> { edition =>
            vSelectedChampionship.update(_ => vChampionships.now().find((c) => c.numEdition == edition.toInt))
            vSelectedEdition.update(_ => edition.toInt)
            seGetMatches(vSelectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
            // TODO: check whether using 2 APIs is a cleaner design - this is currently not working as the following
            //       quick succession of requests result in backend ConcurrentModificationException
            // seGetGroupStandings(selectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
            // seGetFinalStandings(selectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
            seGetStandings(vSelectedChampionship.now().getOrElse(NO_CHAMPIONSHIP).id)
          },
          value <-- vSelectedChampionship.signal.map(_.getOrElse(NO_CHAMPIONSHIP).numEdition.toString())
        )
      ),
      div(
        cls := "col-auto",
        div(
          child.text <-- vSelectedChampionship.signal.map(c => c.getOrElse(NO_CHAMPIONSHIP).numEdition)
        )
      )
    )

  private def championshipCreationDateCol(): Element =
    div(
      cls := "col",
      label(
        cls := "form-label text-muted",
        forId := "championshipDtCreation",
        b(text <-- I18n(ChampionshipCreationToken))
      ),
      input(
        idAttr := "championshipDtCreation",
        cls := "form-control",
        typ := "text",
        value <-- vSelectedChampionship.signal.map(c => c.getOrElse(NO_CHAMPIONSHIP).dtCreation),
        readOnly := true
      ),
    )

  private def championshipStatusCol(): Element =
    div(
      cls := "col",
      label(
        cls := "form-label text-muted",
        forId := "championshipdStatus",
        b(text <-- I18n(ChampionshipStageToken))
      ),
      input(
        idAttr := "championshipStatus",
        cls := "form-control",
        typ := "text",
        value <-- {
          for {
            c <- vSelectedChampionship.signal
            text <- I18n(c.getOrElse(NO_CHAMPIONSHIP).status, ChampionshipStatusTranslationMap).signal
          } yield text.toString()
        },
        readOnly := true
      ),
    )

  def apply(): Element =
    div(
      championshipTypesRow(),
      championshipEditionsRangeRow(),
      div(
        cls := s"row ${spacingStyle("pb")} ${spacingStyle("gx")}",
        championshipCreationDateCol(),
        championshipStatusCol()
      )
    )

end ChampionshipsContent