package day16

import Day
import Lines

class Day16 : Day() {
  val INPUT_PATTERN = """^Valve (.+) has flow rate=(\d+); tunnels? leads? to valves? (.*)$""".toRegex()

  sealed class PathElement {
    abstract val node: String
  }
  data class Open(override val node: String): PathElement()
  data class Move(override val node: String): PathElement()

  class Djikstra(
    val from: String,
    val flowRates: Map<String, Int>,
    val tunnels: Map<String, List<String>>,
  ) {
    val unvisitedNodes = flowRates.keys.toMutableSet()
    val paths = flowRates.keys
      .associateWith { if (it == from) emptyList() else unvisitedNodes.toList() }
      .toMutableMap()
    var current = from

    tailrec fun step() {
      if (paths[current] == null) {
        paths[current] = emptyList()
      }
      tunnels.getValue(current)
        .filter { it in unvisitedNodes }
        .forEach { tunnel ->
          val pTunnel = paths.getValue(tunnel)
          val pCurrent = paths.getValue(current)
          if (pCurrent.size + 1 < pTunnel.size) {
            paths[tunnel] = pCurrent + tunnel
          }
        }

      // mark current node as visited
      unvisitedNodes -= current

      if (unvisitedNodes.none { flowRates.getValue(it) > 0 }) {
        return
      } else {
        val minUnvisited = unvisitedNodes.minBy { paths[it]?.size ?: -1 }
        current = minUnvisited
        step()
      }
    }
  }

  override fun part1(input: Lines): Any {
    val (flowRates, tunnels) = parseInput(input)
    val (best, _) = solveForOneAgent(flowRates, tunnels, 30)
    return best
  }

  override fun part2(input: Lines): Any {
    val (flowRates, tunnels) = parseInput(input)
    val (best, bestPath) = solveForOneAgent(flowRates, tunnels, 26)

    // naiive solution: reset time to 0, and then have the elephant open additional valves
    // create a new graph for the elephant, with the already opened valves set to a flow rate of 0 (then the
    // "additional" score calculated for the elephant will be additive
    // -- interestingly, I didn't think this would work because I didn't think this would be optimal, and in
    // fact for the smaller sample problem, it actually does *not* work because the first agent has time to
    // go and do stuff that the elephant would have done had they been operating simultaneously!
    // However, for the larger actual problem it does work!
    // I guess a proper solution would be track the state of each agent through the graph at each time
    bestPath.filterIsInstance<Open>().forEach { v ->
      flowRates[v.node] = 0
    }

    val (best2, _) = solveForOneAgent(flowRates, tunnels, 26)

    return best + best2
  }

  fun parseInput(input: Lines): Pair<MutableMap<String, Int>, MutableMap<String, List<String>>> {
    val flowRates = mutableMapOf<String, Int>()
    val tunnels = mutableMapOf<String, List<String>>()

    input.forEach { line ->
      val (iv, f, ov) = INPUT_PATTERN.matchEntire(line)!!.destructured
      flowRates[iv] = f.toInt()
      tunnels[iv] = ov.split(", ")
    }

    return flowRates to tunnels
  }

  fun solveForOneAgent(
    flowRates: MutableMap<String, Int>,
    tunnels: MutableMap<String, List<String>>,
    minutes: Int,
  ): Pair<Int, List<PathElement>> {
    val nonZeroValves = flowRates.filter { it.value > 0 }
    val valveShortestPaths = (nonZeroValves.keys + "AA").associateWith { v ->
      Djikstra(v, flowRates, tunnels).apply { step() }.paths
    }

    fun List<PathElement>.isDone() = filterIsInstance<Open>().size >= nonZeroValves.size || size >= minutes

    var best = 0
    var bestPath: List<PathElement> = emptyList()

    fun pathsFromNode(node: String, path: List<PathElement>, depth: Int) {
      if (path.isDone()) {
        // cost of path
        val pressureReleased = calculatePath(path, minutes, flowRates)
        if (pressureReleased > best) {
          best = pressureReleased
          bestPath = path
        }
        return
      }

      // possible moves now are to any of the unopened valves
      nonZeroValves.keys.minus(path.filterIsInstance<Open>().map { it.node }.toSet()).forEach { v ->
        // move to v from wherever we are
        val next = valveShortestPaths[node]!![v]!!
        pathsFromNode(v,path + (next.dropLast(1).map { Move(it) } + Move(next.last()) + Open(next.last())), depth + 1)
      }
    }

    // interesting approach explained by Jonathan Paulson -- use Dynamic Programming (DP) to
    // snapshot states, and then use previously computed answers for those states
    // https://youtu.be/DgqkVDr1WX8?t=449
    // interestingly, my solution runs faster in Kotlin than his does on C++, I guess because I am skipping
    // all the zero-flow nodes and so my search space is way smaller, so it runs faster even without DP.

    pathsFromNode("AA", emptyList(), 0)

    return best to bestPath
  }

  fun calculatePath(path: List<PathElement>, maxMinutes: Int, flowRates: Map<String, Int>): Int {
    val openedValves = mutableListOf<String>()
    var pressureReleased = 0

    for (minute in 1..maxMinutes) {
      val pressureReleasedNow = openedValves.sumOf { flowRates.getValue(it) }
      pressureReleased += pressureReleasedNow
      val p = path.getOrNull(minute - 1)
      if (p is Open) openedValves.add(p.node)
    }

    return pressureReleased
  }
}
