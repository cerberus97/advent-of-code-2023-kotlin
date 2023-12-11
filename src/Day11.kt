import kotlin.math.max
import kotlin.math.min

fun main() {
  fun Pair<Int, Int>.distance(o: Pair<Int, Int>, rowDist: List<Long>, colDist: List<Long>): Long =
    rowDist[max(o.second, second) + 1] - rowDist[min(o.second, second) + 1] +
      colDist[max(o.first, first) + 1] - colDist[min(o.first, first) + 1]

  fun solve(input: List<String>, expansionFactor: Long): Long {
    val n = input.size
    val m = input[0].length
    val galaxies = mutableListOf<Pair<Int, Int>>()
    for (i in input.indices) {
      for (j in input[i].indices) {
        if (input[i][j] == '#') {
          galaxies += Pair(i, j)
        }
      }
    }
    val isRowEmpty = List(n) { row -> !galaxies.any { it.first == row } }
    val isColEmpty = List(m) { col -> !galaxies.any { it.second == col } }
    val rowDist = MutableList(m + 1) { 0L }
    val colDist = MutableList(n + 1) { 0L }
    for (j in 1..m) {
      rowDist[j] = rowDist[j - 1] + if (isColEmpty[j - 1]) expansionFactor else 1
    }
    for (i in 1..n) {
      colDist[i] = colDist[i - 1] + if (isRowEmpty[i - 1]) expansionFactor else 1
    }
    var ans = 0L
    for (i in galaxies.indices) {
      for (j in 0 until i) {
        ans += galaxies[i].distance(galaxies[j], rowDist, colDist)
      }
    }
    return ans
  }

  fun part1(input: List<String>): Long = solve(input, expansionFactor = 2)

  fun part2(input: List<String>): Long = solve(input, expansionFactor = 1000000)

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
