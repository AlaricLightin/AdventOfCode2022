import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Int {
        val coordsList = getCoordsList(input)
        return getSidesCount(coordsList)
    }

    fun part2(input: List<String>): Int {
        val coordsList = getCoordsList(input)
        val minList: Array<Int> = Array(3) { Int.MAX_VALUE }
        val maxList: Array<Int> = Array(3) { Int.MIN_VALUE }
        coordsList.forEach { coords ->
            coords.forEachIndexed { index, i ->
                minList[index] = min(minList[index], i)
                maxList[index] = max(maxList[index], i)
            }
        }

        val sizes: Array<Int> = Array(3) { maxList[it] - minList[it] + 2 }
        val space: Array<Array<Array<CubeType>>> = Array(sizes[0]) {
            Array(sizes[1]) {
                Array(sizes[2]) { CubeType.EMPTY }
            }
        }

        coordsList.forEach {
            space[it[0] - minList[0] + 1][it[1] - minList[1] + 1][it[2] - minList[2] + 1] = CubeType.BLOCK
        }

        var processedList: MutableList<Array<Int>> = mutableListOf()
        processedList.add(Array(3) { 0 })
        space[0][0][0] = CubeType.EXTERNAL
        while (processedList.size > 0) {
            val newList: MutableList<Array<Int>> = mutableListOf()
            processedList.forEach { current ->
                NEAREST_CUBES.forEach { delta ->
                    val coords: Array<Int> = Array(3) { current[it] + delta[it] }
                    if (coords[0] in 0 until sizes[0]
                        && coords[1] in 0 until sizes[1]
                        && coords[2] in 0 until sizes[2]
                        && space[coords[0]][coords[1]][coords[2]] == CubeType.EMPTY
                    ) {
                        newList.add(coords)
                        space[coords[0]][coords[1]][coords[2]] = CubeType.EXTERNAL
                    }
                }
            }

            processedList = newList
        }

        val innerCubesList: MutableList<List<Int>> = mutableListOf()
        space.forEachIndexed { index0, arrayOfArrays ->
            arrayOfArrays.forEachIndexed { index1, array ->
                array.forEachIndexed { index2, cubeType ->
                    if (cubeType == CubeType.EMPTY)
                        innerCubesList.add(
                            listOf(
                                index0 + minList[0] - 1,
                                index1 + minList[1] - 1,
                                index2 + minList[2] - 1
                            )
                        )
                }
            }
        }

        return getSidesCount(coordsList) - getSidesCount(innerCubesList)
    }

    val testInput = readInput("Day18_test")
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}

private fun getCoordsList(input: List<String>): List<List<Int>> {
    return input.map {
        it.split(',').map(String::toInt)
    }
}

private fun getSidesCount(coordsList: List<List<Int>>): Int {
    val sides: Array<MutableSet<List<Int>>> = Array(3) { mutableSetOf() }
    coordsList.forEach {
        it.forEachIndexed { index, i ->
            if (sides[index].contains(it))
                sides[index].remove(it)
            else
                sides[index].add(it)

            val secondCoords = ArrayList(it)
            secondCoords[index] = i + 1
            if (sides[index].contains(secondCoords))
                sides[index].remove(secondCoords)
            else
                sides[index].add(secondCoords)
        }
    }
    return sides.sumOf { it.size }
}

private enum class CubeType { EMPTY, EXTERNAL, BLOCK }

private val NEAREST_CUBES: List<Array<Int>> = listOf(
    arrayOf(1, 0, 0),
    arrayOf(-1, 0, 0),
    arrayOf(0, 1, 0),
    arrayOf(0, -1, 0),
    arrayOf(0, 0, 1),
    arrayOf(0, 0, -1)
)