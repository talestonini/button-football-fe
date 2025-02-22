package com.talestonini.buttonfootball

package object datastructure {
  
  trait Tree[+A] {
    def map[B](f: A => B): Tree[B] = this match {
      case Empty => Empty
      case Node(value, left, right) => Node(f(value), left.map(f), right.map(f))
    }

    def toList(): List[A] = this match {
      case Empty => List.empty
      case Node(value, left, right) => value :: left.toList() ::: right.toList()
    }

    def findFirst(p: A => Boolean): Option[A] = this match {
      case Empty => None
      case Node(valInNode, left, right) =>
        if (p(valInNode))
          Some(valInNode)
        else
          left.findFirst(p) match {
            case Some(valInLeft) => Some(valInLeft)
            case None => right.findFirst(p)
          }
    }
  }
  case object Empty extends Tree[Nothing]
  case class Node[A](value: A, left: Tree[A], right: Tree[A]) extends Tree[A]

}