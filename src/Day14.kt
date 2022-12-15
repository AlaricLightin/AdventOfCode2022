const val SAND_START_X = 500

fun main() {
    fun part1(input: List<String>): Int {
        val pathList: List<List<Coords>> = readPathList(input)
        val fieldData = getFieldDataForPart1(pathList)
        val field = Field(fieldData)
        return field.getSandUnitCount()
    }

    fun part2(input: List<String>): Int {
        val pathList: List<List<Coords>> = readPathList(input)
        val fieldData = getFieldDataForPart2(pathList)
        val field = Field(fieldData)
        return field.getSandUnitCount()
    }

    val testInput = readInput("Day14_test")
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}

private fun readPathList(input: List<String>): List<List<Coords>> {
    return input
        .map {
             it.split(" -> ")
                .map { stringPair ->
                    val list = stringPair.split(",")
                        .map { s -> s.toInt() }
                    Coords(list[0], list[1])
                }
        }
}

private data class FieldData(
    val minX: Int,
    val matrix: Array<Array<Boolean>>
)

private fun getFieldDataForPart1(pathList: List<List<Coords>>): FieldData {
    var maxY = 0
    var minX = SAND_START_X
    var maxX = SAND_START_X

    pathList.flatten()
        .forEach {
            if (it.x < minX)
                minX = it.x
            else if (it.x > maxX)
                maxX = it.x

            if (it.y > maxY)
                maxY = it.y
        }

    val matrix = Array(maxX - minX + 1) { Array(maxY + 1) { false } }
    addPathsToMatrix(pathList, matrix, minX)
    return FieldData(minX, matrix)
}

private fun getFieldDataForPart2(pathList: List<List<Coords>>): FieldData {
    var maxY = 0
    var minX = SAND_START_X
    var maxX = SAND_START_X

    pathList.flatten()
        .forEach {
            if (it.x < minX)
                minX = it.x
            else if (it.x > maxX)
                maxX = it.x

            if (it.y > maxY)
                maxY = it.y
        }

    maxY += 2
    minX = Math.min(minX, SAND_START_X - maxY)
    maxX = Math.max(maxX, SAND_START_X + maxY)

    val matrix = Array(maxX - minX + 1) { Array(maxY + 1) { false } }
    addPathsToMatrix(pathList, matrix, minX)
    matrix.forEach {
        it[it.lastIndex] = true
    }
    return FieldData(minX, matrix)
}

private fun addPathsToMatrix(
    pathList: List<List<Coords>>,
    matrix: Array<Array<Boolean>>,
    minX: Int
) {
    pathList.forEach {
        for (i in 1 .. it.lastIndex) {
            val startCoords = it[i - 1]
            val endCoords = it[i]

            if (startCoords.x == endCoords.x) {
                val x = startCoords.x - minX
                for (j in Math.min(startCoords.y, endCoords.y) .. Math.max(startCoords.y, endCoords.y))
                    matrix[x][j] = true
            }
            else {
                val x1 = startCoords.x - minX
                val x2 = endCoords.x - minX
                for (j in Math.min(x1, x2) .. Math.max(x1, x2))
                    matrix[j][startCoords.y] = true
            }
        }
    }
}

private class Field(fieldData: FieldData) {
    private var maxY = fieldData.matrix[0].lastIndex
    private var minX = fieldData.minX
    private val matrix: Array<Array<Boolean>> = fieldData.matrix
    private val lastColumn = fieldData.matrix.lastIndex

    fun getSandUnitCount(): Int {
        var result = 0
        val startCoords = Coords(SAND_START_X - minX, 0)
        while (true) {
            val coords = getSandFinishCoords(startCoords)
            if (coords == startCoords) {
                result++
                break
            }

            if (coords.y > maxY)
                break
            if (coords.x < 0 || coords.x > lastColumn)
                break

            matrix[coords.x][coords.y] = true
            result++
        }
        return result
    }

    val moveXArray = arrayOf(0, -1, 1)
    private fun getSandFinishCoords(startCoords: Coords): Coords {
        var current = startCoords
        while (true) {
            var next: Coords? = null
            for (move in moveXArray) {
                val nextX = current.x + move
                val nextY = current.y + 1
                if (nextX in 0 .. lastColumn && nextY <= maxY) {
                    if (!matrix[nextX][nextY]) {
                        next = Coords(nextX, nextY)
                        break
                    }
                }
                else {
                    return Coords(nextX, nextY)
                }
            }

            if (next == null)
                return current
            else {
                current = next
            }
        }
    }
}
