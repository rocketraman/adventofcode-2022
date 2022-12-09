package day09

import Day
import Lines

class Day9 : Day() {
  data class Position(val x: Int, val y: Int) {
    val adjacentPositions
      get() = setOf(
        Position(x - 1, y - 1),
        Position(x - 1, y),
        Position(x - 1, y + 1),
        Position(x, y - 1),
        Position(x, y + 1),
        Position(x + 1, y - 1),
        Position(x + 1, y),
        Position(x + 1, y + 1),
      )
    val right
      get() = copy(x = x + 1, y = y)
    val up
      get() = copy(x = x, y = y + 1)
    val left
      get() = copy(x = x - 1, y = y)
    val down
      get() = copy(x = x, y = y - 1)
  }

  override fun part1(input: Lines): Any {
    var head = Position(0, 0)
    var tail = Position(0, 0)
    val tailVisited = mutableSetOf(tail)

    inputToMoves(input)
      .forEach { direction ->
        head = head.move(direction)
        tail = tail.follow(head)
        tailVisited.add(tail)
      }

    return tailVisited.size
  }

  override fun part2(input: Lines): Any {
    val knots = MutableList(10) { Position(0, 0) }
    val tailVisited = mutableSetOf(knots.last())

    inputToMoves(input)
      .forEach { direction ->
        knots[0] = knots.first().move(direction)
        for (k in 1..9) {
          knots[k] = knots[k].follow(knots[k - 1])
        }
        tailVisited.add(knots.last())
      }

    return tailVisited.size
  }

  private fun inputToMoves(input: Lines) = input
    .map { it.split(" ") }
    // flatten multi-moves into individual moves
    .flatMap { (direction, count) ->
      List(count.toInt()) { direction }
    }

  private fun Position.move(direction: String) = when (direction) {
    "R" -> right
    "U" -> up
    "L" -> left
    "D" -> down
    else -> error("Invalid direction $direction")
  }

  private fun Position.follow(leader: Position): Position {
    return if (this !in leader.adjacentPositions) {
      // move this to follow head and add the new position to the visited list
      when {
        this == leader -> this
        this.y == leader.y -> if (this.x < leader.x) this.right else this.left
        this.x == leader.x -> if (this.y < leader.y) this.up else this.down
        this.x < leader.x -> if (this.y < leader.y) this.up.right else this.down.right
        else -> if (this.y < leader.y) this.up.left else this.down.left
      }
    } else this
  }
}
