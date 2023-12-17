import Direction.*
import java.util.*

fun main() {
  data class State(val i: Int, val j: Int, val dir: Direction, val len: Int)

  fun State.go(newDir: Direction) =
    State(
      i + newDir.delta.first,
      j + newDir.delta.second,
      newDir,
      if (dir == newDir) len + 1 else 1
    )

  fun dijkstra(input: List<List<Int>>, minStraight: Int, maxStraight: Int): Int {
    val start = listOf(State(0, 0, DOWN, 0), State(0, 0, RIGHT, 0))
    val dist = start.associateWith { 0 }.toMutableMap()
    val queue = PriorityQueue<Pair<Int, State>>(1, compareBy { it.first })
    start.forEach { queue.offer(Pair(0, it)) }
    while (queue.isNotEmpty()) {
      val cur = queue.poll()
      val curDist = cur.first
      val curState = cur.second
      for (dir in Direction.entries) {
        if (dir == curState.dir.reverse()) {
          continue
        }
        if (dir == curState.dir && curState.len == maxStraight) {
          continue
        }
        if (dir != curState.dir && curState.len < minStraight) {
          continue
        }
        val nState = curState.go(dir)
        if (nState.i !in input.indices || nState.j !in input[0].indices) {
          continue
        }
        val nDist = curDist + input[nState.i][nState.j]
        if (nState !in dist || nDist < dist[nState]!!) {
          dist[nState] = nDist
          queue.offer(Pair(nDist, nState))
        }
      }
    }
    return dist.filter { it.key.i == input.size - 1 && it.key.j == input[0].size - 1 }.values.min()
  }

  fun part1(input: List<String>): Int =
    dijkstra(input.map { s -> s.map { c -> c.digitToInt() } }, 0, 3)

  fun part2(input: List<String>): Int =
    dijkstra(input.map { s -> s.map { c -> c.digitToInt() } }, 4, 10)

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
