fun main() {
    fun part1(inputFilename: String): Int {
        var result = 0
        var currentSum = 0
        getInputFile(inputFilename).forEachLine {
            if (it.isNotBlank()) {
                currentSum += it.toInt()
            }
            else {
                if (currentSum > result)
                    result = currentSum

                currentSum = 0
            }
        }

        if (currentSum > result)
            result = currentSum

        return result
    }

    fun part2(inputFilename: String): Int {
        val sumList = mutableListOf<Int>()
        var currentSum = 0
        getInputFile(inputFilename).forEachLine {
            if (it.isNotBlank()) {
                currentSum += it.toInt()
            }
            else {
                sumList.add(currentSum)
                currentSum = 0
            }
        }
        sumList.add(currentSum)

        return sumList
            .sortedDescending()
            .take(3)
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    check(part1("Day01_test") == 24000)
    check(part2("Day01_test") == 45000)

    println(part1("Day01"))
    println(part2("Day01"))
}
