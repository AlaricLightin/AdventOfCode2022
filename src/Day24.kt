fun main() {
    fun part1(input: List<String>): Int {
        val simulation = ValleySimulation(input)
        return simulation.simulate(simulation.mapStartPoint, simulation.mapEndPoint)
    }

    fun part2(input: List<String>): Int {
        val simulation = ValleySimulation(input)
        return simulation.simulate(simulation.mapStartPoint, simulation.mapEndPoint) +
                simulation.simulate(simulation.mapEndPoint, simulation.mapStartPoint) +
                simulation.simulate(simulation.mapStartPoint, simulation.mapEndPoint)
    }

    val testInput = readInput("Day24_test")
    check(part1(testInput) == 18)
    check(part2(testInput) == 54)

    val input = readInput("Day24")
    println(part1(input))
    println(part2(input))
}

private class ValleySimulation(
    input: List<String>
) {
    private val valleyHeight = input.size - 2
    private val valleyWidth = input[0].length - 2

    val mapStartPoint: Coords = getStartPoint(input)
    val mapEndPoint: Coords = getEndPoint(input)

    private var pathStartPoint = mapStartPoint
    private var pathEndPoint = mapEndPoint

    private val blizzards: Array<Blizzard> = getBlizzards(input)
    private val blizzardPositions: Array<Coords> = Array(blizzards.size) {
        blizzards[it].start
    }

    fun simulate(pathStartPoint: Coords, pathEndPoint: Coords): Int {
        this.pathStartPoint = pathStartPoint
        this.pathEndPoint = pathEndPoint
        var step = 0
        var positionSet: Set<Coords> = setOf(pathStartPoint)
        do {
            step++
            updateBlizzardPositions()
            positionSet = getNextPositionSet(positionSet)
            println("Step = $step VariantsCount = ${positionSet.size}")
        } while (!positionSet.contains(pathEndPoint))
        return step
    }

    private fun updateBlizzardPositions() {
        blizzards.forEachIndexed { index, blizzard ->
            var x = blizzardPositions[index].x + blizzard.move.x
            var y = blizzardPositions[index].y + blizzard.move.y
            if (x < 1)
                x = valleyWidth
            else if (x > valleyWidth)
                x = 1

            if (y < 1)
                y = valleyHeight
            else if (y > valleyHeight)
                y = 1

            blizzardPositions[index] = Coords(x, y)
        }
    }

    private fun getNextPositionSet(positionSet: Set<Coords>): Set<Coords> {
        val potentialPositions = getPotentialPositions(positionSet)
        val blizzardPositionSet = blizzardPositions.toSet()
        return potentialPositions.minus(blizzardPositionSet)
    }

    private fun getPotentialPositions(positionSet: Set<Coords>): Set<Coords> {
        val resultSet = mutableSetOf<Coords>()
        positionSet.forEach { coords ->
            MOVE_LIST.forEach {
                val x = coords.x + it.x
                val y = coords.y + it.y
                if (x == pathEndPoint.x && y == pathEndPoint.y) {
                    resultSet.clear()
                    resultSet.add(pathEndPoint)
                    return resultSet
                }

                if ((x in 1..valleyWidth && y in 1..valleyHeight) ||
                    (y == pathStartPoint.y && x == pathStartPoint.x))
                    resultSet.add(Coords(x, y))
            }
        }
        return resultSet
    }

    private fun getBlizzards(input: List<String>): Array<Blizzard> {
        val list = mutableListOf<Blizzard>()
        input.forEachIndexed { rowIndex, s ->
            s.forEachIndexed { columnIndex, c ->
                val move: Move? = when (c) {
                    '^' -> Move(0, -1)
                    '>' -> Move(1, 0)
                    '<' -> Move(-1, 0)
                    'v' -> Move(0, 1)
                    else -> null
                }

                if (move != null)
                    list.add(Blizzard(Coords(columnIndex, rowIndex), move))
            }
        }
        return list.toTypedArray()
    }

    private fun getEndPoint(input: List<String>): Coords {
        input.last().forEachIndexed { index, c ->
            if (c == '.')
                return Coords(index, valleyHeight + 1)
        }
        throw IllegalArgumentException()
    }

    private fun getStartPoint(input: List<String>): Coords {
        input[0].forEachIndexed { index, c ->
            if (c == '.')
                return Coords(index, 0)
        }
        throw IllegalArgumentException()
    }
}

private data class Blizzard(val start: Coords, val move: Move)

private val MOVE_LIST = arrayOf(
    Move(0, -1),
    Move(1, 0),
    Move(0, 1),
    Move(-1, 0),
    Move(0, 0)
)