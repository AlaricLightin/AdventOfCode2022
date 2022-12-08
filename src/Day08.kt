fun main() {
    fun part1(input: List<String>): Int {
        val resultSet = mutableSetOf<Point>()
        val rowCount = input.size
        val columnCount = input[0].length
        for (i in 1 .. rowCount - 2) {
            checkLine(
                input,
                Point(i, 0),
                { point -> Point(point.row, point.column + 1)},
                columnCount - 1,
                resultSet
            )

            checkLine(
                input,
                Point(i, columnCount - 1),
                { point -> Point(point.row, point.column - 1)},
                columnCount - 1,
                resultSet
            )
        }

        for (i in 1 .. columnCount - 2) {
            checkLine(
                input,
                Point(0, i),
                { point -> Point(point.row + 1, point.column)},
                rowCount - 1,
                resultSet
            )

            checkLine(
                input,
                Point(rowCount - 1, i),
                { point -> Point(point.row - 1, point.column)},
                rowCount - 1,
                resultSet
            )
        }

        //println(resultSet.size)
        return resultSet.size + rowCount * 2 + (columnCount - 2) * 2
    }

    fun part2(input: List<String>): Long {
        val rowCount = input.size
        val columnCount = input[0].length
        var maxScore: Long = 0
        for (i in 1 .. rowCount - 2)
            for (j in 1 .. columnCount - 2) {
                val currentSize = input[i][j]
                var count = 0
                for (k in (0 until i).reversed()) {
                    count++
                    if (input[k][j] >= currentSize)
                        break
                }
                var score: Long = count.toLong()

                count = 0
                for (k in (0 until j).reversed()) {
                    count++
                    if (input[i][k] >= currentSize)
                        break
                }
                score *= count

                count = 0
                for (k in i + 1 until rowCount) {
                    count++
                    if (input[k][j] >= currentSize)
                        break
                }
                score *= count

                count = 0
                for (k in j + 1 until columnCount) {
                    count++
                    if (input[i][k] >= currentSize)
                        break
                }
                score *= count

                if (score > maxScore)
                    maxScore = score
            }

        return maxScore
    }

    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8L)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}

private data class Point(val row: Int, val column: Int)

private fun checkLine(
    input: List<String>,
    startPoint: Point,
    nextPointFunction: (Point) -> Point,
    pointsCount: Int,
    resultSet: MutableSet<Point>
) {
    var nextPoint = startPoint
    var maxSize = input[nextPoint.row][nextPoint.column]
    repeat(pointsCount - 1){
        nextPoint = nextPointFunction.invoke(nextPoint)
        if (input[nextPoint.row][nextPoint.column] > maxSize) {
            maxSize = input[nextPoint.row][nextPoint.column]
            resultSet.add(nextPoint)
        }
    }
}