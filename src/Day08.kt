import MathUtils.chineseRemainderTheorem
import MathUtils.takeMod

fun main() {
  fun List<String>.parseToGraph(): Map<String, Pair<String, String>> = associate { nodeInfo ->
    val regexMatch = Regex("""(...) = \((...), (...)\)""").matchEntire(nodeInfo)
    val (cur, toLeft, toRight) = regexMatch!!.destructured
    cur to Pair(toLeft, toRight)
  }

  fun getNextPosition(
    curPosition: String,
    graph: Map<String, Pair<String, String>>,
    instruction: Char,
  ): String =
    when (instruction) {
      'L' -> graph[curPosition]!!.first
      'R' -> graph[curPosition]!!.second
      else -> throw Exception("Sir this is a 2D world.")
    }

  fun countStepsToEnd(
    startPosition: String,
    graph: Map<String, Pair<String, String>>,
    instructions: String,
    isEndPosition: String.() -> Boolean,
  ): Int {
    var curPosition = startPosition
    var steps = 0
    val maxPossibleSteps = graph.size * instructions.length
    while (!curPosition.isEndPosition() && steps <= maxPossibleSteps) {
      curPosition = getNextPosition(curPosition, graph, instructions[steps % instructions.length])
      ++steps
    }
    return if (steps > maxPossibleSteps) -1 else steps
  }

  fun part1(input: List<String>): Int {
    val instructions = input[0]
    val graph = input.subList(2, input.size).parseToGraph()
    return countStepsToEnd("AAA", graph, instructions) { this == "ZZZ" }
  }

  fun String.isPart2EndPosition() = endsWith('Z')

  data class ProcessedPosition(
    val cycleLength: Long,
    val stepsUntilCycleStarts: Long,
    val allStepsWithEndPositionsInCycle: List<Long>,
  )

  fun processStartPosition(
    startPosition: String,
    graph: Map<String, Pair<String, String>>,
    instructions: String,
  ): ProcessedPosition {
    val seenStates = mutableMapOf<Pair<String, Int>, Long>()
    var curPosition = startPosition
    var curInstructionId = 0
    var curSteps = 0L
    val stepsWithEndPositions = mutableListOf<Long>()
    while (true) {
      if (curPosition.isPart2EndPosition()) stepsWithEndPositions += curSteps

      seenStates += Pair(curPosition, curInstructionId) to curSteps
      curPosition = getNextPosition(curPosition, graph, instructions[curInstructionId])
      curInstructionId = (curInstructionId + 1) % instructions.length
      ++curSteps

      if (Pair(curPosition, curInstructionId) in seenStates) {
        val stepsUntilCycleStarts = seenStates[Pair(curPosition, curInstructionId)]!!
        val cycleLength = curSteps - stepsUntilCycleStarts
        return ProcessedPosition(
          cycleLength,
          stepsUntilCycleStarts,
          stepsWithEndPositions.filter { it >= stepsUntilCycleStarts }
        )
      }
    }
  }

  fun List<Long>.toBigIntegerList() = map { it.toBigInteger() }

  // Recursively check all combinations of cyclePositions. For each combination, use Chinese
  // Remainder Theorem to find the first time at which all positions will intersect.
  fun findFirstIntersection(
    positions: List<ProcessedPosition>,
    cyclePositions: List<Long>,
    cycleLengths: List<Long>
  ): Long {
    if (positions.isEmpty()) {
      val ans =
        chineseRemainderTheorem(cycleLengths.toBigIntegerList(), cyclePositions.toBigIntegerList())
      return if (ans.second.toLong() == -1L) Long.MAX_VALUE else ans.first.toLong()
    }
    return positions.first().allStepsWithEndPositionsInCycle.minOf { cyclePosition ->
      findFirstIntersection(
        positions.drop(1),
        cyclePositions + listOf(cyclePosition),
        cycleLengths + listOf(positions.first().cycleLength)
      )
    }
  }

  fun part2(input: List<String>): Long {
    val instructions = input[0]
    val graph = input.subList(2, input.size).parseToGraph()
    val startPositions = graph.keys.filter { it.last() == 'A' }

    val processedPositions = startPositions.map { processStartPosition(it, graph, instructions) }
    val minStepsToStartAllCycles = processedPositions.minOf { it.stepsUntilCycleStarts }

    // Shift positions so that all are inside their cycles. The answer isn't small (sadly) so we can
    // ignore the values until then.
    val shiftedPositions =
      processedPositions.map { initialPosition ->
        ProcessedPosition(
          cycleLength = initialPosition.cycleLength,
          stepsUntilCycleStarts = 0,
          allStepsWithEndPositionsInCycle =
            initialPosition.allStepsWithEndPositionsInCycle.map {
              takeMod(it - minStepsToStartAllCycles, initialPosition.cycleLength)
            }
        )
      }

    return findFirstIntersection(shiftedPositions, emptyList(), emptyList()) +
      minStepsToStartAllCycles
  }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
