package day03

import Day
import Lines

class Day3 : Day() {
  // could offset the unicode code to get the value but that's a bit esoteric, create a map to value instead
  val itemPriorities = ('A'..'Z').mapIndexed { index, c -> c to index + 27 }.toMap() +
    ('a'..'z').mapIndexed { index, c -> c to index + 1 }.toMap()

  override fun part1(input: Lines): Any {
    return input.sumOf {
      val items = it.toCharArray().toList()
      val (sack1, sack2) = items.chunked(items.size / 2)
      val both = sack1.toSet() intersect sack2.toSet()
      val errorItem = both.single()
      itemPriorities[errorItem] ?: error("Unknown item $errorItem")
    }
  }

  override fun part2(input: Lines): Any {
    val groups = input.chunked(3)
    return groups.sumOf { (s1, s2, s3) ->
      val badgeItemSet = s1.toSet() intersect s2.toSet() intersect s3.toSet()
      val badgeItem = badgeItemSet.single()
      itemPriorities[badgeItem] ?: 0
    }
  }
}
