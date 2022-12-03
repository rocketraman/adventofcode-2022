package day02

import Day
import Lines
import day02.Move.*

class Day2 : Day() {
  val inputMap = mapOf(
    "A" to ROCK,
    "X" to ROCK,
    "B" to PAPER,
    "Y" to PAPER,
    "C" to SCISSORS,
    "Z" to SCISSORS,
  )

  val part2InputMap = mapOf(
    "X" to 0,
    "Y" to 3,
    "Z" to 6,
  )

  val scoreTable = mapOf(
    ROCK to mapOf(
      ROCK to 3,
      PAPER to 0,
      SCISSORS to 6,
    ),
    PAPER to mapOf(
      ROCK to 6,
      PAPER to 3,
      SCISSORS to 0,
    ),
    SCISSORS to mapOf(
      ROCK to 0,
      PAPER to 6,
      SCISSORS to 3,
    ),
  )

  val invertedScoreTable = mapOf(
    ROCK to mapOf(
      3 to ROCK,
      0 to SCISSORS,
      6 to PAPER,
    ),
    PAPER to mapOf(
      0 to ROCK,
      3 to PAPER,
      6 to SCISSORS,
    ),
    SCISSORS to mapOf(
      6 to ROCK,
      0 to PAPER,
      3 to SCISSORS,
    ),
  )

  fun shapeScore(self: Move): Int = self.value

  fun gameScore(opponent: Move, self: Move): Int = scoreTable[self]!![opponent]!!

  fun invertedGameMove(opponent: Move, self: Int): Move = invertedScoreTable[opponent]!![self]!!

  override fun part1(input: Lines): Any {
    val games = input.map { it.split(" ").map { inputMap[it]!! } }
    val scores = games.map { gameScore(it[0], it[1]) + shapeScore(it[1]) }
    return scores.sum()
  }

  override fun part2(input: Lines): Any {
    val games = input.map {
      val (opponent, desiredScore) = it.split(" ")
      inputMap[opponent]!! to part2InputMap[desiredScore]!!
    }
    val scores = games.map {
      val selfMove = invertedGameMove(it.first, it.second)
      shapeScore(selfMove) + it.second
    }
    return scores.sum()
  }
}

enum class Move(val opponent: Char, val self: Char, val value: Int) {
  ROCK('A', 'X', 1),
  PAPER('B', 'Y', 2),
  SCISSORS('C', 'Z', 3),
}
