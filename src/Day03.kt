fun main() {
    fun getPriority(c: Char): Int {
        return if (c in 'A'..'Z')
            c - 'A' + 27
        else
            c - 'a' + 1
    }

    fun part1(inputFilename: String): Int {
        var result = 0
        getInputFile(inputFilename).forEachLine {
            val set = mutableSetOf<Char>()
            val length = it.length
            val half = it.length / 2
            for(i in 0 until half)
                set.add(it[i])

            for(i in half until length) {
                if (set.contains(it[i])) {
                    result += getPriority(it[i])
                    break
                }
            }
        }

        return result
    }

    fun part2(inputFilename: String): Int {
        var result = 0
        val first = mutableSetOf<Char>()
        val firstAndSecond = mutableSetOf<Char>()
        readInput(inputFilename).forEachIndexed { index, s ->
            when(index % 3) {
                0 -> {
                    first.clear()
                    s.forEach { first.add(it) }
                }
                1 -> {
                    firstAndSecond.clear()
                    s
                        .filter { first.contains(it) }
                        .forEach { firstAndSecond.add(it) }
                }
                2 -> {
                    s.forEach {
                        if (firstAndSecond.contains(it)) {
                            result += getPriority(it)
                            return@forEachIndexed
                        }
                    }
                }
            }
        }
        return result
    }

    check(part1("Day03_test") == 157)
    check(part2("Day03_test") == 70)

    println(part1("Day03"))
    println(part2("Day03"))
}