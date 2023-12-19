import kotlin.math.max
import kotlin.math.min

fun main() {

  data class Rule(val cat: Char, val op: Char, val rval: Int, val dest: String)

  data class Workflow(val id: String, val rules: List<Rule>)

  fun String.toWorkflow() =
    Workflow(
      id = substringBefore('{'),
      rules =
        substringAfter('{').substringBefore('}').split(',').map { ruleString ->
          if (ruleString.any { it in "<>" }) {
            Rule(
              cat = ruleString[0],
              op = ruleString[1],
              rval = ruleString.substring(2).substringBefore(':').toInt(),
              dest = ruleString.substringAfter(':'),
            )
          } else {
            Rule(cat = 'x', op = '>', rval = 0, dest = ruleString)
          }
        }
    )

  data class Part(val ratings: Map<Char, Int>)

  fun String.toPart() =
    Part(
      ratings =
        filterNot { it in "{}" }.split(',').associate { it[0] to it.substringAfter('=').toInt() }
    )

  fun Rule.matches(part: Part): Boolean {
    val lval = part.ratings[cat]!!
    return when (op) {
      '<' -> (lval < rval)
      '>' -> (lval > rval)
      else -> throw Exception("Illegal operator $op")
    }
  }

  fun Workflow.process(part: Part): String = rules.first { it.matches(part) }.dest

  fun Map<String, Workflow>.processPart(part: Part): Boolean {
    val seen = mutableSetOf<String>()
    var cur = "in"
    while (true) {
      if (cur in seen) {
        return false
      }
      seen += cur
      cur = this[cur]!!.process(part)
      if (cur == "A") {
        return true
      }
      if (cur == "R") {
        return false
      }
    }
  }

  fun part1(input: List<String>): Int {
    val emptyLineIdx = input.indexOf("")
    val workflows = input.subList(0, emptyLineIdx).map { it.toWorkflow() }.associateBy { it.id }
    val parts = input.subList(emptyLineIdx + 1, input.size).map { it.toPart() }
    return parts.filter { part -> workflows.processPart(part) }.sumOf { it.ratings.values.sum() }
  }

  data class PartRange(val ratingRange: Map<Char, IntRange>)

  fun PartRange.updateRatingRange(newRange: Pair<Char, IntRange>) =
    copy(ratingRange = this.ratingRange.plus(newRange))

  fun countAccepted(cur: String, workflows: Map<String, Workflow>, partRange: PartRange): Long {
    if (cur == "R") {
      return 0
    }
    if (cur == "A") {
      return partRange.ratingRange.values.fold(1L) { cnt, range -> cnt * range.count() }
    }
    for (rule in workflows[cur]!!.rules) {
      val range = partRange.ratingRange[rule.cat]!!
      val (matched, notMatched) =
        when (rule.op) {
          '>' ->
            Pair(
              IntRange(max(range.first, rule.rval + 1), range.last),
              IntRange(range.first, min(range.last, rule.rval))
            )
          '<' ->
            Pair(
              IntRange(range.first, min(range.last, rule.rval - 1)),
              IntRange(max(range.first, rule.rval), range.last)
            )
          else -> throw Exception("Invalid operator ${rule.op}")
        }
      if (matched.isEmpty()) {
        continue
      }
      if (matched == range) {
        return countAccepted(rule.dest, workflows, partRange)
      }
      return countAccepted(rule.dest, workflows, partRange.updateRatingRange(rule.cat to matched)) +
        countAccepted(cur, workflows, partRange.updateRatingRange(rule.cat to notMatched))
    }
    throw Exception("No rule in $cur matched $partRange")
  }

  fun part2(input: List<String>): Long {
    val emptyLineIdx = input.indexOf("")
    val workflows = input.subList(0, emptyLineIdx).map { it.toWorkflow() }.associateBy { it.id }
    return countAccepted(
      "in",
      workflows,
      PartRange(ratingRange = "xmas".associateWith { IntRange(1, 4000) }),
    )
  }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
