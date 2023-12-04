import kotlin.math.min

fun main() {
  fun String.parseSpaceSeparatedInts() = split(' ').filter { it.isNotEmpty() }.map { it.toInt() }

  fun part1(input: List<String>): Int {
    return input.sumOf { card ->
      val cardLists = card.substringAfter(':').split('|').map { it.parseSpaceSeparatedInts() }
      val overlap = cardLists[0].intersect(cardLists[1].toSet()).size
      if (overlap > 0) {
        1 shl (overlap - 1)
      } else {
        0
      }
    }
  }

  fun part2(input: List<String>): Int {
    val cardCounts = MutableList(input.size + 1) { 0 }
    cardCounts[0] = 1
    cardCounts[input.size] = -1
    for ((id, card) in input.withIndex()) {
      cardCounts[id + 1] += cardCounts[id]
      val cardLists = card.substringAfter(':').split('|').map { it.parseSpaceSeparatedInts() }
      val overlap = cardLists[0].intersect(cardLists[1].toSet()).size
      cardCounts[id + 1] += cardCounts[id]
      cardCounts[min(input.size, id + overlap + 1)] -= cardCounts[id]
    }
    return cardCounts.sum()
  }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
