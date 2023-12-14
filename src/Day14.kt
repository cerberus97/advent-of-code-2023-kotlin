import kotlin.math.min

fun main() {
  fun List<List<Char>>.moveNorth(): List<List<Char>> {
    val n = this.size
    val m = this[0].size
    val output = MutableList(n) { MutableList(m) { '.' } }
    for (j in 0 until m) {
      var nextFreePosition = n
      for (i in 0 until n) {
        when (this[i][j]) {
          'O' -> {
            nextFreePosition = min(nextFreePosition, i)
            output[nextFreePosition][j] = 'O'
            ++nextFreePosition
          }
          '.' -> {
            nextFreePosition = min(nextFreePosition, i)
          }
          '#' -> {
            nextFreePosition = n
            output[i][j] = '#'
          }
        }
      }
    }
    return output
  }

  fun List<List<Char>>.getLoad(): Int {
    var totalLoad = 0
    for (i in this.indices) {
      for (j in this[i].indices) {
        if (this[i][j] == 'O') {
          totalLoad += (this.size - i)
        }
      }
    }
    return totalLoad
  }

  fun part1(input: List<String>): Int = input.map { it.toList() }.moveNorth().getLoad()

  fun List<List<Char>>.rotate(): List<List<Char>> {
    val n = this.size
    val m = this[0].size
    return List(m) { i -> List(n) { j -> this[n - j - 1][i] } }
  }

  fun part2(input: List<String>): Int {
    var currentGrid = input.map { it.toList() }
    val seen = mutableMapOf<List<List<Char>>, Int>()
    var numCycles = 0
    val targetCycles = 1000000000
    while (numCycles < targetCycles) {
      repeat(4) { currentGrid = currentGrid.moveNorth().rotate() }
      ++numCycles
      if (currentGrid in seen) {
        val cycleLength = seen[currentGrid]!! - numCycles
        val remainingCycles = (targetCycles - numCycles) % cycleLength
        repeat(remainingCycles * 4) { currentGrid = currentGrid.moveNorth().rotate() }
        return currentGrid.getLoad()
      }
      seen[currentGrid] = numCycles
    }
    return -1
  }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
