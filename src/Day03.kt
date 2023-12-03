fun main() {
  fun String.hasDigit(idx: Int): Boolean = getOrNull(idx)?.isDigit() ?: false

  fun Char.isSymbol(): Boolean = !(this.isDigit() || this == '.')

  fun List<String>.allSurroundingSymbols(i1: Int, i2: Int, j1: Int, j2: Int): List<Pair<Int, Int>> {
    return buildList {
      for (i in i1..i2) {
        for (j in j1..j2) {
          if (this@allSurroundingSymbols.getOrNull(i)?.getOrNull(j)?.isSymbol() == true) {
            this += Pair(i, j)
          }
        }
      }
    }
  }

  fun String.getNumberAndEndPosStarting(pos: Int): Pair<Int, Int> {
    var endPos = pos
    var num = this[pos].digitToInt()
    while (this.hasDigit(endPos + 1)) {
      ++endPos
      num = (num * 10) + this[endPos].digitToInt()
    }
    return Pair(num, endPos)
  }

  fun part1(input: List<String>): Int {
    var ans = 0
    for (i in input.indices) {
      for (j in input[i].indices) {
        if (input[i].hasDigit(j) && !input[i].hasDigit(j - 1)) {
          val (num, endPos) = input[i].getNumberAndEndPosStarting(j)
          if (input.allSurroundingSymbols(i - 1, i + 1, j - 1, endPos + 1).isNotEmpty()) {
            ans += num
          }
        }
      }
    }
    return ans
  }

  fun part2(input: List<String>): Int {
    val gearToNumbers = mutableMapOf<Pair<Int, Int>, MutableList<Int>>()
    for (i in input.indices) {
      for (j in input[i].indices) {
        if (input[i].hasDigit(j) && !input[i].hasDigit(j - 1)) {
          val (num, endPos) = input[i].getNumberAndEndPosStarting(j)
          val surroundingSymbols = input.allSurroundingSymbols(i - 1, i + 1, j - 1, endPos + 1)
          for ((x, y) in surroundingSymbols) {
            if (input[x][y] == '*') {
              gearToNumbers.getOrPut(Pair(x, y)) { mutableListOf() } += num
            }
          }
        }
      }
    }
    return gearToNumbers.values.filter { it.size == 2 }.sumOf { it[0] * it[1] }
  }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
