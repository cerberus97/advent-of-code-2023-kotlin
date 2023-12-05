fun main() {
  fun String.parseSpaceSeparatedLongs() = split(' ').mapNotNull { it.toLongOrNull() }

  fun part1(input: List<String>): Long {
    val src = mutableSetOf<Long>()
    val dst = mutableSetOf<Long>()
    for (row in input) {
      if (row.startsWith("seeds: ")) {
        dst += row.parseSpaceSeparatedLongs()
      } else if (row.endsWith("map:")) {
        src += dst
        dst.clear()
      } else if (row.isNotEmpty()) {
        val (dstStart, srcStart, rSize) = row.parseSpaceSeparatedLongs()
        src
          .filter { it in srcStart ..< srcStart + rSize }
          .map {
            dst.add(dstStart + it - srcStart)
            src.remove(it)
          }
      }
    }
    return (src + dst).min()
  }

  fun part2(input: List<String>): Long {
    val src = mutableMapOf<Long, Long>()
    val dst = mutableMapOf<Long, Long>()
    for (row in input) {
      if (row.startsWith("seeds: ")) {
        dst +=
          row
            .parseSpaceSeparatedLongs()
            .withIndex()
            .groupBy { (id, _) -> id / 2 }
            .values
            .associate { seedPair ->
              seedPair[0].value to seedPair[0].value + seedPair[1].value - 1
            }
      } else if (row.endsWith("map:")) {
        src += dst
        dst.clear()
      } else if (row.isNotEmpty()) {
        val (dstStart, srcStart, rSize) = row.parseSpaceSeparatedLongs()
        val srcEnd = srcStart + rSize - 1
        val dstEnd = dstStart + rSize - 1
        src.entries
          .filter { (curStart, curEnd) -> srcStart in curStart + 1..curEnd }
          .forEach { (curStart, curEnd) ->
            src[curStart] = srcStart - 1
            src += srcStart to curEnd
          }
        src.entries
          .filter { (curStart, curEnd) -> srcEnd in curStart ..< curEnd }
          .forEach { (curStart, curEnd) ->
            src[curStart] = srcEnd
            src += (srcEnd + 1) to curEnd
          }
        src.entries
          .filter { (curStart, curEnd) -> srcStart <= curStart && curEnd <= srcEnd }
          .forEach { (curStart, curEnd) ->
            src.remove(curStart)
            dst += (dstStart + curStart - srcStart) to (dstEnd + curEnd - srcEnd)
          }
      }
    }
    return (src + dst).keys.min()
  }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
