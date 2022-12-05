package day05

import Day
import Lines

class Day5 : Day() {
  override fun part1(input: Lines): Any {
    return executeCraneOperations(input, true)
  }

  override fun part2(input: Lines): Any {
    return executeCraneOperations(input, false)
  }

  private fun executeCraneOperations(input: Lines, singleCrateMoves: Boolean): String {
    val instructionPattern = """move (\d+) from (\d+) to (\d+)""".toRegex()

    val stackLinesAndCounts = input
      .takeWhile { !it.startsWith("move") }
      .filterNot { it.isEmpty() }

    val stackLines = stackLinesAndCounts.dropLast(1)
    val stackCount = stackLinesAndCounts.last().last().digitToInt()

    val stacks = (0 until stackCount).map { stack ->
      val col = stack * 4 + 1
      stackLines
        .mapNotNull { if (it.length > col) it.slice(col..col) else null }
        .filterNot { it.isBlank() }
        .reversed()
        .toMutableList()
    }

    val instructions = input.drop(stackLines.size + 2)
      .map {
        val (moveCount, from, to) = instructionPattern.matchEntire(it)!!.groupValues.drop(1)
        MoveInstruction(moveCount.toInt(), from.toInt() - 1, to.toInt() - 1)
      }

    instructions.forEach { (moveCount, from, to) ->
      stacks[to].addAll(
        stacks[from].takeLast(moveCount).run {
          if (singleCrateMoves) reversed() else this
        },
      )
      repeat(moveCount) {
        stacks[from].removeLast()
      }
    }

    return stacks.joinToString("") { it.last() }
  }
}

data class MoveInstruction(
  val moveCount: Int,
  val from: Int,
  val to: Int,
)
