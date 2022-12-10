fun main() {
    fun part1(inputFilename: String): Int {
        var result = 0
        var cycleCounter = 0
        var x = 1
        getInputFile(inputFilename).forEachLine {
            if (cycleCounter > 220)
                return@forEachLine

            if(it.startsWith("addx")) {
                repeat(2) {
                    cycleCounter++
                    if (cycleCounter % 40 == 20)
                        result += x * cycleCounter
                }

                x += it.substring(5).toInt()
            }
            else {
                cycleCounter++
                if (cycleCounter % 40 == 20)
                    result += x * cycleCounter
            }
        }
        return result
    }

    fun part2(inputFilename: String) {
        var result = 0
        var cycleCounter = 0
        var x = 1

        fun executeCycle() {
            val xPos = cycleCounter % 40
            if (xPos == 0)
                println()
            if (xPos in x - 1..x + 1)
                print('#')
            else
                print('.')

            cycleCounter++
            if (cycleCounter % 40 == 20)
                result += x * cycleCounter
        }
        getInputFile(inputFilename).forEachLine {
            if (cycleCounter > 240)
                return@forEachLine

            if(it.startsWith("addx")) {
                repeat(2) { executeCycle() }

                x += it.substring(5).toInt()
            }
            else
                executeCycle()
        }
    }

    check(part1("Day10_test") == 13140)

    println(part1("Day10"))
    part2("Day10")
}