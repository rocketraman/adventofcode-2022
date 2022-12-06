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
    val windows = input.single()
      .windowed(size, partialWindows = true)
      .withIndex()

    for ((i, w) in windows) {
      if (w.toSet().size == size) {
        return i + size
      }
    }
    error("No window found")
  }
}
