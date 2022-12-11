fun main() {
    fun solution(input: List<String>, roundCount: Int, worthLevelUpdate: (Long) -> Long): Long {
        val monkeyList: List<Monkey> = readMonkeyList(input)
        val inspectionList = Array(monkeyList.size) { 0 }
        val divider = monkeyList
            .map { m -> m.divisionCheckNum }
            .fold(1L) { a, b -> a * b }
        repeat(roundCount) {
            monkeyList.forEachIndexed { index, monkey ->
                monkey.itemList.forEach {
                    inspectionList[index]++
                    val newWorthLevel = worthLevelUpdate.invoke(monkey.getWorthLevel(it) % divider )
                    val newMonkeyIdx = monkey.getNewMonkeyIdx(newWorthLevel)
                    monkeyList[newMonkeyIdx].itemList.add(newWorthLevel)
                }
                monkey.itemList.clear()
            }
        }

        val maxTwo = inspectionList.sortedArrayDescending().take(2)
        return maxTwo[0].toLong() * maxTwo[1]
    }

    val testInput = readInput("Day11_test")
    check(solution(testInput, 20) { a -> a / 3 } == 10605L)
    check(solution(testInput, 10000) { a -> a } == 2713310158)

    val input = readInput("Day11")
    println(solution(input, 20)  { a -> a / 3 } )
    println(solution(input, 10000) { a -> a })
}

private class Monkey(
    val itemList: MutableList<Long>,
    val operation: Operation,
    val divisionCheckNum: Int,
    val monkeyTrueIdx: Int,
    val monkeyFalseIdx: Int
) {
    fun getWorthLevel(level: Long): Long {
        return operation.calculate(level)
    }

    fun getNewMonkeyIdx(newWorthLevel: Long): Int {
        return if (newWorthLevel % divisionCheckNum == 0L) monkeyTrueIdx else monkeyFalseIdx
    }
}

private enum class OperationType { ADD, MULTIPLE }
private class Operand(string: String) {
    val isWorthLevel: Boolean = string == "old"
    val value: Long? = string.toLongOrNull()
}

private data class Operation(
    val operand1: Operand,
    val operand2: Operand,
    val operationType: OperationType
) {
    fun calculate(worthLevel: Long): Long {
        return when (operationType) {
            OperationType.ADD -> {
                operand1.getOperandValue(worthLevel) + operand2.getOperandValue(worthLevel)
            }
            OperationType.MULTIPLE -> {
                operand1.getOperandValue(worthLevel) * operand2.getOperandValue(worthLevel)
            }
        }
    }
}

private fun Operand.getOperandValue(worthLevel: Long): Long {
    return if (isWorthLevel) worthLevel else value!!
}

private fun readMonkeyList(input: List<String>): List<Monkey> {
    val result = mutableListOf<Monkey>()
    var items: MutableList<Long>? = null
    var operation: Operation? = null
    var divisionCheckNum = 1
    var trueIdx = -1
    var falseIdx = -1

    input.forEach { string ->
        when {
            string.startsWith("  Starting items") -> {
                items = string.substringAfterLast(": ")
                    .split(", ")
                    .map { it.toLong() }
                    .toMutableList()
            }

            string.startsWith("  Operation") -> {
                val expression = string.substringAfterLast("new = ")
                val operationType = if (expression.contains("+"))
                    OperationType.ADD
                else
                    OperationType.MULTIPLE

                operation = Operation(
                    Operand(expression.substringBefore(" ")),
                    Operand(expression.substringAfterLast(" ")),
                    operationType
                )
            }

            string.contains("Test") -> {
                divisionCheckNum = string.substringAfterLast(" ").toInt()
            }

            string.contains("If true") -> {
                trueIdx = string.substringAfterLast(" ").toInt()
            }

            string.contains("If false") -> {
                falseIdx = string.substringAfterLast(" ").toInt()
            }

            string.isBlank() -> {
                result.add(Monkey(
                    items!!,
                    operation!!,
                    divisionCheckNum,
                    trueIdx,
                    falseIdx
                ))
            }
        }
    }

    result.add(Monkey(
        items!!,
        operation!!,
        divisionCheckNum,
        trueIdx,
        falseIdx
    ))

    return result
}
