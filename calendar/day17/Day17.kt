package day17

import Day
import Lines

class Day17 : Day() {
  // for part 2, vals are too slow, mutate with vars
  data class Position(var x: Int, var y: Int)
  enum class Direction {
    DOWN,
    LEFT,
    RIGHT
  }

  override fun part1(input: Lines): Any {
    val jetPattern = sequence {
      while(true) yieldAll(
        input[0].toCharArray().map {
          when (it) {
            '<' -> Direction.LEFT
            '>' -> Direction.RIGHT
            else -> error("Invalid jet $it")
          }
        },
      )
    }.iterator()

    fun nextJet(): Direction = jetPattern.next()

    var currentHighest = 0
    val rockPositions = mutableSetOf<Position>()

    fun dashRock() = setOf(
      Position(2, currentHighest + 4),
      Position(3, currentHighest + 4),
      Position(4, currentHighest + 4),
      Position(5, currentHighest + 4),
    )

    fun plusRock() = setOf(
      Position(3, currentHighest + 4),
      Position(2, currentHighest + 5),
      Position(3, currentHighest + 5),
      Position(4, currentHighest + 5),
      Position(3, currentHighest + 6),
    )

    fun backwardLRock() = setOf(
      Position(2, currentHighest + 4),
      Position(3, currentHighest + 4),
      Position(4, currentHighest + 4),
      Position(4, currentHighest + 5),
      Position(4, currentHighest + 6),
    )

    fun lineRock() = setOf(
      Position(2, currentHighest + 7),
      Position(2, currentHighest + 6),
      Position(2, currentHighest + 5),
      Position(2, currentHighest + 4),
    )

    fun squareRock() = setOf(
      Position(2, currentHighest + 4),
      Position(3, currentHighest + 4),
      Position(2, currentHighest + 5),
      Position(3, currentHighest + 5),
    )

    fun Set<Position>.move(direction: Direction): Set<Position> = when (direction) {
      Direction.LEFT -> map { it.copy(x = it.x - 1) }.toSet()
      Direction.RIGHT -> map { it.copy(x = it.x + 1) }.toSet()
      Direction.DOWN -> map { it.copy(y = it.y - 1) }.toSet()
    }

    fun Set<Position>.overlapsWall() =
      any { it.x < 0 || it.x > 6 }

    fun Set<Position>.overlapsRock() =
      any { it in rockPositions }

    fun Set<Position>.overlapsFloor() =
      any { it.y <= 0 }

    repeat(2022) { iteration ->
      var rock = when (iteration % 5) {
        0 -> dashRock()
        1 -> plusRock()
        2 -> backwardLRock()
        3 -> lineRock()
        4 -> squareRock()
        else -> error("Invalid modulus at $iteration")
      }

      while (true) {
        val jet = nextJet()
        val jetRock = rock.move(jet)
        if (!jetRock.overlapsWall() && !jetRock.overlapsRock()) {
          rock = jetRock
        }

        val fallRock = rock.move(Direction.DOWN)
        if (!fallRock.overlapsRock() && !fallRock.overlapsFloor()) {
          rock = fallRock
        } else {
          rockPositions.addAll(rock)
          val rockMax = rock.maxOf { it.y }
          if (rockMax > currentHighest) {
            currentHighest = rockMax
          }
          break
        }
      }
    }

    //visualize(emptySet(), rockPositions, currentHighest + 3)

    return currentHighest
  }

  override fun part2(input: Lines): Any {
    TODO(
      "Nah, too long. Good discussion of pattern-matching solution here https://youtu.be/QXTBseFzkW4?t=1814" +
        "and good implementation in Kotlin here https://github.com/n3o59hf/AoC2022/blob/master/src/main/kotlin/lv/n3o/aoc2022/tasks/T17.kt#L102-L111",
    )
  }
}
