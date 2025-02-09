package com.talestonini.buttonfootball

package object exception {

  class InvalidNumberOfTeams(numTeams: Int) extends RuntimeException(s"invalid number of teams: $numTeams")

}