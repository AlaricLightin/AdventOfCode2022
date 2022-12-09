import kotlin.math.abs
import kotlin.math.sign

fun main() {
    fun solution(input: List<String>, ropeLength: Int): Int {
        val visitedSet = mutableSetOf<Coords>()
        val rope: Array<Coords> = Array(ropeLength) { Coords(0, 0) }
        visitedSet.add(rope.last())
        input.forEach {
            val headMove = MOVES[it[0]]!!
            repeat(it.substring(2).toInt()) {
                rope[0] = getNewCoords(rope[0], headMove)
                for (i in 1 .. rope.lastIndex) {
                    val ropeKnotMove = getMove(rope[i - 1], rope[i])
                    if (ropeKnotMove == Move(0, 0))
                        break
                    rope[i] = getNewCoords(rope[i], ropeKnotMove)
                }

                visitedSet.add(rope.last())
            }
        }
        return visitedSet.size
    }

    val testInput = readInput("Day09_test")
    check(solution(testInput, 2) == 13)
    check(solution(testInput, 10) == 1)

    val input = readInput("Day09")
    println(solution(input, 2))
    println(solution(input, 10))
}

private data class Coords(val x: Int, val y: Int)
private data class Move(val x: Int, val y: Int)

private val MOVES: Map<Char, Move> = mapOf(
    'R' to Move(1, 0),
    'D' to Move(0, 1),
    'U' to Move(0, -1),
    'L' to Move(-1, 0)
)

private fun getMove(headCoords: Coords, tailCoords: Coords): Move {
    return if (abs(headCoords.x - tailCoords.x) > 1
        || abs(headCoords.y - tailCoords.y) > 1
    ) Move(
        (headCoords.x - tailCoords.x).sign,
        (headCoords.y - tailCoords.y).sign
    )
    else Move(0, 0)
}

private fun getNewCoords(startCoords: Coords, move: Move): Coords {
    return Coords(
        startCoords.x + move.x,
        startCoords.y + move.y
    )
}