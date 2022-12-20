fun main() {
    fun part1(input: List<String>): Int {
        val rootDir = fillTree(input)
        return getSumSizesLess100K(rootDir)
    }

    fun part2(input: List<String>): Int {
        val rootDir = fillTree(input)
        return getDeletingDirSize(rootDir)
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}

private class Directory(val parent: Directory?) {
    val directories = mutableMapOf<String, Directory>()
    val files = mutableMapOf<String, File>()
}

private class File(val size: Int)

private fun fillTree(input: List<String>): Directory {
    var result: Directory? = null
    var currentDirectory: Directory? = null
    for (s in input) {
        when {
            s.startsWith("$ ls") -> {}
            s.startsWith("$ cd") -> {
                val dirname = s.substring(5)
                currentDirectory = when (dirname) {
                    "/" -> {
                        result = Directory(null)
                        result
                    }
                    ".." -> currentDirectory?.parent
                    else -> currentDirectory?.directories?.get(dirname)
                }
            }
            else -> {
                if (s.startsWith("dir")) {
                    val name = s.substring(4)
                    currentDirectory?.directories?.put(name,
                        Directory(currentDirectory))
                }
                else {
                    val substr = s.split(" ")
                    currentDirectory?.files?.put(substr[1],
                        File(substr[0].toInt())
                    )
                }
            }
        }
    }
    return result!!
}

private fun getSumSizesLess100K(rootDir: Directory): Int {
    var result = 0

    fun getSum(rootDir: Directory): Int {
        var innerResult = rootDir.files
            .map { it.value.size }
            .sum()

        for(directory in rootDir.directories.values)
            innerResult += getSum(directory)

        if (innerResult <= 100000 )
            result += innerResult

        return innerResult
    }

    getSum(rootDir)
    return result
}

private const val FULL_SPACE = 70000000
private const val NEED_TO_FREE = 30000000

private fun getDeletingDirSize(rootDir: Directory): Int {
    val sizeList = mutableListOf<Int>()

    fun getSum(rootDir: Directory): Int{
        var innerResult = rootDir.files
            .map { it.value.size }
            .sum()

        for(directory in rootDir.directories.values)
            innerResult += getSum(directory)

        sizeList.add(innerResult)
        return innerResult
    }

    val fullSum = getSum(rootDir)
    val freeSize = FULL_SPACE - fullSum
    var minDirSize = fullSum
    sizeList.forEach {
        if (freeSize + it >= NEED_TO_FREE && it < minDirSize) {
            minDirSize = it
        }
    }
    return minDirSize
}
