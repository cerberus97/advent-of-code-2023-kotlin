import java.util.*
import kotlin.math.abs
import kotlin.math.sign

fun main() {
  fun List<String>.find(c: Char): Pair<Int, Int> {
    for (i in this.indices) {
      for (j in this[0].indices) {
        if (this[i][j] == c) {
          return Pair(i, j)
        }
      }
    }
    return Pair(-1, -1)
  }

  fun part1(input: List<String>, targetSteps: Int): Int {
    val n = input.size
    val m = input[0].length
    val start = input.find('S')
    val distances = MutableList(n) { MutableList(m) { Int.MAX_VALUE } }
    distances[start.first][start.second] = 0
    val queue = LinkedList(listOf(start))
    while (queue.isNotEmpty()) {
      val cur = queue.poll()
      val curDist = distances[cur.first][cur.second]
      for (dir in Direction.entries) {
        val nxt = dir.go(cur)
        if (
          nxt.first in input.indices &&
            nxt.second in input[0].indices &&
            input[nxt.first][nxt.second] != '#' &&
            distances[nxt.first][nxt.second] > curDist + 1
        ) {
          distances[nxt.first][nxt.second] = curDist + 1
          queue += nxt
        }
      }
    }
    return distances.sumOf {
      it.count { dist -> (dist <= targetSteps) && (dist % 2 == targetSteps % 2) }
    }
  }

  fun List<String>.getWrapped(i: Int, j: Int, n: Int, m: Int): Char {
    return this[((i % n) + n) % n][((j % m) + m) % m]
  }

  fun Map<Int, Map<Int, Int>>.getOrMaxValue(i: Int, j: Int): Int {
    return this.getOrDefault(i, emptyMap()).getOrDefault(j, Int.MAX_VALUE)
  }

  /**
   * Count n such that:
   * - lo <= n <= hi
   * - n % 2 == parity
   */
  fun countInRangeWithParity(lo: Long, hi: Long, parity: Long): Long {
    val actual_lo = if (abs(lo) % 2 == parity) lo else lo + 1
    val actual_hi = if (abs(hi) % 2 == parity) hi else hi - 1
    if (actual_lo > actual_hi) {
      return 0L
    }
    return (actual_hi - actual_lo) / 2 + 1
  }

  /**
   * Count (p, q) such that:
   * - p, q >= 1
   * - p + q <= hi
   * - (p + q) % 2 == parity
   */
  fun countSumInRangeWithParity(hi: Long, parity: Long): Long {
    val actual_hi = if (abs(hi) % 2 == parity) hi else hi - 1
    if (actual_hi < 2) {
      return 0L
    }

    var ans = 0L

    // Case 1: p is odd
    // p: 1, 3, 5, ...
    // q: 1 + parity, 3 + parity, ..., actual_hi - p
    var max_q_cnt = (actual_hi - parity - 2) / 2 + 1
    ans += (max_q_cnt * (max_q_cnt + 1)) / 2

    // Case 2: p is even
    // p: 2, 4, 6, ...
    // q: 2 - parity, 4 - parity, ..., actual_hi - p
    max_q_cnt = (actual_hi + parity - 4) / 2 + 1
    ans += (max_q_cnt * (max_q_cnt + 1)) / 2

    return ans
  }

  /**
   * Key observations (for the main input only):
   * 1. n = m == 1 (mod 2) [leads to simplifications]
   * 2. The distance of the border elements is always the manhattan distance from the Start.
   * 3. Grids can be divided into 9 (3 * 3) types, depending on if they are (above, below, same) and
   *    (right, left, same) of the initial grid. All grids in each type behave similarly with
   *    respect to the shortest paths. More precisely, define the "cost" of a cell as the difference
   *    between the actual shortest distance and Manhattan distance to it. The cost is the same for
   *    all cells with the same (type, (i % n), (j % m)).
   * 4. So by computing the distances for one grid of each type, we can instead answer a different
   *    question. For each (type, (i % n), (j % m)), how many such cells are within the (manhattan
   *    distance + cost(...)) of the starting cell (with the correct parity)?
   */
  fun part2(input: List<String>, targetDistance: Long): Long {
    val n = input.size
    val m = input[0].length
    val start = input.find('S')
    val distances = mutableMapOf(start.first to mutableMapOf(start.second to 0))
    val queue = LinkedList(listOf(start))
    val maxStepsToTry = 3 * n + 50 // Enough for (3 x 3) supergrid (one of each type).
    while (queue.isNotEmpty()) {
      val cur = queue.poll()
      val curDist = distances[cur.first]!![cur.second]!!
      if (curDist > maxStepsToTry) {
        break
      }
      for (dir in Direction.entries) {
        val nxt = dir.go(cur)
        if (
          input.getWrapped(nxt.first, nxt.second, n, m) != '#' &&
            distances.getOrMaxValue(nxt.first, nxt.second) > curDist + 1
        ) {
          if (nxt.first !in distances) {
            distances += nxt.first to mutableMapOf()
          }
          distances[nxt.first]!![nxt.second] = curDist + 1
          queue += nxt
        }
      }
    }

    data class CellType(val i: Int, val j: Int, val i_sign: Int, val j_sign: Int)

    val costs = mutableMapOf<CellType, Int>()
    for ((i, rowDistances) in distances) {
      for ((j, distance) in rowDistances) {
        val orig_i = ((i % n) + n) % n
        val orig_j = ((j % m) + m) % m
        if (input[orig_i][orig_j] == '#') {
          continue
        }

        val cellType = CellType(orig_i, orig_j, (i - orig_i).sign, (j - orig_j).sign)
        val manhattanDistance = abs(i - start.first) + abs(j - start.second)
        costs[cellType] = (distance - manhattanDistance)
      }
    }

    var ans = 0L
    for ((cellType, cost) in costs) {
      val (i, j, i_sign, j_sign) = cellType
      val (si, sj) = start
      val RHS = targetDistance - cost
      val targetParity = (RHS + si + sj + i + j) % 2

      /*
       * Count (x, y) such that:
       * - (x == i) mod n
       * - (y == j) mod m
       * - sign(x - i) = i_sign
       * - sign(y - j) = j_sign
       * - |x - si| + |y - sj| <= (targetDistance - cost) = RHS
       * - (|x - si| + |y - sj| == RHS (mod 2)
       *     => x + y == RHS + si + sj (mod 2)
       *
       * Set x = i + pn; y = j + qm; then the problem becomes:
       * - sign(p) = i_sign
       * - sign(q) = j_sign
       * - |i + pn - si| + |j + qn - sj| <= RHS [using n = m]
       * - p + q == RHS + si + sj + i + j (mod 2) [using n == 1 (mod 2)]
       */
      ans +=
        when (i_sign) {
          0 -> {
            // p = 0
            when (j_sign) {
              0 -> {
                // q = 0
                if (targetParity == 0L && abs(i - si) + abs(j - sj) <= RHS) 1 else 0
              }
              1 -> {
                // q > 0
                // abs(i - si) + j + qn - sj <= RHS
                // q <= (RHS - abs(i - si) - j + sj) / n
                countInRangeWithParity(lo = 1, hi = (RHS - abs(i - si) - j + sj) / n, targetParity)
              }
              else -> {
                // q < 0
                // abs(i - si) - j - qn + sj <= RHS
                // q >= (abs(i - si) - j + sj - RHS) / n
                countInRangeWithParity(
                  lo = (abs(i - si) - j + sj - RHS) / n,
                  hi = -1L,
                  targetParity
                )
              }
            }
          }
          1 -> {
            // p > 0
            // i + pn - si + abs(j + qn - sj) <= RHS
            when (j_sign) {
              0 -> {
                // q = 0
                // p <= (RHS - i + si - abs(j - sj)) / n
                countInRangeWithParity(lo = 1, hi = (RHS - i + si - abs(j - sj)) / n, targetParity)
              }
              1 -> {
                // q > 0
                // i + pn - si + j + qn - sj <= RHS
                // (p + q) <= (RHS - i + si - j + sj) / n
                countSumInRangeWithParity(hi = (RHS - i + si - j + sj) / n, targetParity)
              }
              else -> {
                // q < 0
                // i + pn - si - j - qn + sj <= RHS
                // (p - q) <= (RHS - i + si + j - sj) / n
                countSumInRangeWithParity(hi = (RHS - i + si + j - sj) / n, targetParity)
              }
            }
          }
          else -> {
            // p < 0
            // si - i - pn + abs(j + qn - sj) <= RHS
            when (j_sign) {
              0 -> {
                // q = 0
                // p >= (si - i - RHS + abs(j - sj)) / n
                countInRangeWithParity(lo = (si - i - RHS + abs(j - sj)) / n, hi = -1, targetParity)
              }
              1 -> {
                // q > 0
                // si - i - pn + j + qn - sj <= RHS
                // (q - p) <= (RHS - si + i - j + sj) / n
                countSumInRangeWithParity(hi = (RHS - si + i - j + sj) / n, targetParity)
              }
              else -> {
                // q < 0
                // si - i - pn - j - qn + sj <= RHS
                // -(p + q) <= (RHS - si + i + j - sj) / n
                countSumInRangeWithParity(hi = (RHS - si + i + j - sj) / n, targetParity)
              }
            }
          }
        }
    }

    return ans
  }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample"), targetSteps = 6).println()
  part1(readInput("part1_input"), targetSteps = 64).println()

  part2(readInput("part2_sample"), targetDistance = 6).println()
  part2(readInput("part2_input"), targetDistance = 26501365).println()
}
