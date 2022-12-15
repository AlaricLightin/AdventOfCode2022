import kotlin.math.abs


fun main() {
    fun part1(coords: List<Pair<Coords, Coords>>, lineY: Int): Int {
        val rangePointList = getRangePointListForLine(coords, lineY)
        var result = 0
        var beginCount = 0
        var currentBegin: Int? = null
        rangePointList.forEach {
            when(it.pointType) {
                RangePointType.BEGIN -> {
                    if (beginCount == 0) {
                        currentBegin = it.x
                    }
                    beginCount++
                }

                RangePointType.END -> {
                    beginCount--
                    if (beginCount == 0) {
                        result += (it.x - currentBegin!! + 1)
                    }
                }

                RangePointType.BEACON -> {
                    if (beginCount > 0)
                        result--
                }
            }
        }

        return result
    }

    fun part2(coords: List<Pair<Coords, Coords>>, range: IntRange): Long {
        var resultX: Int? = null
        var resultY: Int? = null
        for (y in range) {
            val rangePointList = getRangePointListForLine(coords, y)
            var lastEndOrBeacon: Int? = null
            var beginCount = 0

            run rangePointList@{
                rangePointList.forEach {
                    if (it.x > range.last)
                        return@rangePointList

                    when (it.pointType) {
                        RangePointType.BEGIN -> {
                            if (beginCount == 0 && it.x > 0 && lastEndOrBeacon != it.x - 1) {
                                resultX = it.x - 1
                                return@rangePointList
                            }
                            beginCount++
                        }

                        RangePointType.END -> {
                            beginCount--
                            lastEndOrBeacon = it.x
                        }

                        RangePointType.BEACON -> {
                            lastEndOrBeacon = it.x
                        }
                    }
                }
            }
            if (resultX != null) {
                resultY = y
                break
            }
        }

        return 4000000L * resultX!!.toLong() + resultY!!
    }

    val testInput = readInput("Day15_test")
    val testCoords: List<Pair<Coords, Coords>> = readCoords(testInput)
    check(part1(testCoords, 10) == 26)
    check(part2(testCoords, 1..20) == 56000011L)

    val input = readInput("Day15")
    val coords = readCoords(input)
    println(part1(coords, 2000000))
    println(part2(coords, 1 .. 4000000))
}

private val REGEX = Regex("""Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""")

private fun readCoords(input: List<String>): List<Pair<Coords, Coords>> {
    val result = mutableListOf<Pair<Coords, Coords>>()
    input.forEach {
        val matchResult = REGEX.matchEntire(it) ?: return@forEach
        result.add(
            Pair(
                Coords(matchResult.groupValues[1].toInt(), matchResult.groupValues[2].toInt()),
                Coords(matchResult.groupValues[3].toInt(), matchResult.groupValues[4].toInt())
            )
        )
    }
    return result
}

private enum class RangePointType { BEGIN, BEACON, END }
private data class RangePoint(val x: Int, val pointType: RangePointType)

private fun getRangePointListForLine(
    coords: List<Pair<Coords, Coords>>,
    lineY: Int
): List<RangePoint> {
    val rangePointList = mutableListOf<RangePoint>()
    val beaconSet = mutableSetOf<Int>()
    coords.forEach {
        val distance = abs(it.first.x - it.second.x) + abs(it.first.y - it.second.y)
        val deltaY = abs(it.first.y - lineY)
        if (deltaY > distance)
            return@forEach

        val deltaX = distance - deltaY
        rangePointList.add(RangePoint(it.first.x - deltaX, RangePointType.BEGIN))
        rangePointList.add(RangePoint(it.first.x + deltaX, RangePointType.END))

        if (it.second.y == lineY) {
            beaconSet.add(it.second.x)
        }
    }

    beaconSet.forEach {
        rangePointList.add(RangePoint(it, RangePointType.BEACON))
    }

    rangePointList.sortWith(
        Comparator.comparingInt<RangePoint> { rp -> rp.x }
            .thenComparing { rp -> rp.pointType.ordinal }
    )
    return rangePointList
}