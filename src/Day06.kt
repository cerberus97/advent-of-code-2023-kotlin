fun main() {
  fun String.parseSpaceSeparatedInts() = split(' ').mapNotNull { it.toIntOrNull() }

  fun part1(input: List<String>): Int {
    val times = input[0].parseSpaceSeparatedInts()
    val distances = input[1].parseSpaceSeparatedInts()
    var ans = 1
    for (i in times.indices) {
      var lo = 1
      var hi = times[i] / 2
      while (lo <= hi) {
        val mid = (lo + hi) / 2
        if (mid * (times[i] - mid) > distances[i]) {
          hi = mid - 1
        } else {
          lo = mid + 1
        }
      }
      ans *= (times[i] - 2 * hi - 1)
    }
    return ans
  }

  fun part2(input: List<String>): Long {
    val times = listOf(input[0].filter { it.isDigit() }.toLong())
    val distances = listOf(input[1].filter { it.isDigit() }.toLong())
    var ans: Long = 1
    for (i in times.indices) {
      var lo: Long = 1
      var hi = times[i] / 2
      while (lo <= hi) {
        val mid = (lo + hi) / 2
        if (mid * (times[i] - mid) > distances[i]) {
          hi = mid - 1
        } else {
          lo = mid + 1
        }
      }
      ans *= (times[i] - 2 * hi - 1)
    }
    return ans
  }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
