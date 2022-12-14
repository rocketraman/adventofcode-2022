package day14

import Day
import Lines

class Day14 : Day() {
  data class Point(val x: Int, val y: Int)

  val source = Point(500, 0)

  fun rockPoints(input: Lines): Set<Point> = input.flatMap { i ->
    i.split(" -> ")
      .map { vertex ->
        vertex.split(",").let { Point(it[0].toInt(), it[1].toInt()) }
      }
      .windowed(2)
      .flatMap { (p1, p2) ->
        // fill in the points and get a complete set of rock locations
        if (p1.x == p2.x) {
          if (p1.y < p2.y) {
            (p2.y downTo p1.y).map { Point(p1.x, it) }
          } else {
            (p1.y downTo p2.y).map { Point(p1.x, it) }
          }
        } else {
          if (p1.x < p2.x) {
            (p2.x downTo p1.x).map { Point(it, p1.y) }
          } else {
            (p1.x downTo p2.x).map { Point(it, p1.y) }
          }
        }
      }
  }.toSet()

  override fun part1(input: Lines): Any {
    val rockLocations = rockPoints(input)
    val maxRockY = rockLocations.maxOf { it.y }

    fun nextSandLocation(
      sandLocations: Set<Point> = emptySet(),
      startFrom: Point = source,
    ): Set<Point>? {
      val candidates = listOf(
        startFrom.copy(y = startFrom.y + 1),
        startFrom.copy(x = startFrom.x - 1, y = startFrom.y + 1),
        startFrom.copy(x = startFrom.x + 1, y = startFrom.y + 1),
      )
      candidates.forEach { candidate ->
        if (candidate !in rockLocations && candidate !in sandLocations) {
          return if (candidate.y > maxRockY) {
            null
          } else {
            nextSandLocation(sandLocations, candidate)
          }
        }
      }
      // done this sand particle, on to the next
      return sandLocations + startFrom
    }

    val sandLocations = generateSequence(nextSandLocation()) {
      nextSandLocation(it)
    }.last()

    printCave(rockLocations, sandLocations)

    return sandLocations.size
  }

  override fun part2(input: Lines): Any {
    val rockLocations = rockPoints(input)
    val maxRockY = rockLocations.maxOf { it.y }
    val floorY = maxRockY + 2

    fun nextSandLocation(
      sandLocations: Set<Point> = emptySet(),
      startFrom: Point = source,
    ): Set<Point>? {
      val candidates = listOf(
        startFrom.copy(y = startFrom.y + 1),
        startFrom.copy(x = startFrom.x - 1, y = startFrom.y + 1),
        startFrom.copy(x = startFrom.x + 1, y = startFrom.y + 1),
      )
      candidates.forEach { candidate ->
        if (candidate !in rockLocations && candidate !in sandLocations && candidate.y < floorY) {
          return nextSandLocation(sandLocations, candidate)
        }
      }
      // done this sand particle, on to the next (as long as the sand does not cover the source)
      return if (startFrom != source) sandLocations + startFrom else null
    }

    val sandLocations = generateSequence(nextSandLocation()) {
      nextSandLocation(it)
    }.last()

    printCave(rockLocations, sandLocations)

    return sandLocations.size
  }

  fun printCave(rockPoints: Set<Point>, sandPoints: Set<Point>) {
    val allPoints = rockPoints + sandPoints
    val minX = allPoints.minOf { it.x }
    val maxX = allPoints.maxOf { it.x }
    val maxY = allPoints.maxOf { it.y }

    for (y in 0..maxY) {
      for (x in minX..maxX) {
        when (Point(x, y)) {
          in rockPoints -> print("█")
          in sandPoints -> print("o")
          else -> print(".")
        }
      }
      print("\n")
    }
  }
}
