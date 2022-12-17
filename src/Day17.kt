import kotlin.math.max

private const val ROCK_COUNT = 2022
private const val FIELD_WIDTH = 7
private const val ITERATION_COUNT_FOR_PART2 = 1000000000000

fun main() {
    val testInput = readLine("Day17_test")
    val testSimulation = RockSimulation(testInput)
    check(testSimulation.simulate(ROCK_COUNT) == 3068)
    check(testSimulation.getPart2Result(ITERATION_COUNT_FOR_PART2) == 1514285714288)

    val input = readLine("Day17")
    val simulation = RockSimulation(input)
    println(simulation.simulate(ROCK_COUNT))
    println(simulation.getPart2Result(ITERATION_COUNT_FOR_PART2))
}

private val ROCKS: List<List<Coords>> = listOf(
    listOf(Coords(2, 0), Coords(3, 0), Coords(4, 0), Coords(5, 0)),
    listOf(Coords(3, 0), Coords(2, 1), Coords(3, 1), Coords(4, 1), Coords(3, 2)),
    listOf(Coords(2, 0), Coords(3, 0), Coords(4, 0), Coords(4, 1), Coords(4, 2)),
    listOf(Coords(2, 0), Coords(2, 1), Coords(2, 2), Coords(2, 3)),
    listOf(Coords(2, 0), Coords(3, 0), Coords(2, 1), Coords(3, 1))
)

private class RockSimulation(val windString: String) {
    private val field: Array<Array<Boolean>> = Array(ROCK_COUNT * 4) {
        Array(FIELD_WIDTH) { false }
    }
    private var maxHeight: Int = -1
    private var windCount: Int = 0

    // Data for part2
    private val rockTypeWindHeightMap = mutableMapOf<Pair<Int, Int>, Int>()
    private val rockTypeWindRockMap = mutableMapOf<Pair<Int, Int>, Int>()
    private val rockHeightArray: Array<Int> = Array(ROCK_COUNT) { 0 }
    private var lastRockDiff = 0
    private var lastHeightDiff = 0
    private var lastRockNum = 0
    private var lastMaxHeight = 0

    fun simulate(count: Int): Int {
        for (i in 0 until count) {
            simulateOneRock(i % ROCKS.size)
            val pair = Pair(windCount, i % ROCKS.size)
            val height: Int? = rockTypeWindHeightMap[pair]
            val rockNum: Int? = rockTypeWindRockMap[pair]
            if (height != null && rockNum != null) {
                lastRockDiff = i - rockNum
                lastHeightDiff = maxHeight - height
                lastRockNum = i
                lastMaxHeight = maxHeight
            }
            rockTypeWindHeightMap[pair] = maxHeight
            rockTypeWindRockMap[pair] = i
            rockHeightArray[i] = maxHeight
        }

        return maxHeight + 1
    }

    fun getPart2Result(iterationCount: Long): Long {
        val diff = iterationCount - 1L - lastRockNum
        val cyclesCount: Long = diff / lastRockDiff
        val cyclesLast = diff % lastRockDiff
        val heightLast = rockHeightArray[lastRockNum - lastRockDiff + cyclesLast.toInt()] -
                rockHeightArray[lastRockNum - lastRockDiff]
        return lastMaxHeight.toLong() + cyclesCount * lastHeightDiff + heightLast.toLong() + 1
    }

    private fun simulateOneRock(rockNumber: Int) {
        var rockPosition: List<Coords> = ROCKS[rockNumber]
            .map { Coords(it.x, it.y + maxHeight + 4) }
        var movedDown = true
        while (movedDown) {
            val windDirection: Int = getWindDirection()
            var newPosition = rockPosition
                .map { Coords(it.x + windDirection, it.y) }
            if (isPositionAfterWindCorrect(newPosition))
                rockPosition = newPosition

            newPosition = rockPosition
                .map { Coords(it.x, it.y - 1) }
            movedDown = isPositionAfterDescendCorrect(newPosition)
            if (movedDown)
                rockPosition = newPosition
            else {
                maxHeight = max(maxHeight, rockPosition.maxOf { it.y } )
                rockPosition.forEach {
                    field[it.y][it.x] = true
                }
            }
        }
    }

    private fun printField() {
        field.filter { it.any { v -> v } }
            .reversed()
            .forEach {
                print('|')
                it.forEach { v -> if (v) print('#') else print('.') }
                println('|')
            }

        println("---------")
        println()
    }

    private fun isPositionAfterWindCorrect(newPosition: List<Coords>): Boolean {
        return newPosition.all {
            it.x in 0 until FIELD_WIDTH && !field[it.y][it.x]
        }
    }

    private fun isPositionAfterDescendCorrect(newPosition: List<Coords>): Boolean {
        return newPosition.all {
            it.y >= 0 && !field[it.y][it.x]
        }
    }

    private fun getWindDirection(): Int {
        val result = if (windString[windCount] == '<') -1 else 1
        windCount++
        if (windCount == windString.length)
            windCount = 0

        return result
    }
}