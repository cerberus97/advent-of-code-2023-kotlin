import java.math.BigInteger
import kotlin.io.path.Path
import kotlin.io.path.readLines

/** Reads lines from the given input txt file. */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/** The cleaner shorthand for printing output. */
fun Any?.println() = println(this)

/** The 4 2-D directions with the change in row, column coordinates. */
enum class Direction(val delta: Pair<Int, Int>) {
  LEFT(Pair(0, -1)),
  RIGHT(Pair(0, 1)),
  UP(Pair(-1, 0)),
  DOWN(Pair(1, 0));

  fun reverse() =
    when (this) {
      LEFT -> RIGHT
      RIGHT -> LEFT
      UP -> DOWN
      DOWN -> UP
    }
}

// Fancy math.
object MathUtils {
  // Returns (a mod b), where a could be negative.
  fun takeMod(a: Long, b: Long): Long = ((a % b) + b) % b

  // Returns (a mod b), where a could be negative.
  fun takeMod(a: BigInteger, b: BigInteger): BigInteger = ((a % b) + b) % b

  // returns (d, x, y) where d = ax + by and d = gcd(a, b)
  private fun extendedEuclid(
    inpA: BigInteger,
    inpB: BigInteger,
  ): Triple<BigInteger, BigInteger, BigInteger> {
    var a = inpA
    var b = inpB
    var xx = BigInteger.ZERO
    var y = BigInteger.ZERO
    var yy = BigInteger.ONE
    var x = BigInteger.ONE
    while (b != BigInteger.ZERO) {
      val q = a / b
      var t = b
      b = a % b
      a = t
      t = xx
      xx = x - q * xx
      x = t
      t = yy
      yy = y - q * yy
      y = t
    }
    return Triple(a, x, y)
  }

  // Chinese remainder theorem (special case): find z such that
  // z % x = a, z % y = b.  Here, z is unique modulo M = lcm(x,y).
  // Return (z,M).  On failure, M = -1.
  private fun chineseRemainderTheorem(
    x: BigInteger,
    a: BigInteger,
    y: BigInteger,
    b: BigInteger,
  ): Pair<BigInteger, BigInteger> {
    val (d, s, t) = extendedEuclid(x, y)
    if (a % d != b % d) return Pair(BigInteger.ZERO, -BigInteger.ONE)
    return Pair(takeMod((s * b * x) + (t * a * y), x * y) / d, (x * y) / d)
  }

  // Chinese remainder theorem: find z such that
  // z % x[i] = a[i] for all i.  Note that the solution is
  // unique modulo M = lcm_i (x[i]).  Return (z,M).  On
  // failure, M = -1.  Note that we do not require the a[i]'s
  // to be relatively prime.
  fun chineseRemainderTheorem(
    x: List<BigInteger>,
    a: List<BigInteger>,
  ): Pair<BigInteger, BigInteger> {
    var ans = Pair(a[0], x[0])
    for (i in 1 until x.size) {
      ans = chineseRemainderTheorem(ans.second, ans.first, x[i], a[i])
      if (ans.second == -BigInteger.ONE) return ans
    }
    return ans
  }
}
