fun main() {
    fun part1(input: List<String>): Int {
        val solution = Solution(input) { c -> c == 'S' }
        return solution.getPathLength()
    }

    fun part2(input: List<String>): Int {
        val solution = Solution(input) { c -> c == 'S' || c == 'a' }
        return solution.getPathLength()
    }

    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

private class Solution(
    val input: List<String>,
    val exitCondition: (Char) -> Boolean
) {
    var result: Int = input.size * input[0].length + 1
    val maxRow = input.lastIndex
    val maxColumn = input[0].lastIndex
    val minPathArray = Array(maxRow + 1) {
        Array(maxColumn + 1) { result }
    }

    private fun addNextCell(coords: Coords, path: Set<Coords>) {
        if (path.size > result)
            return

        if (minPathArray[coords.y][coords.x] <= path.size)
            return
        else
            minPathArray[coords.y][coords.x] = path.size

        var pointValue = input[coords.y][coords.x]
        if (exitCondition.invoke(pointValue)) {
            if (result > path.size)
                result = path.size
            return
        }

        if (pointValue == 'E')
            pointValue = 'z'

        NEXT_MOVE_LIST.forEach {
            val newCoords = getNewCoords(coords, it)
            if (newCoords.x in 0 .. maxColumn && newCoords.y in 0 .. maxRow
                && !path.contains(newCoords)
            ) {
                var newValue = input[newCoords.y][newCoords.x]
                if (newValue == 'S')
                    newValue = 'a'
                if (pointValue <= newValue + 1)
                    addNextCell(newCoords, path.plus(newCoords))
            }
        }
    }

    private fun getEndCoords(): Coords {
        input.forEachIndexed { index, s ->
            if (s.contains("E"))
                return Coords(s.indexOf('E'), index)
        }
        return Coords(0, 0)
    }

    fun getPathLength(): Int {
        val startCoords = getEndCoords()
        addNextCell(startCoords, setOf(startCoords))
        return result - 1
    }
}