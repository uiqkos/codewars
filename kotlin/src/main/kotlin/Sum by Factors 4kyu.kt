fun sumOfDivided(l: IntArray): String {
    return (2..(l.max() ?: 0))
        .let { divisors ->
            divisors.filter { divisor1 ->
                divisors.filter {
                    it < divisor1
                }
                .all { divisor2 ->
                    divisor1 % divisor2 != 0
                }
            }
        }
        .map { divisor ->
            divisor to l.sumBy {
                if (it % divisor == 0) it
                else 0
            }
        }
        .filter { it.second != 0 }
        .joinToString("") {
            "(${it.first} ${it.second})"
        }
}

fun main() {
    println(sumOfDivided(intArrayOf(44, 52)))
}