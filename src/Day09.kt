fun main() {
  fun String.parseSpaceSeparatedInts() = split(' ').mapNotNull { it.toIntOrNull() }

  fun findNextNumber(nums: List<Int>): Int {
    if (nums.all { it == 0 }) {
      return 0
    }
    val differences = buildList {
      for (i in 1 until nums.size) {
        this += nums[i] - nums[i - 1]
      }
    }
    return findNextNumber(differences) + nums.last()
  }

  fun part1(input: List<String>): Int {
    return input.sumOf { row -> findNextNumber(row.parseSpaceSeparatedInts()) }
  }

  fun part2(input: List<String>): Int {
    return input.sumOf { row -> findNextNumber(row.parseSpaceSeparatedInts().reversed()) }
  }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
