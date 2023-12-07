fun main() {
  val cardsInRankedOrderPart1 =
    listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2').reversed()

  val cardsInRankedOrderPart2 =
    listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J').reversed()

  fun String.getCardRanks(cardsInRankedOrder: List<Char>): List<Int> = map {
    cardsInRankedOrder.indexOf(it)
  }

  fun String.getTypeRank(): List<Int> {
    val sizes = this.groupBy { it }.values.map { it.size }
    return listOf(sizes.max(), -sizes.size)
  }

  val handComparator =
    Comparator<Pair<List<Int>, Int>> { hand0, hand1 ->
      for (i in hand0.first.indices) {
        if (hand0.first[i] < hand1.first[i]) return@Comparator -1
        if (hand0.first[i] > hand1.first[i]) return@Comparator 1
      }
      return@Comparator 0
    }

  fun part1(input: List<String>): Int {
    return input
      .map {
        val (hand, value) = it.split(' ')
        (hand.getTypeRank() + hand.getCardRanks(cardsInRankedOrderPart1)) to value.toInt()
      }
      .sortedWith(handComparator)
      .withIndex()
      .sumOf { (idx, hand) -> (idx + 1) * hand.second }
  }

  fun String.getTypeRankWithJoker(): List<Int> {
    val numJokers = this.count { it == 'J' }
    val otherSizes =
      this.filterNot { it == 'J' }.groupBy { it }.values.map { it.size }.ifEmpty { listOf(0) }
    return listOf(otherSizes.max() + numJokers, -otherSizes.size)
  }

  fun part2(input: List<String>): Int {
    return input
      .map {
        val (hand, value) = it.split(' ')
        (hand.getTypeRankWithJoker() + hand.getCardRanks(cardsInRankedOrderPart2)) to value.toInt()
      }
      .sortedWith(handComparator)
      .withIndex()
      .sumOf { (idx, hand) -> (idx + 1) * hand.second }
  }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
