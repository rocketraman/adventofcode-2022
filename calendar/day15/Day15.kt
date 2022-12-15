package day15

import Day
import Lines
import java.util.BitSet
import kotlin.math.abs

typealias BitsetVisitorAtRow = (Int, BitSet) -> Unit

class Day15 : Day() {
  val INPUT_REGEX = """^Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)$""".toRegex()

  data class Point(val x: Int, val y: Int)

  fun Point.distance(p2: Point) =
    abs(x - p2.x) + abs(y - p2.y)

  fun coveredPointsAtY(origin: Point, distance: Int): (Int) -> IntRange? {
    val maxX = origin.x + distance
    val minX = origin.x - distance

    return { y ->
      val distanceY = abs(origin.y - y)
      val minXAtY = minX + distanceY
      val maxXAtY = maxX - distanceY
      if (minXAtY <= maxXAtY) minXAtY..maxXAtY else null
    }
  }

  /**
   * Using [coveredPointsAtY] is not fast enough for part 2, as we need to test 4 million rows of y, not just 1.
   * Because we can easily calculate if there are any points possible on a given row y, if none ore possible we don't
   * bother with visiting the bitset -- we just skip that row for this origin using [validForY].
   *
   * This is still ridiculously slow though, 2:30 run time.
   */
  fun bitsetVisitorAtY(origin: Point, searchSpace: Int, distance: Int): BitsetVisitorAtRow {
    val maxX = origin.x + distance
    val minX = origin.x - distance

    return { y, targetBitset ->
      val distanceY = abs(origin.y - y)
      val minXAtY = (minX + distanceY).coerceAtLeast(0)
      val maxXAtY = (maxX - distanceY).coerceAtMost(searchSpace + 1)
      if (minXAtY <= maxXAtY) targetBitset.apply {
        set(minXAtY, maxXAtY + 1)
      }
    }
  }

  @Suppress("DestructuringDeclarationWithTooManyEntries")
  override fun part1(input: Lines): Any {
    val sensorsBeacons = input.map {
      val (sensorX, sensorY, beaconX, beaconY) = INPUT_REGEX.matchEntire(it)!!.groupValues
        .drop(1).map { it.toInt() }
      Point(sensorX, sensorY) to Point(beaconX, beaconY)
    }

    val rangesAtY = sensorsBeacons.map { (sensor, beacon) ->
      val excludedDistance = sensor.distance(beacon)
      val coveredXAtY = coveredPointsAtY(sensor, excludedDistance)
      sensor to coveredXAtY
    }

    val y = 2000000

    val xRanges = rangesAtY.mapNotNull { it.second(y) }
    val minX = xRanges.minOf { it.first }
    val maxX = xRanges.maxOf { it.last }

    return maxX - minX
  }

  @Suppress("DestructuringDeclarationWithTooManyEntries")
  override fun part2(input: Lines): Any {
    val searchSpace = 4000000
//    val searchSpace = 20

    // there are only 27 of these
    val sensorsBeacons = input.map {
      val (sensorX, sensorY, beaconX, beaconY) = INPUT_REGEX.matchEntire(it)!!.groupValues
        .drop(1).map { it.toInt() }
      Point(sensorX, sensorY) to Point(beaconX, beaconY)
    }

    val eachSensorBitsetVisitorAtY = sensorsBeacons.map { (sensor, beacon) ->
      val excludedDistance = sensor.distance(beacon)
      bitsetVisitorAtY(sensor, searchSpace, excludedDistance)
    }

    val bitSet1 = BitSet(searchSpace + 1)
    val bitSet2 = BitSet(searchSpace + 1)

    val beacon = (0..searchSpace)
      .asSequence()
      .mapNotNull { y ->
        bitSet1.clear()
        bitSet2.clear()
        eachSensorBitsetVisitorAtY.forEach { it(y, bitSet2) }
        bitSet1.or(bitSet2)
        val x = bitSet1.nextClearBit(0)
        if (x == -1 || x > searchSpace + 1) null else Point(x, y)
      }
      .first()

    println("beacon=$beacon")

    return beacon.x.toLong() * 4000000L + beacon.y.toLong()
  }
}
