fun main() {
    check(snafuToDecimal("1=0") == 15L)
    check(snafuToDecimal("1-0") == 20L)
    check(snafuToDecimal("1=11-2") == 2022L)
    check(snafuToDecimal("1-0---0") == 12345L)
    check(snafuToDecimal("1121-1110-1=0") == 314159265L)

    check(decimalToSnafu(15) == "1=0")
    check(decimalToSnafu(20) == "1-0")
    check(decimalToSnafu(2022) == "1=11-2")
    check(decimalToSnafu(12345) == "1-0---0")
    check(decimalToSnafu(314159265) == "1121-1110-1=0")

    fun solution(input: List<String>): String {
        return decimalToSnafu(
            input.sumOf { snafuToDecimal(it) }
        )
    }

    check(solution(readInput("Day25_test")) == "2=-1=0")

    println(solution(readInput("Day25")))
}

private val snafuDigits: Map<Char, Int> = mapOf(
    '2' to 2,
    '1' to 1,
    '0' to 0,
    '-' to -1,
    '=' to -2
)

private fun snafuToDecimal(s: String): Long {
    var result = 0L
    var multiplier = 1L
    for (i in (0..s.lastIndex).reversed()) {
        result += snafuDigits[s[i]]!! * multiplier
        multiplier *= 5L
    }
    return result
}

private fun decimalToSnafu(a: Long): String {
    val resultList: MutableList<Int> = mutableListOf()
    var current: Long = a
    var addToNext = 0
    while (current > 0) {
        val normalDigit: Int = (current % 5 + addToNext).toInt()
        addToNext = if (normalDigit <= 2) {
            resultList.add(normalDigit)
            0
        } else {
            resultList.add(normalDigit - 5)
            1
        }

        current /= 5
    }

    if (addToNext > 0)
        resultList.add(1)

    return resultList.reversed().joinToString(separator = "") {
        when (it) {
            -2 -> "="
            -1 -> "-"
            in 0..2 -> it.toString()
            else -> throw IllegalArgumentException()
        }
    }
}