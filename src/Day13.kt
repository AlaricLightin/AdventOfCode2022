import java.util.Stack
import kotlin.math.sign

fun main() {
    fun part1(input: List<String>): Int {
        var result = 0
        var pairIndex = 0
        var firstLine = ""
        input.forEachIndexed { index, s ->
            when(index % 3) {
                0 -> firstLine = s
                1 -> {
                    pairIndex++
                    if (compareStrings(firstLine, s) == -1)
                        result += pairIndex
                }
            }
        }
        return result
    }

    fun part2(input: List<String>): Int {
        val list: MutableList<Pair<ElemList, Boolean>> = input
            .filter { it.isNotBlank() }
            .map { Pair(createElemList(it), false) }
            .toMutableList()

        list.add(Pair(createElemList("[[2]]"), true))
        list.add(Pair(createElemList("[[6]]"), true))

        list.sortWith { p1, p2 -> compareElemList(p1.first, p2.first) }

        var result = 1
        list.forEachIndexed { index, pair -> if (pair.second) result *= (index + 1) }
        return result
    }

    check(compareStrings("[1,1,3,1,1]", "[1,1,5,1,1]") == -1)
    check(compareStrings("[[1],[2,3,4]]", "[[1],4]") == -1)
    check(compareStrings("[9]", "[[8,7,6]]") == 1)
    check(compareStrings("[]", "[3]") == -1)

    val testInput = readInput("Day13_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}

private abstract class Element

private class ElemNum(val int: Int): Element()

private class ElemList(val list: List<Element>): Element() {
    val size get() = list.size

    operator fun get(index: Int): Element = list[index]
}

private fun createElemList(input: String): ElemList {
    val childList = mutableListOf<Element>()
    if (input[0] != '[' || input[input.lastIndex] != ']')
        throw IllegalArgumentException(input)

    val stack = Stack<MutableList<Element>>()
    var currentList = childList
    var currentNum: Int? = null
    for (i in 1 until input.lastIndex) {
        when(input[i]) {
            '[' -> {
                stack.push(currentList)
                currentList = mutableListOf()
            }

            ']' -> {
                if (currentNum != null) {
                    currentList.add(ElemNum(currentNum))
                    currentNum = null
                }
                val parentList = stack.pop()
                parentList.add(ElemList(currentList))
                currentList = parentList
            }

            ',' -> {
                if (currentNum != null) {
                    currentList.add(ElemNum(currentNum))
                    currentNum = null
                }
            }

            in '0' .. '9' -> {
                if (currentNum == null) {
                    currentNum = 0
                }
                currentNum = currentNum * 10 + input[i].digitToInt()
            }
        }
    }
    if (currentNum != null)
        childList.add(ElemNum(currentNum))
    return ElemList(childList)
}

private fun createElemListFromNum(elemNum: ElemNum): ElemList {
    return ElemList(listOf(elemNum))
}

private fun compareElemList(list1: ElemList, list2: ElemList): Int {
    for(i in 0 until list1.size.coerceAtMost(list2.size)) {
        val compareResult: Int = if (list1[i] is ElemNum) {
            if (list2[i] is ElemNum)
                ((list1[i] as ElemNum).int - (list2[i] as ElemNum).int).sign
            else
                compareElemList(createElemListFromNum(list1[i] as ElemNum), list2[i] as ElemList)
        }
        else {
            if (list2[i] is ElemNum)
                compareElemList(list1[i] as ElemList, createElemListFromNum(list2[i] as ElemNum))
            else
                compareElemList(list1[i] as ElemList, list2[i] as ElemList)
        }

        if (compareResult != 0)
            return compareResult
    }

    return (list1.size - list2.size).sign
}

private fun compareStrings(s1: String, s2: String): Int {
    return compareElemList(
        createElemList(s1), createElemList(s2)
    )
}