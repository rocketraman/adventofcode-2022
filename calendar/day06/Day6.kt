package day06

import Day
import Lines

class Day6 : Day() {
  override fun part1(input: Lines): Any {
    return findMarker(input, 4)
  }

  override fun part2(input: Lines): Any {
    return findMarker(input, 14)
  }

  private fun findMarker(input: Lines, size: Int): Int {
    return input.single()
      .windowed(size, partialWindows = true)
      .indexOfFirst { it.toSet().size == size } + size
  }
}
