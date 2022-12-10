package day10

import Day
import Lines

class Day10 : Day() {
  override fun part1(input: Lines): Any {
    val registerX = buildRegisterOverAllCycles(input)

    return (20..220 step 40).sumOf {
      registerX[it - 1] * it
    }
  }

  override fun part2(input: Lines): Any {
    val registerX = buildRegisterOverAllCycles(input)

    val crtPixels = List(40 * 6) { i ->
      val pixelIndex = i.mod(40)
      // change crt output to make it easier to read, nice idea from Kotlin Slack
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

  private fun buildRegisterOverAllCycles(input: Lines): List<Int> = buildList {
    add(1)
    input.forEach { instruction ->
      when {
        instruction == "noop" -> add(last())
        instruction.startsWith("addx") -> {
          val delta = instruction.substring(5).toInt()
          add(last())
          add(last() + delta)
        }
      }
    }
  }
}
