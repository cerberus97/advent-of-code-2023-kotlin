import kotlin.math.max

fun main() {
  fun part1(input: List<String>): Int {
    val maxAllowed =
      mapOf(
        "red" to 12,
        "green" to 13,
        "blue" to 14,
      )
    return input.sumOf { game ->
      val id = game.split(':').first().split(' ').last().toInt()
      val cubeSets = game.split(':').last().split(';')
      for (cubeSet in cubeSets) {
        for (cubes in cubeSet.split(',')) {
          val (cnt, col) = cubes.split(' ').filter { it.isNotEmpty() }
          if (cnt.toInt() > maxAllowed[col]!!) {
            return@sumOf 0
          }
        }
      }
      id
    }
  }

  fun part2(input: List<String>): Int {
    return input.sumOf { game ->
      val maxSeen = buildMap {
        val cubeSets = game.split(':').last().split(';')
        for (cubeSet in cubeSets) {
          for (cubes in cubeSet.split(',')) {
            val (cnt, col) = cubes.split(' ').filter { it.isNotEmpty() }
            this[col] = max(this[col] ?: 0, cnt.toInt())
          }
        }
      }
      maxSeen.values.reduce { x, y -> x * y }
    }
  }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
