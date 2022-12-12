package day11

import Day
import Lines

class Day11 : Day() {
  data class Monkey(
    val items: MutableList<Long>,
    val operation: (Long) -> Long,
    val test: MonkeyTest,
    var inspections: Long = 0,
  )

  data class MonkeyTest(
    val testDivisibleBy: Long,
    val nextTrue: Int,
    val nextFalse: Int,
  )

  val startingItemRegex = """  Starting items: (.*)""".toRegex()
  val operationRegex = """  Operation: new = old (.) (.*)""".toRegex()
  val testRegex = """  Test: divisible by (.*)""".toRegex()
  val throwRegex = """    If (?:true|false): throw to monkey (.*)""".toRegex()

  override fun part1(input: Lines): Any {
    return solve(input, 20) { _ -> { it / 3 } }
  }

  override fun part2(input: Lines): Any {
    return solve(input, 10_000) { monkeys ->
      // had to check the subreddit for this -- I noticed all the divisors were primes but too rusty on math
      //  for the implication to sink in :-(
      val primesLcm = monkeys
        .map { it.test.testDivisibleBy }
        .reduce { acc, l -> acc * l }
      return@solve { it.mod(primesLcm) }
    }
  }

  private fun solve(input: Lines, rounds: Int, worryReducerOf: (List<Monkey>) -> (Long) -> Long): Long {
    val items = input
      .filter { startingItemRegex.matches(it) }
      .mapNotNull {
        startingItemRegex
          .matchEntire(it)!!.groups[1]?.value
          ?.split(", ")
          ?.map { it.toLong() }
      }

    val operations = input
      .filter { operationRegex.matches(it) }
      .map {
        val (operand, multiplier) = operationRegex
          .matchEntire(it)!!.destructured

        val multiplierFn = when (multiplier) {
          "old" -> { old: Long -> old }
          else -> { _ -> multiplier.toLong() }
        }

        when (operand) {
          "*" -> { old: Long -> old * multiplierFn(old) }
          "+" -> { old: Long -> old + multiplierFn(old) }
          else -> error("Invalid operand $operand")
        }
      }

    val tests = input
      .filter { testRegex.matches(it) || it.startsWith("    If ") }
      .chunked(3)
      .map {
        val divisibleBy = testRegex
          .matchEntire(it[0])!!.groups[1]?.value
          ?.toLong()

        val (monkeyTrue) = throwRegex.matchEntire(it[1])!!.destructured
        val (monkeyFalse) = throwRegex.matchEntire(it[2])!!.destructured

        MonkeyTest(divisibleBy!!, monkeyTrue.toInt(), monkeyFalse.toInt())
      }

    val monkeys = items.indices.map {
      Monkey(
        items = items[it].toMutableList(),
        operation = operations[it],
        test = tests[it],
      )
    }

    val worryReducer = worryReducerOf(monkeys)

    (1..rounds).forEach { _ ->
      monkeys.forEach { monkey ->
        val listIterator = monkey.items.listIterator()
        for (item in listIterator) {
          monkey.inspections++
          listIterator.remove()
          val worry = worryReducer(monkey.operation(item))
          val testResult = worry.mod(monkey.test.testDivisibleBy) == 0L
          monkeys[if (testResult) monkey.test.nextTrue else monkey.test.nextFalse].items.add(worry)
        }
      }
    }

    val twoActive = monkeys.map { it.inspections }.sortedByDescending { it }.take(2)
    return twoActive[0] * twoActive[1]
  }
}
