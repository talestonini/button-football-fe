package com.talestonini.datastructure

class TreeTest extends munit.FunSuite:

  // ===================================================================================================================
  // Empty Tree Tests
  // ===================================================================================================================

  test("Empty tree - map returns Empty") {
    val empty: Tree[Int] = Empty
    val result = empty.map(_ * 2)
    assertEquals(result, Empty)
  }

  test("Empty tree - toList returns empty list") {
    val empty: Tree[Int] = Empty
    assertEquals(empty.toList(), List.empty[Int])
  }

  test("Empty tree - findFirst returns None") {
    val empty: Tree[Int] = Empty
    assertEquals(empty.findFirst(_ > 0), None)
  }

  // ===================================================================================================================
  // Single Node Tests
  // ===================================================================================================================

  test("Single node - map transforms the value") {
    val tree = Node(5, Empty, Empty)
    val result = tree.map(_ * 2)
    assertEquals(result, Node(10, Empty, Empty))
  }

  test("Single node - toList returns single element list") {
    val tree = Node(42, Empty, Empty)
    assertEquals(tree.toList(), List(42))
  }

  test("Single node - findFirst with matching predicate returns Some") {
    val tree = Node(7, Empty, Empty)
    assertEquals(tree.findFirst(_ == 7), Some(7))
  }

  test("Single node - findFirst with non-matching predicate returns None") {
    val tree = Node(7, Empty, Empty)
    assertEquals(tree.findFirst(_ == 10), None)
  }

  // ===================================================================================================================
  // Multi-Node Tree Tests
  // ===================================================================================================================

  test("Multi-node tree - map transforms all nodes") {
    val tree = Node(1, Node(2, Empty, Empty), Node(3, Empty, Empty))
    val result = tree.map(_ * 10)
    assertEquals(result, Node(10, Node(20, Empty, Empty), Node(30, Empty, Empty)))
  }

  test("Multi-node tree - toList returns pre-order traversal") {
    // Tree structure:
    //       1
    //      / \
    //     2   3
    val tree = Node(1, Node(2, Empty, Empty), Node(3, Empty, Empty))
    assertEquals(tree.toList(), List(1, 2, 3))
  }

  test("Multi-node tree - toList with deeper tree") {
    // Tree structure:
    //       1
    //      / \
    //     2   3
    //    /     \
    //   4       5
    val tree = Node(
      1,
      Node(2, Node(4, Empty, Empty), Empty),
      Node(3, Empty, Node(5, Empty, Empty))
    )
    assertEquals(tree.toList(), List(1, 2, 4, 3, 5))
  }

  test("Multi-node tree - findFirst returns first match in pre-order") {
    // Tree structure:
    //       5
    //      / \
    //     3   7
    //    /     \
    //   2       8
    val tree = Node(
      5,
      Node(3, Node(2, Empty, Empty), Empty),
      Node(7, Empty, Node(8, Empty, Empty))
    )
    // First element > 6 should be 7 (root is 5, left subtree max is 3, then right is 7)
    assertEquals(tree.findFirst(_ > 6), Some(7))
  }

  test("Multi-node tree - findFirst returns first match from left subtree") {
    val tree = Node(
      5,
      Node(3, Node(2, Empty, Empty), Empty),
      Node(7, Empty, Node(8, Empty, Empty))
    )
    // First element < 4 should be 3 (checked in pre-order: 5, then 3)
    assertEquals(tree.findFirst(_ < 4), Some(3))
  }

  test("Multi-node tree - findFirst with no match returns None") {
    val tree = Node(
      5,
      Node(3, Node(2, Empty, Empty), Empty),
      Node(7, Empty, Node(8, Empty, Empty))
    )
    assertEquals(tree.findFirst(_ > 10), None)
  }

  // ===================================================================================================================
  // String Tree Tests
  // ===================================================================================================================

  test("String tree - map transforms strings") {
    val tree = Node("hello", Node("world", Empty, Empty), Empty)
    val result = tree.map(_.toUpperCase)
    assertEquals(result, Node("HELLO", Node("WORLD", Empty, Empty), Empty))
  }

  test("String tree - findFirst with string predicate") {
    val tree = Node("apple", Node("banana", Empty, Empty), Node("cherry", Empty, Empty))
    assertEquals(tree.findFirst(_.startsWith("b")), Some("banana"))
  }

  // ===================================================================================================================
  // Complex Tree Shapes
  // ===================================================================================================================

  test("Left-heavy tree - operations work correctly") {
    // Tree structure:
    //       1
    //      /
    //     2
    //    /
    //   3
    val tree = Node(1, Node(2, Node(3, Empty, Empty), Empty), Empty)
    assertEquals(tree.toList(), List(1, 2, 3))
    assertEquals(tree.map(_ + 10), Node(11, Node(12, Node(13, Empty, Empty), Empty), Empty))
  }

  test("Right-heavy tree - operations work correctly") {
    // Tree structure:
    //   1
    //    \
    //     2
    //      \
    //       3
    val tree = Node(1, Empty, Node(2, Empty, Node(3, Empty, Empty)))
    assertEquals(tree.toList(), List(1, 2, 3))
    assertEquals(tree.map(_ + 10), Node(11, Empty, Node(12, Empty, Node(13, Empty, Empty))))
  }

  test("Complete binary tree - operations work correctly") {
    // Tree structure:
    //       1
    //      / \
    //     2   3
    //    / \ / \
    //   4  5 6  7
    val tree = Node(
      1,
      Node(2, Node(4, Empty, Empty), Node(5, Empty, Empty)),
      Node(3, Node(6, Empty, Empty), Node(7, Empty, Empty))
    )
    assertEquals(tree.toList(), List(1, 2, 4, 5, 3, 6, 7))
    
    val doubled = tree.map(_ * 2)
    assertEquals(doubled.toList(), List(2, 4, 8, 10, 6, 12, 14))
  }

  // ===================================================================================================================
  // Edge Cases
  // ===================================================================================================================

  test("Tree with mixed Empty children") {
    val tree = Node(
      1,
      Node(2, Empty, Node(3, Empty, Empty)),
      Node(4, Node(5, Empty, Empty), Empty)
    )
    assertEquals(tree.toList(), List(1, 2, 3, 4, 5))
  }

  test("Deep tree - map preserves structure") {
    val deep = Node(1, Empty, Node(2, Empty, Node(3, Empty, Node(4, Empty, Node(5, Empty, Empty)))))
    val result = deep.map(_ * 2)
    assertEquals(result.toList(), List(2, 4, 6, 8, 10))
  }

  test("findFirst stops at first match and doesn't traverse entire tree") {
    // If we find a match in the root, we shouldn't need to check children
    val tree = Node(5, Node(3, Empty, Empty), Node(7, Empty, Empty))
    assertEquals(tree.findFirst(_ == 5), Some(5))
    
    // If we find in left subtree, we shouldn't check right
    assertEquals(tree.findFirst(_ == 3), Some(3))
  }

  // ===================================================================================================================
  // Custom Type Tests
  // ===================================================================================================================

  case class Person(name: String, age: Int)

  test("Custom type - map transforms custom objects") {
    val tree = Node(
      Person("Alice", 30),
      Node(Person("Bob", 25), Empty, Empty),
      Node(Person("Charlie", 35), Empty, Empty)
    )
    
    val olderTree = tree.map(p => p.copy(age = p.age + 1))
    val people = olderTree.toList()
    
    assertEquals(people.head.age, 31)
    assertEquals(people(1).age, 26)
    assertEquals(people(2).age, 36)
  }

  test("Custom type - findFirst with complex predicate") {
    val tree = Node(
      Person("Alice", 30),
      Node(Person("Bob", 25), Empty, Empty),
      Node(Person("Charlie", 35), Empty, Empty)
    )
    
    assertEquals(tree.findFirst(_.age > 30), Some(Person("Charlie", 35)))
    assertEquals(tree.findFirst(_.name.startsWith("B")), Some(Person("Bob", 25)))
    assertEquals(tree.findFirst(_.age > 40), None)
  }

end TreeTest
