fun main() {
    fun part1(input: List<String>): Long {
        val yellsMap: MutableMap<String, MonkeyYell> = readYellsMap(input)
        val calculation = MonkeyCalculation(yellsMap)
        return calculation.calculate("root")
    }

    fun part2(input: List<String>): Long {
        val yellsMap: MutableMap<String, MonkeyYell> = readYellsMap(input)
        val currentRoot = yellsMap["root"]
        if (currentRoot !is MonkeyOperation)
            throw IllegalArgumentException()

        val simplification = MonkeyCalculation(yellsMap)
        yellsMap["root"] = MonkeyOperation(
            currentRoot.operand1, currentRoot.operand2, '='
        ) { a, b -> a - b }
        simplification.simplify("root")
        //println(simplification.print("root"))
        return simplification.solveEquation("root")
    }

    val testInput = readInput("Day21_test")
    check(part1(testInput) == 152L)
    check(part2(testInput) == 301L)

    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}

private val functionsMap = mapOf<Char, (Long, Long) -> Long>(
    '+' to { a, b -> a + b },
    '-' to { a, b -> a - b },
    '*' to { a, b -> a * b },
    '/' to { a, b -> a / b },
)

private fun readYellsMap(input: List<String>): MutableMap<String, MonkeyYell> {
    val result = mutableMapOf<String, MonkeyYell>()
    input.forEach {
        val substrings = it.split(": ")
        val id = substrings[0]
        val number: Long? = substrings[1].toLongOrNull()
        if (number != null)
            result[id] = MonkeyNumber(number)
        else {
            val operationSubstrings = substrings[1].split(' ')
            result[id] = MonkeyOperation(
                operationSubstrings[0], operationSubstrings[2],
                operationSubstrings[1][0],
                functionsMap[operationSubstrings[1][0]]!!
            )
        }
    }
    return result
}

private abstract class MonkeyYell

private class MonkeyNumber(val number: Long) : MonkeyYell()

private class MonkeyOperation(
    val operand1: String,
    val operand2: String,
    val functionChar: Char,
    val function: (Long, Long) -> Long
) : MonkeyYell()

private class MonkeyCalculation(val yellsMap: MutableMap<String, MonkeyYell>) {
    private val HUMN = "humn"

    fun calculate(id: String): Long {
        val yell = yellsMap[id]!!
        return if (yell is MonkeyOperation) {
            val result = yell.function(calculate(yell.operand1), calculate(yell.operand2))
            yellsMap[id] = MonkeyNumber(result)
            result
        } else
            (yell as MonkeyNumber).number
    }

    private val cannotSimplified = mutableSetOf(HUMN)
    fun simplify(id: String): Long? {
        if (cannotSimplified.contains(id))
            return null

        val yell = yellsMap[id]!!
        return if (yell is MonkeyOperation) {
            val operand1 = simplify(yell.operand1)
            val operand2 = simplify(yell.operand2)
            if (operand1 != null && operand2 != null) {
                val result = yell.function(operand1, operand2)
                yellsMap[id] = MonkeyNumber(result)
                result
            } else {
                cannotSimplified.add(id)
                null
            }
        } else
            (yell as MonkeyNumber).number
    }

    fun print(id: String): String {
        if (id == HUMN)
            return "X"
        val yell = yellsMap[id]!!
        return if (yell is MonkeyOperation) {
            " (" + print(yell.operand1) + yell.functionChar + print(yell.operand2) + ") "
        }
        else
            (yell as MonkeyNumber).number.toString()
    }

    // solve equation operation = value
    // with condition: only one part of operation has unknown value
    private fun solve(operation: MonkeyOperation, value: MonkeyNumber): Long {
        val yell1 = yellsMap[operation.operand1]!!
        val yell2 = yellsMap[operation.operand2]!!
        if (cannotSimplified.contains(operation.operand1)
            && cannotSimplified.contains(operation.operand2)) {
            println("${print(operation.operand1)} ${operation.functionChar} ${operation.operand2} " +
                    "= ${value.number}")
            throw IllegalArgumentException()
        }

        if (cannotSimplified.contains(operation.operand1)) {
            val newValue = when(operation.functionChar) {
                '+' -> value.number - (yell2 as MonkeyNumber).number
                '-' -> value.number + (yell2 as MonkeyNumber).number
                '*' -> value.number / (yell2 as MonkeyNumber).number
                '/' -> value.number * (yell2 as MonkeyNumber).number

                else -> throw IllegalArgumentException()
            }

            return if (operation.operand1 == HUMN)
                newValue
            else
                solve(yell1 as MonkeyOperation, MonkeyNumber(newValue))
        }
        else {
            val newValue = when(operation.functionChar) {
                '+' -> value.number - (yell1 as MonkeyNumber).number
                '-' -> (yell1 as MonkeyNumber).number - value.number
                '*' -> value.number / (yell1 as MonkeyNumber).number
                '/' -> (yell1 as MonkeyNumber).number / value.number

                else -> throw IllegalArgumentException()
            }

            return if (operation.operand2 == HUMN)
                newValue
            else
                solve(yell2 as MonkeyOperation, MonkeyNumber(newValue))
        }
    }

    fun solveEquation(id: String): Long {
        val rootOperation = yellsMap[id]
        if (rootOperation !is MonkeyOperation)
            throw IllegalArgumentException()

        val yell1 = yellsMap[rootOperation.operand1]!!
        val yell2 = yellsMap[rootOperation.operand2]!!

        return if (yell1 is MonkeyOperation && yell2 is MonkeyNumber)
            solve(yell1, yell2)
        else if (yell2 is MonkeyOperation && yell1 is MonkeyNumber)
            solve(yell2, yell1)
        else
            throw IllegalArgumentException()
    }
}