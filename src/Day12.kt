fun main() {
  fun String.parseCommaSeparatedInts() = split(',').mapNotNull { it.toIntOrNull() }

  fun solveRow(row: String, numCopies: Int): Long {
    val splitRow = row.split(' ')
    val groupSizes = (splitRow[1] + ',').repeat(numCopies).parseCommaSeparatedInts()
    val pattern = (splitRow[0] + '?').repeat(numCopies).dropLast(1) + '.'
    val n = groupSizes.size
    val m = pattern.length
    val maxStartingAt = MutableList(m + 1) { 0 }
    for (j in m - 1 downTo 0) {
      maxStartingAt[j] = if (pattern[j] == '.') 0 else 1 + maxStartingAt[j + 1]
    }
    val dp = MutableList(n + 1) { MutableList(m + 1) { 0L } }
    dp[0][0] = 1
    for (i in 0..n) {
      for (j in 0 until m) {
        if (pattern[j] != '#') {
          dp[i][j + 1] += dp[i][j]
        }
        if (i < n && maxStartingAt[j] >= groupSizes[i] && pattern[j + groupSizes[i]] != '#') {
          dp[i + 1][j + 1 + groupSizes[i]] += dp[i][j]
        }
      }
    }
    return dp[n][m]
  }

  fun part1(input: List<String>): Long = input.sumOf { row -> solveRow(row, numCopies = 1) }

  fun part2(input: List<String>): Long = input.sumOf { row -> solveRow(row, numCopies = 5) }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
