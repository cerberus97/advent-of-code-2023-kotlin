import MathUtils.lcm
import java.util.*

fun main() {
  abstract class Module {
    abstract val id: String
    abstract val destinations: List<String>

    abstract fun sendPulse(pulse: Boolean, from: String): Map<String, Boolean>

    abstract fun getState(): List<Boolean>
  }

  class BroadcastModule(override val id: String, override val destinations: List<String>) :
    Module() {

    var state = false

    override fun sendPulse(pulse: Boolean, from: String): Map<String, Boolean> {
      state = pulse
      return destinations.associateWith { pulse }
    }

    override fun getState() = listOf(state)
  }

  class FlipFlopModule(override val id: String, override val destinations: List<String>) :
    Module() {
    var state = false

    override fun sendPulse(pulse: Boolean, from: String): Map<String, Boolean> {
      return if (!pulse) {
        state = !state
        destinations.associateWith { state }
      } else {
        emptyMap()
      }
    }

    override fun getState() = listOf(state)
  }

  class ConjunctionModule(override val id: String, override val destinations: List<String>) :
    Module() {
    val inputMemory = mutableMapOf<String, Boolean>()
    var state = false

    override fun sendPulse(pulse: Boolean, from: String): Map<String, Boolean> {
      inputMemory[from] = pulse
      state = !inputMemory.values.all { it }
      return destinations.associateWith { !inputMemory.values.all { it } }
    }

    fun addInput(inputId: String) {
      inputMemory[inputId] = false
    }

    override fun getState() = listOf(state)
  }

  data class Pulse(val from: String, val to: String, val type: Boolean)

  fun List<String>.toModules(): Map<String, Module> {
    val modules =
      this.map { moduleString ->
          val idWithSymbol = moduleString.split(' ').first()
          val id = idWithSymbol.filterNot { it in "%&" }
          val destinations =
            moduleString.substringAfter('>').filterNot { it.isWhitespace() }.split(',')
          if (id == "broadcaster") {
            BroadcastModule(id, destinations)
          } else if (idWithSymbol[0] == '%') {
            FlipFlopModule(id, destinations)
          } else {
            ConjunctionModule(id, destinations)
          }
        }
        .associateBy { it.id }
    for ((id, module) in modules) {
      module.destinations
        .map { modules[it] }
        .filterIsInstance<ConjunctionModule>()
        .forEach { it.addInput(id) }
    }
    return modules
  }

  fun performPulseCycle(modules: Map<String, Module>): MutableMap<Pair<String, Boolean>, Long> {
    val pulseQueue = LinkedList(listOf(Pulse("", "broadcaster", false)))
    val pulses = mutableMapOf<Pair<String, Boolean>, Long>()
    while (pulseQueue.isNotEmpty()) {
      val pulse = pulseQueue.poll()
      pulses[Pair(pulse.to, pulse.type)] = 1 + (pulses[Pair(pulse.to, pulse.type)] ?: 0)
      val newPulses = modules[pulse.to]?.sendPulse(pulse.type, pulse.from) ?: emptyMap()
      pulseQueue.addAll(newPulses.map { Pulse(pulse.to, it.key, it.value) })
    }
    return pulses
  }

  fun part1(input: List<String>): Long {
    val modules = input.toModules()
    val pulseCounts = mutableMapOf(false to 0L, true to 0L)
    repeat(1000) {
      val pulses = performPulseCycle(modules)
      pulseCounts[false] = pulseCounts[false]!! + pulses.filterKeys { !it.second }.values.sum()
      pulseCounts[true] = pulseCounts[true]!! + pulses.filterKeys { it.second }.values.sum()
    }
    return pulseCounts[false]!! * pulseCounts[true]!!
  }

  fun part2(input: List<String>): Long {
    val modules = input.toModules()

    val startNodes = modules["broadcaster"]!!.destinations

    val offsetAndCycles =
      startNodes.map { start ->
        // Find the chain of flip-flops to the final conjunction
        val flipFlopChain = mutableListOf<String>()
        var cur = start
        while (true) {
          flipFlopChain += cur
          for (nxt in modules[cur]!!.destinations) {
            if (modules[nxt] is FlipFlopModule) {
              cur = nxt
            }
          }
          if (flipFlopChain.last() == cur) {
            break
          }
        }
        // Find the final conjunction of this subgraph
        val finalConjunction = modules[flipFlopChain.last()]!!.destinations.single()
        if (modules[finalConjunction] !is ConjunctionModule) {
          throw Exception("$finalConjunction is not a Conjunction")
        }
        // Initially, everything is 0. So the first time the final conjunction sees all high values
        // is when all the flip-flops which output to conjunction reach a 1 together for the first
        // time.
        val offset =
          flipFlopChain
            .withIndex()
            .filter { (_, flipFlopId) -> finalConjunction in modules[flipFlopId]!!.destinations }
            .sumOf { 1L shl it.index }
        // After that, all flip-flops which the final conjunction outputs to (except the first one
        // in the chain) will permanently stay 1. So only the remaining flip-flops will matter.
        val cycleLength =
          flipFlopChain
            .withIndex()
            .filter { (_, flipFlopId) ->
              flipFlopId == start || flipFlopId !in modules[finalConjunction]!!.destinations
            }
            .sumOf { 1L shl it.index }

        Pair(offset, cycleLength)
      }

    // Now the answer is the first time everything intersects. Since our input is a special case
    // where the offsets are all equal to the cycle lengths, this is just the LCM of the cycle
    // lengths.
    assert(offsetAndCycles.all { it.first == it.second })
    return offsetAndCycles.map { it.second }.fold(1L) { x, y -> lcm(x, y) }

    // The code in comments below was used to inspect/analyze the input and find the pattern.

    //    fun Module.getType() =
    //      this.id +
    //        "(" +
    //        when (this) {
    //          is BroadcastModule -> "BROA"
    //          is FlipFlopModule -> "FLIP"
    //          is ConjunctionModule -> "CONJ"
    //          else -> "XYZZ"
    //        } +
    //        ")"
    //
    //    val reverseMap = modules.keys.associateWith { mutableMapOf<String, Int>() }.toMutableMap()
    //    for ((id, module) in modules) {
    //      for (dest in module.destinations) {
    //        if (dest !in reverseMap) {
    //          reverseMap[dest] = mutableMapOf()
    //        }
    //        val type = module.getType()
    //        if (type !in reverseMap[dest]!!) {
    //          reverseMap[dest]!![type] = 0
    //        }
    //        reverseMap[dest]!![type] = reverseMap[dest]!![type]!! + 1
    //      }
    //    }
    //    println(reverseMap)
    //
    //    repeat(100000) {
    //      val pulses = performPulseCycle(modules)
    //      if (Pair("rx", false) in pulses) {
    //        return (it + 1).toLong()
    //      }
    //    }
  }

  // test if implementation meets criteria from the description, like:
  part1(readInput("part1_sample")).println()
  part1(readInput("part1_input")).println()

  //  part2(readInput("part2_sample")).println()
  part2(readInput("part2_input")).println()
}
