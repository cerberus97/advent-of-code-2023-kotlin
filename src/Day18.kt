import Direction.DOWN
import Direction.LEFT
import Direction.RIGHT
import Direction.UP
import java.util.*
import kotlin.math.abs

fun main() {
  fun String.toDirection() =
    when (this) {
      "R" -> RIGHT
      "L" -> LEFT
      "U" -> UP
      "D" -> DOWN
      else -> throw Exception("Unknown direction ${this}")
    }

  fun Pair<Int, Int>.go(dir: Direction, times: Int = 1) =
    Pair(first + times * dir.delta.first, second + times * dir.delta.second)

  fun part1(input: List<String>): Int {
    var cur = Pair(0, 0)
    val pts = mutableSetOf(cur)
    for (step in input) {
      val splits = step.split(' ')
      val dir = splits[0].toDirection()
      repeat(splits[1].toInt()) {
        cur = cur.go(dir)
        pts += cur
      }
    }
    val minR = pts.minOf { it.first } - 1
    val maxR = pts.maxOf { it.first } + 1
    val minC = pts.minOf { it.second } - 1
    val maxC = pts.maxOf { it.second } + 1
    val queue = LinkedList(listOf(Pair(minR, minC)))
    val seen = mutableSetOf(Pair(minR, minC))
    while (queue.isNotEmpty()) {
      cur = queue.poll()
      for (dir in Direction.entries) {
        val nxt = cur.go(dir)
        if (nxt.first in minR..maxR && nxt.second in minC..maxC && nxt !in seen && nxt !in pts) {
          queue += nxt
          seen += nxt
        }
      }
    }
    return (maxR - minR + 1) * (maxC - minC + 1) - seen.size
  }

  fun Int.toDirection() =
    when (this) {
      0 -> RIGHT
      1 -> DOWN
      2 -> LEFT
      3 -> UP
      else -> throw Exception("Unknown direction ${this}")
    }

  fun shiftDown(d1: Direction, d2: Direction) = if (d1 == RIGHT || d2 == RIGHT) 1 else 0

  fun shiftRight(d1: Direction, d2: Direction) = if (d1 == UP || d2 == UP) 1 else 0

  fun part2(input: List<String>): Long {
    var cur = Pair(0, 0)
    val instructions =
      (input + input.first())
        .reversed()
        .map { step ->
          val hexCode = step.substringAfter('#').dropLast(1)
          val times = hexCode.dropLast(1).toInt(16)
          val dir = hexCode[5].digitToInt(16).toDirection()
          Pair(dir, times)
        }
        .windowed(2)
    val coordinates = mutableListOf<Pair<Int, Int>>()
    for ((curIns, nxtIns) in instructions) {
      val (dir, times) = curIns
      val nDir = nxtIns.first
      val nxt = cur.go(dir, times)
      coordinates += Pair(nxt.first + shiftDown(dir, nDir), nxt.second + shiftRight(dir, nDir))
      cur = nxt
    }
    val area =
      (coordinates + coordinates.first()).windowed(2).sumOf { (p1, p2) ->
        p2.second.toLong() * p1.first - p2.first.toLong() * p1.second
      }
    return abs(area) / 2
  }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
