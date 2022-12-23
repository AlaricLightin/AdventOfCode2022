import kotlin.math.max
import kotlin.math.min

fun main() {
    fun readElvesArray(input: List<String>): Array<Coords> {
        val resultList = mutableListOf<Coords>()
        input.forEachIndexed { rowIndex, s ->
            s.forEachIndexed { index, c ->
                if (c == '#')
                    resultList.add(Coords(index, rowIndex))
            }
        }
        return resultList.toTypedArray()
    }

    fun getMoveProposal(coords: Coords, elvesSet: MutableSet<Coords>, direction: Int): Coords {
        if (movesToAdjacent.all {
                !elvesSet.contains(getNewCoords(coords, it))
            })
            return coords

        for (i in 0..3) {
            val currentDirection = (direction + i) % 4
            val checkMove = checkMoveArray[currentDirection]
            if (checkMove.checks.all {
                    !elvesSet.contains(getNewCoords(coords, it))
                })
                return getNewCoords(coords, checkMove.move)
        }

        return coords
    }

    fun getNextMove(
        elvesArray: Array<Coords>,
        elvesSet: MutableSet<Coords>,
        direction: Int
    ): Boolean {
        val moveProposals: Array<Coords> = Array(elvesArray.size) {
            getMoveProposal(elvesArray[it], elvesSet, direction)
        }

        val proposalMap: MutableMap<Coords, List<Int>> = mutableMapOf()
        moveProposals.forEachIndexed { index, coords ->
            if (moveProposals[index] != elvesArray[index])
                proposalMap.merge(coords, listOf(index)) { list1, list2 ->
                    list1.plus(list2)
                }
        }

        proposalMap
            .filter { entry -> entry.value.size == 1 }
            .forEach { (_, indexList) ->
                indexList.forEach {
                    elvesSet.remove(elvesArray[it])
                    elvesArray[it] = moveProposals[it]
                    elvesSet.add(elvesArray[it])
                }
            }

        return proposalMap
            .filter { entry -> entry.value.size == 1 }
            .isNotEmpty()
    }

    fun part1(input: List<String>): Int {
        val elvesArray: Array<Coords> = readElvesArray(input)
        val elvesSet = elvesArray.toMutableSet()
        var direction = 0
        repeat(10) {
            getNextMove(elvesArray, elvesSet, direction)
            direction = (direction + 1) % 4
        }

        var north = Int.MAX_VALUE
        var south = Int.MIN_VALUE
        var east = Int.MIN_VALUE
        var west = Int.MAX_VALUE

        elvesArray.forEach {
            north = min(north, it.y)
            south = max(south, it.y)
            east = max(east, it.x)
            west = min(west, it.x)
        }

        return (south - north + 1) * (east - west + 1) - elvesArray.size
    }

    fun part2(input: List<String>): Int {
        val elvesArray: Array<Coords> = readElvesArray(input)
        val elvesSet = elvesArray.toMutableSet()
        var direction = 0
        var iterationCount = 0
        do {
            iterationCount++
            val wasMoved = getNextMove(elvesArray, elvesSet, direction)
            direction = (direction + 1) % 4
        } while (wasMoved)

        return iterationCount
    }

    val testInput = readInput("Day23_test")
    check(part1(testInput) == 110)
    check(part2(testInput) == 20)

    val input = readInput("Day23")
    println(part1(input))
    println(part2(input))
}

private data class DirectionCheckMove(
    val checks: List<Move>,
    val move: Move
)

private val checkMoveArray = arrayOf(
    // north
    DirectionCheckMove(
        listOf(Move(-1, -1), Move(0, -1), Move(1, -1)),
        Move(0, -1)
    ),
    // south
    DirectionCheckMove(
        listOf(Move(-1, 1), Move(0, 1), Move(1, 1)),
        Move(0, 1)
    ),
    // west
    DirectionCheckMove(
        listOf(Move(-1, -1), Move(-1, 0), Move(-1, 1)),
        Move(-1, 0)
    ),
    // east
    DirectionCheckMove(
        listOf(Move(1, -1), Move(1, 0), Move(1, 1)),
        Move(1, 0)
    )
)

private val movesToAdjacent = arrayOf(
    Move(-1, -1),
    Move(-1, 0),
    Move(-1, 1),
    Move(0, -1),
    Move(0, 1),
    Move(1, -1),
    Move(1, 0),
    Move(1, 1)
)