package day10

import Day
import Lines

class Day10 : Day() {
  override fun part1(input: Lines): Any {
    val registerX = mutableListOf(1)

    input.forEach { instruction ->
      if (instruction == "noop") {
        registerX.add(registerX.last())
      }
      else if (instruction.startsWith("addx")) {
        val delta = instruction.substring(5).toInt()
        registerX.add(registerX.last())
        registerX.add(registerX.last() + delta)
      }
    }

    return listOf(20, 60, 100, 140, 180, 220).sumOf {
      registerX[it - 1] * it
    }
  }

  override fun part2(input: Lines): Any {
    val registerX = mutableListOf(1)

    input.forEach { instruction ->
      if (instruction == "noop") {
        registerX.add(registerX.last())
      }
      else if (instruction.startsWith("addx")) {
        val delta = instruction.substring(5).toInt()
        registerX.add(registerX.last())
        registerX.add(registerX.last() + delta)
      }
    }

    val crtPixels = List(40 * 6) { i ->
      val pixelIndex = i.mod(40)
      if (pixelIndex in registerX[i].let { (it - 1)..(it + 1) }) "█" else " "
    }

    val crt = crtPixels
      .windowed(40, 40)
      .joinToString("\n") { it.joinToString("") }

    return "\n" + crt
  }
}
