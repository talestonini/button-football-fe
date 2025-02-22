package com.talestonini.buttonfootball

package object datastructure {
  
  trait Tree[+A] {
    def map[B](f: A => B): Tree[B] = this match {
      case Empty => Empty
      case Node(value, left, right) => Node(f(value), left.map(f), right.map(f))
    }
  }
  case object Empty extends Tree[Nothing]
  case class Node[A](value: A, left: Tree[A], right: Tree[A]) extends Tree[A]

  def treeToList[A](tree: Tree[A]): List[A] = tree match {
    case Empty => List.empty
    case Node(value, left, right) => value :: treeToList(left) ::: treeToList(right)
  }

}