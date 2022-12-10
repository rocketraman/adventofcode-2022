package day10

import Day
import Lines

class Day10 : Day() {
  override fun part1(input: Lines): Any {
    val registerX = buildRegisterOverAllCycles(input)

    return listOf(20, 60, 100, 140, 180, 220).sumOf {
      registerX[it - 1] * it
    }
  }

  override fun part2(input: Lines): Any {
    val registerX = buildRegisterOverAllCycles(input)

    val crtPixels = List(40 * 6) { i ->
      val pixelIndex = i.mod(40)
      if (pixelIndex in registerX[i].let { (it - 1)..(it + 1) }) "█" else " "
    }

    val crt = crtPixels
      .chunked(40)
      .joinToString("\n") {
        it.joinToString("")
      }

    // avoid the first line offset in the harness output
    return "\n" + crt
  }

  private fun buildRegisterOverAllCycles(input: Lines): MutableList<Int> {
    val registerX = mutableListOf(1)

    input.forEach { instruction ->
      if (instruction == "noop") {
        registerX.add(registerX.last())
      } else if (instruction.startsWith("addx")) {
        val delta = instruction.substring(5).toInt()
        registerX.add(registerX.last())
        registerX.add(registerX.last() + delta)
      }
    }
    return registerX
  }
}
