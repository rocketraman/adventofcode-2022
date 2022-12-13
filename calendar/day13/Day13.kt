package day13

import Day
import Lines

class Day13 : Day() {
  sealed class Packet: Comparable<Packet> {
    override fun compareTo(right: Packet): Int {
      val result = when {
        this is IntegerPacket && right is IntegerPacket ->
          this.data.compareTo(right.data)
        this is ListPacket && right is ListPacket -> {
          extendTo(right.data.size).data
            .zip(right.extendTo(data.size).data)
            .asSequence().map { (l, r) -> l.compareTo(r) }.firstOrNull { it != 0 } ?: 0
        }
        this is ListPacket && right is IntegerPacket ->
          compareTo(right.asList())
        this is IntegerPacket && right is ListPacket ->
          asList().compareTo(right)
        this is IntegerPacket && right is EmptyPacket -> 1
        this is EmptyPacket && right is IntegerPacket -> -1
        this is ListPacket && right is EmptyPacket -> 1
        this is EmptyPacket && right is ListPacket -> -1
        this is EmptyPacket && right is EmptyPacket -> 0
        else -> error("Unhandled compare left is ${this::class.simpleName} right is ${right::class.simpleName}")
      }
      return result
    }
  }
  data class IntegerPacket(val data: Int): Packet() {
    fun asList() = ListPacket(mutableListOf(this))
    override fun toString() = data.toString()
  }
  data class ListPacket(val data: List<Packet>): Packet() {
    fun extendTo(size: Int): ListPacket {
      return if (data.size < size) {
        ListPacket(data + List(size - data.size) { EmptyPacket })
      } else {
        this
      }
    }
    override fun toString() = data
      .filterNot { it is EmptyPacket }
      .joinToString(",", prefix = "[", postfix = "]")
  }
  object EmptyPacket: Packet()

  class PacketsBuilder {
    private var currentList: MutableList<MutableList<Packet>>? = mutableListOf()
    private var currentInteger: MutableList<Char> = mutableListOf()
    private var finalPacket: MutableList<Packet> = mutableListOf()

    fun startList() {
      currentList!!.add(mutableListOf())
    }

    fun char(data: Char) {
      currentInteger.add(data)
    }

    fun separator() {
      if (currentInteger.isNotEmpty()) {
        currentList!!.last().add(
          IntegerPacket(currentInteger.toCharArray().concatToString().toInt())
        )
        currentInteger.clear()
      }
    }

    fun endList() {
      if (currentInteger.isNotEmpty()) {
        // implicit separator by ending list
        separator()
      }
      val finishedList = currentList!!.removeLast()
      if (currentList!!.isEmpty()) {
        finalPacket.add(ListPacket(finishedList))
      } else {
        currentList!!.last().add(ListPacket(finishedList))
      }
    }

    fun complete(): ListPacket {
      return ListPacket(finalPacket)
    }
  }

  fun parseInput(s: String): ListPacket {
    val b = PacketsBuilder()
    s.toCharArray().forEach { c ->
      when {
        c == '[' -> b.startList()
        c.isDigit() -> b.char(c)
        c == ',' -> b.separator()
        c == ']' -> b.endList()
      }
    }
    val parsed = b.complete()
    return parsed
  }

  override fun part1(input: Lines): Any {
    val packets = input
      .chunked(3)
      .map { it.take(2) }
      .map { (left, right) ->
        parseInput(left) to parseInput(right)
      }

    val orderedIndices = packets
      .withIndex()
      .filter { indexedValue ->
        val (_, pair) = indexedValue
        val (left, right) = pair
        left < right
      }
      .map { it.index + 1 }

    return orderedIndices.sum()
  }

  override fun part2(input: Lines): Any {
    val divider1 = ListPacket(mutableListOf(ListPacket(mutableListOf(IntegerPacket(2)))))
    val divider2 = ListPacket(mutableListOf(ListPacket(mutableListOf(IntegerPacket(6)))))

    val packets = input
      .filterNot { it.isEmpty() }
      .map { parseInput(it) }
      .plus(listOf(divider1, divider2))
      .sorted()

    val divider1Index = packets.indexOf(divider1) + 1
    val divider2Index = packets.indexOf(divider2) + 1

    return divider1Index * divider2Index
  }
}
