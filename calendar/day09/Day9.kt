package day09

import Day
import Lines

class Day9 : Day() {
  data class Position(val x: Int, val y: Int) {
    val adjacentPositions get() = setOf(
      Position(x, y),
      Position(x - 1, y - 1),
      Position(x - 1, y),
      Position(x - 1, y + 1),
      Position(x, y - 1),
      Position(x, y + 1),
      Position(x + 1, y - 1),
      Position(x + 1, y),
      Position(x + 1, y + 1),
    )
    val right get() = copy(x = x + 1, y = y)
    val up get() = copy(x = x, y = y + 1)
    val left get() = copy(x = x - 1, y = y)
    val down get() = copy(x = x, y = y - 1)
  }

  override fun part1(input: Lines): Any = simulate(input, 2)

  override fun part2(input: Lines): Any = simulate(input, 10)

  private fun simulate(input: Lines, knotCount: Int): Int {
    val knots = MutableList(knotCount) { Position(0, 0) }
    return buildSet {
      add(knots.last())
      inputToMoves(input).forEach { direction ->
        knots[0] = knots.first().move(direction)
        for (k in 1 until knotCount) knots[k] = knots[k].follow(knots[k - 1])
        add(knots.last())
      }
    }.size
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

  private fun Position.follow(leader: Position): Position = if (this in leader.adjacentPositions) this else when {
    y == leader.y -> if (x < leader.x) right else left
    x == leader.x -> if (y < leader.y) up else down
    x < leader.x -> if (y < leader.y) up.right else down.right
    else -> if (y < leader.y) up.left else down.left
  }
}
