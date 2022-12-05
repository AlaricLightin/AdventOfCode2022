import java.util.Stack

val MOVE_REGEX = Regex("move\\s(\\d+)\\sfrom\\s(\\d+)\\sto\\s(\\d+)")

fun main() {
    fun createStackList(input: List<String>, blankLineNumber: Int): List<Stack<Char>> {
        val stackList = mutableListOf<Stack<Char>>()
        for (i in (0..blankLineNumber - 2).reversed()) {
            val s = input[i]

            for (j in 1..s.length step 4) {
                val stackNumber = j / 4
                if (stackNumber >= stackList.size)
                    stackList.add(Stack<Char>())

                if (s[j] != ' ')
                    stackList[stackNumber].push(s[j])
            }
        }
        return stackList
    }

    fun getBlankLineNumber(input: List<String>): Int {
        var blankLineNumber = 0
        run findBlank@{
            input.forEachIndexed { index, s ->
                if (s.isBlank()) {
                    blankLineNumber = index
                    return@findBlank
                }
            }
        }
        return blankLineNumber
    }

    fun getResult(stackList: List<Stack<Char>>): String {
        var result = ""
        for (stack in stackList) {
            result += stack.pop()
        }
        return result
    }

    fun part1(input: List<String>): String {
        val blankLineNumber = getBlankLineNumber(input)
        val stackList = createStackList(input, blankLineNumber)

        for (i in blankLineNumber + 1 until input.size) {
            val s = input[i]
            val matchResult = MOVE_REGEX.matchEntire(s) ?: continue
            repeat(matchResult.groupValues[1].toInt()) {
                val c = stackList[matchResult.groupValues[2].toInt() - 1].pop()
                stackList[matchResult.groupValues[3].toInt() - 1].push(c)
            }
        }

        return getResult(stackList)
    }

    fun part2(input: List<String>): String {
        val blankLineNumber = getBlankLineNumber(input)
        val stackList = createStackList(input, blankLineNumber)

        for (i in blankLineNumber + 1 until input.size) {
            val s = input[i]
            val matchResult = MOVE_REGEX.matchEntire(s) ?: continue
            val count = matchResult.groupValues[1].toInt()
            val tempStack = Stack<Char>()
            repeat(count) {
                val c = stackList[matchResult.groupValues[2].toInt() - 1].pop()
                tempStack.push(c)
            }
            repeat(count) {
                val c = tempStack.pop()
                stackList[matchResult.groupValues[3].toInt() - 1].push(c)
            }
        }

        return getResult(stackList)
    }


    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}