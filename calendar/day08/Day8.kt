package day08

import Day
import Lines

class Day8 : Day() {
  data class Position(val row: Int, val col: Int)

  class Grid(val matrix: List<List<Int>>) {
    fun Position.valueAt() = matrix[row][col]

    fun Position.leftOf() = matrix[row].subList(0, col)
    fun Position.rightOf() = matrix[row].slice((col + 1) until matrix[row].size)
    fun Position.topOf() = matrix.map { it[col] }.subList(0, row)
    fun Position.bottomOf() = matrix.map { it[col] }.slice((row + 1) until matrix[col].size)
  }

  fun matrix(input: Lines) = input.map { line -> line.toCharArray().map { it.digitToInt() } }

  override fun part1(input: Lines): Any {
    return with(Grid(matrix(input))) {
      fun Position.visible(others: List<Int>): Boolean =
        others.isEmpty() || others.all { it < valueAt() }

      fun Position.visibleLeft(): Boolean = visible(leftOf())
      fun Position.visibleRight(): Boolean = visible(rightOf())
      fun Position.visibleTop(): Boolean = visible(topOf())
      fun Position.visibleBottom(): Boolean = visible(bottomOf())

      fun Position.visibleFromOutside(): Boolean =
        visibleLeft() || visibleRight() || visibleTop() || visibleBottom()

      matrix.withIndex().sumOf { (row, rowValues) ->
        rowValues.withIndex().sumOf { (col, _) ->
          val pos = Position(row, col)
          if (pos.visibleFromOutside()) 1L else 0L
        }
      }
    }
  }

  override fun part2(input: Lines): Any {
    return with(Grid(matrix(input))) {
      matrix.withIndex().maxOf { (row, rowValues) ->
        rowValues.indices.maxOf { col ->
          val pos = Position(row, col)

          fun List<Int>.takeWhileVisible(): List<Int> {
            val value = pos.valueAt()
            return fold(emptyList()) { acc, elem ->
              if (acc.isNotEmpty() && acc.last() >= value) acc
              else acc + elem
            }
          }

          val left = pos.leftOf().reversed().takeWhileVisible().count()
          val right = pos.rightOf().takeWhileVisible().count()
          val top = pos.topOf().reversed().takeWhileVisible().count()
          val bottom = pos.bottomOf().takeWhileVisible().count()

          left * right * top * bottom
        }
      }
    }
  }
}
