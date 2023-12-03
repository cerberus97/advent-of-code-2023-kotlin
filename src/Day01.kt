fun main() {
  fun part1(input: List<String>): Int =
    input.sumOf { row ->
      10 * row.first { it.isDigit() }.digitToInt() + row.last { it.isDigit() }.digitToInt()
    }

  val digitWordsToInt =
    mapOf(
      "one" to 1,
      "two" to 2,
      "three" to 3,
      "four" to 4,
      "five" to 5,
      "six" to 6,
      "seven" to 7,
      "eight" to 8,
      "nine" to 9,
      "1" to 1,
      "2" to 2,
      "3" to 3,
      "4" to 4,
      "5" to 5,
      "6" to 6,
      "7" to 7,
      "8" to 8,
      "9" to 9,
    )

  fun String.digitAtPosition(pos: Int): Int? {
    for ((digitWord, digitInt) in digitWordsToInt) {
      if (this.substring(pos).startsWith(digitWord)) return digitInt
    }
    return null
  }

  fun String.firstDigitOverIndices(indices: IntProgression): Int {
    for (pos in indices) {
      this.digitAtPosition(pos)?.let {
        return it
      }
    }
    throw Exception("No first digit found in $this")
  }

  fun part2(input: List<String>): Int =
    input.sumOf { row ->
      10 * row.firstDigitOverIndices(row.indices) +
        row.firstDigitOverIndices(row.indices.reversed())
    }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part2(readInput("part2_sample")).println()

  part1(readInput("part1_input")).println()
  part2(readInput("part2_input")).println()
}
