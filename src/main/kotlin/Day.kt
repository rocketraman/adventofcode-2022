import org.junit.jupiter.api.Test
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

typealias Lines = List<String>

abstract class Day {
    abstract fun part1(input: Lines): Long
    abstract fun part2(input: Lines): Long

    @OptIn(ExperimentalTime::class)
    private fun solve(inputFileName: String, solution: (Lines) -> Long) {
        val lines = this::class.java.getResource(inputFileName)?.toURI()?.let(::File)?.readLines()
            ?: error("$inputFileName not found")
        val (value, duration) = measureTimedValue { solution(lines) }
        val time = duration.toComponents { seconds, nanoseconds -> "${seconds}s ${nanoseconds}ns" }
        println("Solution took ${duration.inWholeNanoseconds}ns or $time. Answer: $value")
    }

    @Test
    fun part1(): Unit = solve("part1.txt", ::part1)

    @Test
    fun part2(): Unit = solve("part2.txt", ::part2)
}