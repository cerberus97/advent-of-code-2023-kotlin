import Direction.DOWN
import Direction.LEFT
import Direction.RIGHT
import Direction.UP

private enum class Direction(val delta: Pair<Int, Int>) {
  LEFT(Pair(0, -1)),
  RIGHT(Pair(0, 1)),
  UP(Pair(-1, 0)),
  DOWN(Pair(1, 0)),
}

fun main() {
  val pipeDirs =
    mapOf(
      '|' to setOf(UP, DOWN),
      '-' to setOf(LEFT, RIGHT),
      'L' to setOf(RIGHT, UP),
      'J' to setOf(LEFT, UP),
      '7' to setOf(LEFT, DOWN),
      'F' to setOf(RIGHT, DOWN),
      '.' to emptySet(),
    )

  fun Direction.reverse() =
    when (this) {
      LEFT -> RIGHT
      RIGHT -> LEFT
      UP -> DOWN
      DOWN -> UP
    }

  fun go(i: Int, j: Int, dir: Direction) = Pair(i + dir.delta.first, j + dir.delta.second)

  fun List<String>.find(c: Char): Pair<Int, Int> {
    for (i in indices) {
      val j = this[i].indexOf(c)
      if (j != -1) return Pair(i, j)
    }
    return Pair(-1, -1)
  }

  fun List<String>.findCycle(sPos: Pair<Int, Int>, sdir: Direction): List<Pair<Int, Int>> {
    val path = mutableListOf(sPos)
    val seen = mutableSetOf(sPos)
    var curPos = sPos
    var dir = sdir
    while (true) {
      curPos = go(curPos.first, curPos.second, dir)
      if (curPos in seen) {
        return if (curPos == sPos) path else emptyList()
      } else {
        seen += curPos
        path += curPos
      }

      val curPipe = this.getOrNull(curPos.first)?.getOrNull(curPos.second) ?: return emptyList()
      val curPipeDirs = pipeDirs[curPipe]!!
      if (dir.reverse() !in curPipeDirs) {
        return emptyList()
      }
      dir = curPipeDirs.single { it != dir.reverse() }
    }
  }

  fun part1(input: List<String>): Int {
    val startPos = input.find('S')
    for (direction in Direction.entries) {
      val cycle = input.findCycle(startPos, direction)
      if (cycle.isNotEmpty()) {
        return cycle.size / 2
      }
    }
    return -1
  }

  fun Char.isLPipe() =
    (this in pipeDirs) && pipeDirs[this]!!.first().reverse() != pipeDirs[this]!!.last()

  fun getDir(p1: Pair<Int, Int>, p2: Pair<Int, Int>): Direction {
    for (d in Direction.entries) {
      if (go(p1.first, p1.second, d) == p2) return d
    }
    throw Exception("no direction")
  }

  fun part2(input: List<String>): Int {
    val startPos = input.find('S')
    var cycle = emptyList<Pair<Int, Int>>()
    for (direction in Direction.entries) {
      cycle = input.findCycle(startPos, direction)
      if (cycle.isNotEmpty()) {
        break
      }
    }
    val startDirs = setOf(getDir(cycle[0], cycle[1]), getDir(cycle[0], cycle.last()))
    val startPipe = pipeDirs.filter { it.value == startDirs }.keys.single()
    var area = 0
    for (i in input.indices) {
      var inside = 0
      var horStart = '0'
      for (j in input[i].indices) {
        if (Pair(i, j) in cycle) {
          val pipe = if (input[i][j] == 'S') startPipe else input[i][j]
          if (pipe == '|') {
            inside = 1 - inside
          } else if (pipe.isLPipe()) {
            if (horStart == '0') {
              horStart = pipe
            } else {
              if (pipeDirs[pipe]!!.intersect(pipeDirs[horStart]!!).isEmpty()) {
                inside = 1 - inside
              }
              horStart = '0'
            }
          }
        } else {
          area += inside
        }
      }
    }
    return area
  }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
