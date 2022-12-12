package day12

import Day
import Lines

class Day12 : Day() {
  data class Position(val x: Int, val y: Int)

  override fun part1(input: Lines): Any {
    val graph = input.map { it.toCharArray().asList() }

    fun positionOf(c: Char): Position {
      val y = graph.indexOfFirst { it.contains(c) }
      val x = graph[y].indexOfFirst { it == c }
      return Position(x, y)
    }

    val startPosition = positionOf('S')
    val endPosition = positionOf('E')

    fun Position.value() = when (val value = graph[y][x]) {
      'S' -> 'a'
      'E' -> 'z'
      else -> value
    }

    fun Position.possibleMoves(previousPosition: Position?): List<Position> = listOf(
      Position(x + 1, y),
      Position(x - 1, y),
      Position(x, y + 1),
      Position(x, y - 1),
    ).filter {
      (previousPosition == null || it != previousPosition) &&
        it != startPosition &&
        it.x >= 0 &&
        it.y >= 0 &&
        it.y < graph.size &&
        it.x < graph[0].size &&
        it.value() - value() <= 1
    }

    val unvisitedNodes = buildSet {
      for (y in graph.indices) {
        for (x in graph[0].indices) {
          add(Position(x, y))
        }
      }
    }.toMutableSet()

    var current = startPosition

    val distances = buildMap {
      for (y in graph.indices) {
        for (x in graph[0].indices) {
          val position = Position(x, y)
          if (position == current) put(position, 0)
          else put(position, Int.MAX_VALUE)
        }
      }
    }.toMutableMap()

    tailrec fun djikstraStep() {
      current
        .possibleMoves(null)
        .filter { it in unvisitedNodes }
        .forEach { position ->
          if (distances.getValue(current) + 1 < distances.getValue(position)) {
            distances += (position to distances.getValue(current) + 1)
          }
        }

      // mark current node as visited
      unvisitedNodes -= current

      if (endPosition !in unvisitedNodes) {
        return
      } else {
        val minUnvisited = unvisitedNodes.minBy { distances.getValue(it) }
        current = minUnvisited
        djikstraStep()
      }
    }

    djikstraStep()

    return distances.getValue(endPosition)
  }

  override fun part2(input: Lines): Any {
    val graph = input.map { it.toCharArray().asList() }

    fun positionOf(c: Char): Position {
      val y = graph.indexOfFirst { it.contains(c) }
      val x = graph[y].indexOfFirst { it == c }
      return Position(x, y)
    }

    // any node with a
    val startPosition = positionOf('a')
    val endPosition = positionOf('E')

    fun Position.value() = when (val value = graph[y][x]) {
      'S' -> 'a'
      'E' -> 'z'
      else -> value
    }

    fun Position.possibleMoves(previousPosition: Position?): List<Position> = listOf(
      Position(x + 1, y),
      Position(x - 1, y),
      Position(x, y + 1),
      Position(x, y - 1),
    ).filter {
      (previousPosition == null || it != previousPosition) &&
        it.x >= 0 &&
        it.y >= 0 &&
        it.y < graph.size &&
        it.x < graph[0].size &&
        it.value() != 'a' &&
        it.value() - value() <= 1
    }

    val unvisitedNodes = buildSet {
      for (y in graph.indices) {
        for (x in graph[0].indices) {
          if (Position(x, y) != startPosition) {
            add(Position(x, y))
          }
        }
      }
    }.toMutableSet()

    var current = startPosition

    val distances = buildMap {
      for (y in graph.indices) {
        for (x in graph[0].indices) {
          val position = Position(x, y)
          // this time we initialize all nodes with a with start distance 0, as we can start from any of them
          if (position.value() == 'a') put(position, 0)
          else put(position, Int.MAX_VALUE)
        }
      }
    }.toMutableMap()

    tailrec fun djikstraStep() {
      current
        .possibleMoves(null)
        .filter { it in unvisitedNodes }
        .forEach { position ->
          if (distances.getValue(current) + 1 < distances.getValue(position)) {
            distances += (position to distances.getValue(current) + 1)
          }
        }

      // mark current node as visited
      unvisitedNodes -= current

      if (endPosition !in unvisitedNodes) {
        return
      } else {
        val minUnvisited = unvisitedNodes.minBy { distances.getValue(it) }
        current = minUnvisited
        djikstraStep()
      }
    }

    djikstraStep()

    return distances.getValue(endPosition)
  }
}
