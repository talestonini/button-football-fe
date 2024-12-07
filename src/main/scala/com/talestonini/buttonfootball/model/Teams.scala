package com.talestonini.buttonfootball.model

import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import cats.effect.IO

object Teams {
  case class Team(id: Int, name: String, `type`: String, fullName: String, foundation: String, city: String,
                  country: String, logoImgFile: String)
  implicit val teamDecoder: EntityDecoder[IO, Team] = jsonOf[IO, Team]
  implicit val teamEncoder: EntityEncoder[IO, Team] = jsonEncoderOf[IO, Team]
}