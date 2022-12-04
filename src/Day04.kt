fun main() {
    fun getRanges(string: String): Pair<IntRange, IntRange> {
        val stringList = string.split(",")
        if (stringList.size < 2)
            throw IllegalArgumentException()

        val s0List = stringList[0].split("-")
        val s1List = stringList[1].split("-")
        return Pair(IntRange(s0List[0].toInt(), s0List[1].toInt()),
            IntRange(s1List[0].toInt(), s1List[1].toInt()))
    }

    fun part1(inputFilename: String): Int {
        var result = 0
        getInputFile(inputFilename).forEachLine {
            val (range1, range2) = getRanges(it)
            if ((range1.contains(range2.first) && (range1.contains(range2.last))
                        || (range2.contains(range1.first) && range2.contains(range1.last))))
                result++
        }
        return result
    }

    fun part2(inputFilename: String): Int {
        var result = 0
        getInputFile(inputFilename).forEachLine {
            val (range1, range2) = getRanges(it)
            val intersect = range1.intersect(range2)
            if (intersect.isNotEmpty())
                result++
        }
        return result
    }


    check(part1("Day04_test") == 2)
    check(part2("Day04_test") == 4)

    println(part1("Day04"))
    println(part2("Day04"))
}