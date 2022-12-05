package day04

import Day
import Lines

class Day4 : Day() {
  override fun part1(input: Lines): Any {
    return input
      .count { line ->
        val (range1, range2) = line.split(",").map { p ->
          p.split("-").let { it[0].toInt()..it[1].toInt() }
        }
        range1.containsRange(range2) || range2.containsRange(range1)
      }
  }

  override fun part2(input: Lines): Any {
    return input
      .count { line ->
        val (range1, range2) = line.split(",").map { p ->
          p.split("-").let { it[0].toInt()..it[1].toInt() }
        }
        range1.overlapsRange(range2)
      }
  }

  fun IntRange.containsRange(other: IntRange): Boolean =
    this.first <= other.first && this.last >= other.last

  fun IntRange.overlapsRange(other: IntRange): Boolean =
    this.first in other || this.last in other || other.first in this || other.last in this
}
