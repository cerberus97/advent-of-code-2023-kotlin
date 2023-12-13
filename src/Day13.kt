fun main() {
  fun parseInput(input: List<String>): List<List<String>> {
    val breakPoints =
      listOf(-1) +
        input.mapIndexedNotNull { id, row -> id.takeIf { row.isEmpty() } } +
        listOf(input.size)

    return breakPoints.windowed(2).map { (before, after) -> input.subList(before + 1, after) }
  }

  fun findHorizontalReflection(grid: List<String>, numMismatches: Int): Int {
    val n = grid.size
    for (rowsAbove in 1 ..< n) {
      // Check for reflected line between rows with index rowsAbove - 1, rowsAbove
      var mismatchCount = 0
      var id1 = rowsAbove - 1
      var id2 = rowsAbove
      while (id1 >= 0 && id2 < n) {
        for (j in grid[id1].indices) {
          mismatchCount += if (grid[id1][j] != grid[id2][j]) 1 else 0
        }
        id1--
        id2++
      }
      if (mismatchCount == numMismatches) {
        return rowsAbove
      }
    }
    return -1
  }

  fun List<String>.transpose(): List<String> {
    val n = this.size
    val m = this[0].length
    return List(m) { i -> (0 until n).map { j -> this[j][i] }.toString() }
  }

  fun solve(input: List<String>, numMismatches: Int): Int {
    val grids = parseInput(input)
    return grids.sumOf { grid ->
      val rowsAbove = findHorizontalReflection(grid, numMismatches)
      if (rowsAbove != -1) {
        return@sumOf rowsAbove * 100
      }
      val colsLeft = findHorizontalReflection(grid.transpose(), numMismatches)
      colsLeft
    }
  }

  fun part1(input: List<String>): Int = solve(input, numMismatches = 0)

  fun part2(input: List<String>): Int = solve(input, numMismatches = 1)

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
