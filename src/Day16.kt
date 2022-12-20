import kotlin.math.max

private const val TIME_LIMIT_1 = 30
private const val TIME_LIMIT_2 = 26

fun main() {
    val testInput = readInput("Day16_test")
    val testData: ValvesData = createData(testInput)
    val testValvesSolution = ValvesSolution(testData)
    check(testValvesSolution.getMaxPressure() == 1651)
    check(testValvesSolution.getMaxPressure2() == 1707)

    val input = readInput("Day16")
    val data = createData(input)
    val valvesSolution = ValvesSolution(data)
    println(valvesSolution.getMaxPressure())
    println(valvesSolution.getMaxPressure2())
}

private val REGEX =
    Regex("""Valve (?<ID>[A-Z]{2}) has flow rate=(?<flowRate>\d+); (tunnels lead to valves|tunnel leads to valve)\s(?<tunnelList>.*)""")

private fun createData(testInput: List<String>): ValvesData {
    val valveNameMap = mutableMapOf<String, Int>()
    val flowRateArray: Array<Int> = Array(testInput.size) { 0 }
    val movesArrayString: Array<List<String>> = Array(testInput.size) { listOf() }

    testInput.forEachIndexed { idx, s ->
        val matchResult = REGEX.matchEntire(s) ?: return@forEachIndexed
        val valve: String = matchResult.groups["ID"]!!.value
        flowRateArray[idx] = matchResult.groups["flowRate"]!!.value.toInt()
        movesArrayString[idx] = matchResult.groups["tunnelList"]!!.value.split(", ")
        valveNameMap[valve] = idx
    }

    val movesArray: Array<List<Int>> = Array(movesArrayString.size) {
        movesArrayString[it].map { valve -> valveNameMap[valve]!! }
    }

    return ValvesData(flowRateArray, movesArray, valveNameMap.get("AA")!!)
}

private data class ValvesData(
    val flowRate: Array<Int>,
    val moves: Array<List<Int>>,
    val start: Int
)

private data class ValvesState(
    val current: Int,
    val opened: Long
)

private class ValvesSolution(val valvesData: ValvesData) {
    private val timeMap = mutableMapOf<ValvesState, Int>()
    private var result = 0
    private val maxOpened: Long = valvesData.flowRate.foldIndexed(0) {
        idx, acc, v -> if (v > 0) acc or (1L shl idx) else acc
    }
    private val pressureForMinuteMap = mutableMapOf<Long, Int>()
    private val pressureForPart2Map = mutableMapOf<Long, Int>()

    private fun getPressureForState(opened: Long): Int {
        return pressureForMinuteMap.computeIfAbsent(opened) { key ->
            valvesData.flowRate
                .filterIndexed { idx, _ -> key and (1L shl idx) > 0 }
                .sum()
        }
    }

    fun getMaxPressure(): Int {
        val startState = ValvesState(valvesData.start, 0)
        calculatePressure(startState, 0, 1)
        return result
    }

    fun getMaxPressure2(): Int {
        val list: List<Pair<Long, Int>> = pressureForPart2Map
            .map { entry -> Pair(entry.key, entry.value) }

        var result = 0
        list.forEachIndexed { index, pair1 ->
            for(i in index + 1 .. list.lastIndex) {
                if (pair1.first and list[i].first == 0L ) {
                    val sum = pair1.second + list[i].second
                    if (sum > result)
                        result = sum
                }
            }
        }
        return result
    }

    private fun calculatePressure(
        state: ValvesState,
        pressure: Int,
        time: Int
    ) {
        val pressureForMinute = getPressureForState(state.opened)
        val newPressure = pressure + pressureForMinute
        if (time == TIME_LIMIT_1) {
            if (newPressure > result)
                result = newPressure

            return
        }

        if (time <= TIME_LIMIT_2) {
            val expectedPressure = newPressure + (TIME_LIMIT_2 - time) * pressureForMinute
            pressureForPart2Map.merge(state.opened, expectedPressure) {
                old, new -> max(old, new)
            }
        }

        if (maxOpened == state.opened) {
            val expectedPressure = newPressure + (TIME_LIMIT_1 - time) * pressureForMinute
            if (expectedPressure > result)
                result = expectedPressure

            return
        }

        if (timeMap.getOrDefault(state, TIME_LIMIT_1 + 1) < time)
            return
        timeMap[state] = time

        for (doMove in 0..1) { // 1 - move, 0 - open
            if (doMove == 1) {
                valvesData.moves[state.current].forEach {
                    calculatePressure(
                        ValvesState(it, state.opened),
                        newPressure,
                        time + 1
                    )
                }
            } else if ((state.opened and (1L shl state.current) == 0L)
                && valvesData.flowRate[state.current] > 0
            )
                calculatePressure(
                    ValvesState(state.current, state.opened or (1L shl state.current)),
                    newPressure,
                    time + 1
                )
        }
    }
}