package day01

import Day
import Lines

class Day1 : Day() {
  override fun part1(input: Lines): Any {
    val groupedByElf = groupedByElf(input)
    return groupedByElf.maxOfOrNull { it.sum() }!!
  }

  override fun part2(input: Lines): Any {
    val groupedByElf = groupedByElf(input)
    return groupedByElf.sortedByDescending { it.sum() }.take(3).sumOf { it.sum() }
  }

  private fun groupedByElf(input: Lines): List<List<Int>> {
    val inputInts = input.map { it.toIntOrNull() }
    val groupedByElf = inputInts.fold(emptyList<List<Int>>()) { acc, current ->
      val a = if (current != null) {
        acc.dropLast(1).plusElement(acc.lastOrNull().orEmpty() + current)
      } else {
        acc.plusElement(emptyList())
      }
      a
    }
    return groupedByElf
  }
}
