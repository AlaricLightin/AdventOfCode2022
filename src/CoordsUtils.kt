data class Coords(val x: Int, val y: Int)
data class Move(val x: Int, val y: Int)

val NEXT_MOVE_LIST = arrayOf(Move(0, -1), Move(1, 0), Move(0, 1), Move(-1, 0))

fun getNewCoords(startCoords: Coords, move: Move): Coords {
    return Coords(
        startCoords.x + move.x,
        startCoords.y + move.y
    )
}