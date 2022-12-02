fun main() {
    fun part1(inputFilename: String): Int {
        var result = 0
        getInputFile(inputFilename).forEachLine {
            if (it.isBlank())
                return@forEachLine
            val first = it[0]
            val second = it[2]
            result += (when (second) {
                'X' -> 1
                'Y' -> 2
                else -> 3
            }
                    + when {
                first - 'A' == second - 'X' -> 3
                (first == 'A' && second == 'Y')
                        || (first == 'B' && second == 'Z')
                        || (first == 'C' && second == 'X') -> 6
                else -> 0
            })
        }

        return result
    }

    fun part2(inputFilename: String): Int {
        var result = 0
        getInputFile(inputFilename).forEachLine {
            if (it.isBlank())
                return@forEachLine
            val first = it[0]
            val second = it[2]
            result += when (second) {
                'X' -> when (first) {
                    'A' -> 3
                    'B' -> 1
                    else -> 2
                }
                'Y' -> 3 + when(first) {
                    'A' -> 1
                    'B' -> 2
                    else -> 3
                }
                else -> 6 + when(first) {
                    'A' -> 2
                    'B' -> 3
                    else -> 1
                }
            }
        }

        return result
    }

    check(part1("Day02_test") == 15)
    check(part2("Day02_test") == 12)

    println(part1("Day02"))
    println(part2("Day02"))
}