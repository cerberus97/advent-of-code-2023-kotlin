import java.util.LinkedList

fun main() {
  data class Point(val x: Int, val y: Int, val z: Int)

  data class Brick(val p1: Point, val p2: Point)

  fun String.toPoint(): Point {
    val splits = this.split(',')
    return Point(splits[0].toInt(), splits[1].toInt(), splits[2].toInt())
  }

  fun String.toBrick(): Brick {
    val pts = this.split('~').map { it.toPoint() }.sortedBy { it.x + it.y + it.z }
    return Brick(pts[0], pts[1])
  }

  fun getSupportingBrickMap(input: List<String>): Map<Brick, Set<Brick>> {
    val bricks = input.map { it.toBrick() }.sortedBy { it.p1.z }
    val heightMap = mutableMapOf<Point, Pair<Int, Brick>>()
    val supportingBrickMap = mutableMapOf<Brick, Set<Brick>>()
    for (brick in bricks) {
      var curHeight = 0
      val supportingBricks = mutableSetOf<Brick>()
      for (x in brick.p1.x..brick.p2.x) {
        for (y in brick.p1.y..brick.p2.y) {
          if (Point(x, y, 0) in heightMap) {
            val (height, brickBelow) = heightMap[Point(x, y, 0)]!!
            if (height > curHeight) {
              curHeight = height
              supportingBricks.clear()
              supportingBricks += brickBelow
            } else if (height == curHeight) {
              supportingBricks += brickBelow
            }
          }
        }
      }
      for (x in brick.p1.x..brick.p2.x) {
        for (y in brick.p1.y..brick.p2.y) {
          heightMap[Point(x, y, 0)] = Pair(curHeight + (brick.p2.z - brick.p1.z + 1), brick)
        }
      }
      supportingBrickMap[brick] = supportingBricks
    }
    return supportingBrickMap
  }

  fun part1(input: List<String>): Int {
    val supportingBricks = getSupportingBrickMap(input)
    val safeBricks =
      supportingBricks.keys -
        supportingBricks.values.filter { it.size == 1 }.map { it.single() }.toSet()
    return safeBricks.size
  }

  fun Map<Brick, Set<Brick>>.reverse(): Map<Brick, Set<Brick>> {
    val reverseMap = this.mapValues { mutableSetOf<Brick>() }
    for ((key, values) in this) {
      for (value in values) {
        reverseMap[value]!! += key
      }
    }
    return reverseMap
  }

  fun part2(input: List<String>): Int {
    val supportingBricks = getSupportingBrickMap(input)
    val supportedBricks = supportingBricks.reverse()
    return supportingBricks.keys.sumOf { startBrick ->
      val curSupportingBricks = supportingBricks.mapValues { it.value.toMutableSet() }
      val queue = LinkedList(listOf(startBrick))
      var ans = 0
      while (queue.isNotEmpty()) {
        val curBrick = queue.poll()
        ++ans
        for (supportedBrick in supportedBricks[curBrick]!!) {
          curSupportingBricks[supportedBrick]!! -= curBrick
          if (curSupportingBricks[supportedBrick]!!.isEmpty()) {
            queue.push(supportedBrick)
          }
        }
      }
      ans - 1
    }
  }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
