fun main() {
  fun String.toHashValue(): Int {
    var ans = 0
    for (c in this) {
      ans = (17 * (ans + c.code)) % 256
    }
    return ans
  }

  fun part1(input: List<String>): Int = input[0].split(',').sumOf { it.toHashValue() }

  data class Operation(val label: String, val focalLength: Int)

  fun String.toOperation(): Operation {
    val splits = split('-', '=')
    val focalLength = if (splits[1].isNotEmpty()) splits[1].toInt() else -1
    return Operation(splits[0], focalLength)
  }

  fun part2(input: List<String>): Int {
    val groups =
      input[0].split(',').map { it.toOperation() }.groupBy { op -> op.label.toHashValue() }
    return groups.entries.sumOf { group ->
      val operations = group.value
      val labelsDone = mutableSetOf<String>()
      val labelPosition = mutableMapOf<String, Int>()
      val labelFocalLength = mutableMapOf<String, Int>()
      for ((id, op) in operations.reversed().withIndex()) {
        if (op.label in labelsDone) {
          continue
        }
        if (op.focalLength == -1) {
          labelsDone += op.label
          continue
        }
        if (op.label !in labelFocalLength) {
          labelFocalLength[op.label] = op.focalLength
        }
        labelPosition[op.label] = -id
      }
      (1 + group.key) *
        labelPosition.entries
          .sortedBy { it.value }
          .withIndex()
          .sumOf { (id, labelEntry) -> (1 + id) * labelFocalLength[labelEntry.key]!! }
    }
  }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
