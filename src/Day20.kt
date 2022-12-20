fun main() {
    fun mixList(list: MutableList<ListElement>) {
        val listSizeMinus1 = list.size - 1
        for (i in 0..list.lastIndex) {
            val currentIndex: Int = list.indexOfFirst { it.initialOrder == i }
            if (currentIndex < 0)
                throw IllegalArgumentException()

            val element = list[currentIndex]
            list.removeAt(currentIndex)
            val newIndex: Int = (currentIndex.toLong() + element.v).mod(listSizeMinus1)
            if (newIndex > 0)
                list.add(newIndex, element)
            else
                list.add(element)
        }
    }

    fun calculateResult(list: MutableList<ListElement>): Long {
        val zeroIndex = list.indexOfFirst { it.v == 0L }
        if (zeroIndex < 0)
            throw IllegalArgumentException()

        return (1..3).sumOf {
            val index = (zeroIndex + it * 1000).mod(list.size)
            list[index].v
        }
    }

    fun part1(input: List<String>): Long {
        val list: MutableList<ListElement> = input
            .mapIndexed { index, s ->
                ListElement(s.toLong(), index)
            }
            .toMutableList()

        mixList(list)
        return calculateResult(list)
    }

    fun part2(input: List<String>): Long {
        val list: MutableList<ListElement> = input
            .mapIndexed { index, s ->
                ListElement(s.toLong() * 811589153, index)
            }
            .toMutableList()

        repeat(10) { mixList(list) }
        return calculateResult(list)
    }


    val testInput = readInput("Day20_test")
    check(part1(testInput) == 3L)
    check(part2(testInput) == 1623178306L)

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}

private data class ListElement(
    val v: Long,
    val initialOrder: Int
)