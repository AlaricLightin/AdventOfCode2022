import java.io.PrintWriter
import java.lang.StringBuilder

// This solution works only on my input fold type
fun main() {
    fun part1(input: List<String>): Int {
        val map: Array<Array<Cell>> = readMap(input)
        val instructions = input.last()
        val board = Board(map, true)
        return board.execute(instructions)
    }

    fun part2(input: List<String>): Int {
        val map: Array<Array<Cell>> = readMap(input)
        val instructions = input.last()
        val board = Board(map, false)
        return board.execute(instructions)
    }

    val testInput = readInput("Day22_test")
    check(part1(testInput) == 6032)
    //check(part2(testInput) == 301L)
    check(part2(readInput("Day22_test2")) == 4039)

    val input = readInput("Day22")
    println(part1(input))
    println(part2(input))
}

private enum class Cell { EMPTY, OPEN, WALL }

private data class Position(val row: Int, val column: Int, val direction: Int)

private fun readMap(input: List<String>): Array<Array<Cell>> {
    val maxWidth = input.maxOf { it.length }
    return Array(input.size - 2) {
        Array(maxWidth) { idx ->
            if (idx <= input[it].lastIndex)
                when (input[it][idx]) {
                    ' ' -> Cell.EMPTY
                    '.' -> Cell.OPEN
                    '#' -> Cell.WALL
                    else -> throw IllegalArgumentException()
                }
            else Cell.EMPTY
        }
    }
}

private class Board(
    val map: Array<Array<Cell>>,
    val isPart1: Boolean
) {
    private val edgeSize = map.size / 4
    private val movesArray: Array<Pair<Int, Int>> = arrayOf(
        Pair(0, 1), Pair(1, 0), Pair(0, -1), Pair(-1, 0)
    )
    val getNextPositionFunction: (Position, Pair<Int, Int>) -> Position? = if (isPart1)
        this::getNextPosition
    else
        this::getNextPosition2

    private val savedMap: Array<StringBuilder> = Array(map.size) {
        val result = StringBuilder()
        for (cell in map[it]) {
            val c: Char = when (cell) {
                Cell.EMPTY -> ' '
                Cell.OPEN -> '.'
                Cell.WALL -> '#'
            }
            result.append(c)
        }
        result
    }

    fun execute(instructions: String): Int {
        var position = getStartPosition()

        var stepCount = 0
        var iteration = 0
        for (c in instructions) {
            if (c == 'R' || c == 'L') {
                if (stepCount > 0) {
                    position = getNextMove(position, stepCount)
                    stepCount = 0
                }

                position = rotate(position, c)

                iteration++
                //if (iteration % 100 == 0)
                // savePathToFile(iteration)
            } else
                stepCount = stepCount * 10 + c.digitToInt()
        }

        if (stepCount > 0) {
            position = getNextMove(position, stepCount)
        }

        return 1000 * (position.row + 1) + 4 * (position.column + 1) + position.direction
    }

    private fun rotate(position: Position, c: Char): Position {
        var direction = position.direction
        if (c == 'R')
            direction++
        else
            direction--

        if (direction == 4)
            direction = 0
        else if (direction == -1)
            direction = 3

        return Position(position.row, position.column, direction)
    }

    private fun getStartPosition(): Position {
        map[0].forEachIndexed { index, cell ->
            if (cell == Cell.OPEN) return Position(0, index, 0)
        }

        throw IllegalArgumentException()
    }

    private fun getNextMove(
        position: Position,
        count: Int
    ): Position {
        var current = position
        repeat(count) {
            val move = movesArray[current.direction]
            val next: Position? = getNextPositionFunction(current, move)
            if (next != null)
                current = next
            else
                return current
        }
        return current
    }

    fun getNextPosition(position: Position, move: Pair<Int, Int>): Position? {
        var newRow = position.row
        var newColumn = position.column
        while (true) {
            newRow += move.first
            newColumn += move.second

            if (newRow < 0)
                newRow = map.lastIndex
            else if (newRow == map.size)
                newRow = 0

            if (newColumn < 0)
                newColumn = map[newRow].lastIndex
            else if (newColumn == map[newRow].size)
                newColumn = 0

            when (map[newRow][newColumn]) {
                Cell.EMPTY -> {}
                Cell.WALL -> return null
                Cell.OPEN -> return Position(newRow, newColumn, position.direction)
            }
        }
    }

    fun getNextPosition2(position: Position, move: Pair<Int, Int>): Position? {
        val newPosition0 = Position(
            position.row + move.first,
            position.column + move.second,
            position.direction
        )

        val newPosition = moveThroughEdge(newPosition0)
        return if (map[newPosition.row][newPosition.column] == Cell.OPEN) {
            addToSavedMap(newPosition)
            newPosition
        } else
            null
    }

    private fun moveThroughEdge(position: Position): Position {
        return when {
            // 1 -> 6
            position.row < 0 && position.column in edgeSize until 2 * edgeSize ->
                Position(2 * edgeSize + position.column, 0, 0)

            // 2 -> 6
            position.row < 0 && position.column >= 2 * edgeSize ->
                Position(4 * edgeSize - 1, position.column - 2 * edgeSize, 3)

            // 2 -> 5
            position.row in 0 until edgeSize && position.column == 3 * edgeSize ->
                Position(3 * edgeSize - 1 - position.row, 2 * edgeSize - 1, 2)

            // 2 -> 3
            position.row == edgeSize && position.column >= 2 * edgeSize &&
                    position.direction == 1 ->
                Position(position.column - edgeSize, 2 * edgeSize - 1, 2)

            // 3 -> 2
            position.row in edgeSize until 2 * edgeSize && position.column == 2 * edgeSize &&
                    position.direction == 0 ->
                Position(edgeSize - 1, edgeSize + position.row, 3)

            // 5 -> 2
            position.row in 2 * edgeSize until 3 * edgeSize && position.column == 2 * edgeSize &&
                    position.direction == 0 ->
                Position(3 * edgeSize - 1 - position.row, 3 * edgeSize - 1, 2)

            // 5 -> 6
            position.row == 3 * edgeSize && position.column in edgeSize until 2 * edgeSize &&
                    position.direction == 1 ->
                Position(2 * edgeSize + position.column, edgeSize - 1, 2)

            // 6 -> 5
            position.row >= 3 * edgeSize && position.column == edgeSize &&
                    position.direction == 0 ->
                Position(3 * edgeSize - 1, position.row - 2 * edgeSize, 3)

            // 6 -> 2
            position.row == 4 * edgeSize ->
                Position(0, position.column + 2 * edgeSize, 1)

            // 6 -> 1
            position.row >= 3 * edgeSize && position.column < 0 ->
                Position(0, position.row - 2 * edgeSize, 1)

            // 4 -> 1
            position.row in 2 * edgeSize until 3 * edgeSize && position.column < 0 ->
                Position(3 * edgeSize - 1 - position.row, edgeSize, 0)

            // 4 -> 3
            position.row == 2 * edgeSize - 1 && position.column < edgeSize
                    && position.direction == 3 ->
                Position(position.column + edgeSize, edgeSize, 0)

            // 3 -> 4
            position.row in edgeSize until 2 * edgeSize && position.column == edgeSize - 1 &&
                    position.direction == 2 ->
                Position(2 * edgeSize, position.row - edgeSize, 1)

            // 1 -> 4
            position.row < edgeSize && position.column == edgeSize - 1 &&
                    position.direction == 2 ->
                Position(3 * edgeSize - 1 - position.row, 0, 0)

            else -> position
        }
    }

    private fun addToSavedMap(position: Position) {
        val c: Char = when (position.direction) {
            0 -> '>'
            1 -> 'v'
            2 -> '<'
            3 -> 'A'
            else -> '%'
        }
        savedMap[position.row][position.column] = c
    }

    private fun savePathToFile(iteration: Int) {
        if (isPart1)
            return

        PrintWriter("logs/$iteration.txt").use {
            savedMap.forEach { sb ->
                it.appendLine(sb.toString())
            }
        }
    }
}

