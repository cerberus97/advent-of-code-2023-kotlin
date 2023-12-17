import Direction.DOWN
import Direction.LEFT
import Direction.RIGHT
import Direction.UP
import kotlin.math.max

fun main() {
  val mirrorDirs =
    mapOf(
      '.' to
        mapOf(UP to listOf(UP), DOWN to listOf(DOWN), LEFT to listOf(LEFT), RIGHT to listOf(RIGHT)),
      '|' to
        mapOf(
          UP to listOf(UP),
          DOWN to listOf(DOWN),
          LEFT to listOf(UP, DOWN),
          RIGHT to listOf(UP, DOWN),
        ),
      '-' to
        mapOf(
          LEFT to listOf(LEFT),
          RIGHT to listOf(RIGHT),
          UP to listOf(LEFT, RIGHT),
          DOWN to listOf(LEFT, RIGHT),
        ),
      '/' to
        mapOf(LEFT to listOf(DOWN), RIGHT to listOf(UP), UP to listOf(RIGHT), DOWN to listOf(LEFT)),
      '\\' to
        mapOf(LEFT to listOf(UP), RIGHT to listOf(DOWN), UP to listOf(LEFT), DOWN to listOf(RIGHT)),
    )

  data class State(val i: Int, val j: Int, val dir: Direction)

  fun State.go(dir: Direction) = State(i + dir.delta.first, j + dir.delta.second, dir)

  fun List<String>.dfs(state: State, seen: MutableSet<State>) {
    if (state in seen || state.i !in indices || state.j !in this[0].indices) {
      return
    }
    seen += state
    for (newDir in mirrorDirs[this[state.i][state.j]]!![state.dir]!!) {
      dfs(state.go(newDir), seen)
    }
  }

  fun solve(input: List<String>, startingState: State): Int {
    val seen = mutableSetOf<State>()
    input.dfs(startingState, seen)
    return seen.distinctBy { Pair(it.i, it.j) }.size
  }

  fun part1(input: List<String>): Int = solve(input, State(0, 0, RIGHT))

  fun part2(input: List<String>): Int {
    var ans = 0
    for (i in input.indices) {
      for (j in input[i].indices) {
        if (i == 0) ans = max(ans, solve(input, State(i, j, DOWN)))
        if (i == input.size - 1) ans = max(ans, solve(input, State(i, j, UP)))
        if (j == 0) ans = max(ans, solve(input, State(i, j, RIGHT)))
        if (j == input[i].length - 1) ans = max(ans, solve(input, State(i, j, LEFT)))
      }
    }
    return ans
  }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
