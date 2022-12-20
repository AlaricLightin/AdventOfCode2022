fun main() {
    fun part1(blueprints: List<RobotBlueprint>): Int {
        return blueprints.sumOf {
            it.id * getMaxGeodes(it, 24)
        }
    }

    fun part2(blueprints: List<RobotBlueprint>): Int {
        return blueprints.fold(1) { acc, robotBlueprint ->
            acc * getMaxGeodes(robotBlueprint, 32)
        }
    }

    val testBlueprints = readBlueprints("Day19_test")
    check(part1(testBlueprints) == 33)
    check(getMaxGeodes(testBlueprints[0], 32) == 56)
    check(getMaxGeodes(testBlueprints[1], 32) == 62)

    val blueprints = readBlueprints("Day19")
    println(part1(blueprints))
    println(part2(blueprints.take(3)))
}

private fun readBlueprints(inputFilename: String): List<RobotBlueprint> {
    return readInput(inputFilename).map { string ->
        val substrings = string.split(':')
        val id = substrings[0].substringAfter(" ").toInt()
        val costs = substrings[1]
            .split(".")
            .filter { it.isNotBlank() }
            .map {
                val costsString = it.substringAfter("costs ")
                    .split(" and ")

                val result = Array(3) { 0 }
                costsString.forEach { s ->
                    val substrings1 = s.split(" ")
                    val value = substrings1[0].toInt()
                    when(substrings1[1]) {
                        "ore" -> result[0] = value
                        "clay" -> result[1] = value
                        "obsidian" -> result[2] = value
                    }
                }
                result
            }.toTypedArray()

        RobotBlueprint(id, costs)
    }
}

private data class RobotBlueprint(
    val id: Int,
    val robotCosts: Array<Array<Int>>
)

private enum class Actions { WAIT, ORE, CLAY, OBSIDIAN, GEODE }

private fun getMaxGeodes(blueprint: RobotBlueprint, timeLimit: Int): Int {
    var result = 0
    val maxCosts: Array<Int> = Array(3) { index ->
        blueprint.robotCosts.maxOf { it[index] }
    }

    fun canBuildRobot(costs: Array<Int>, resources: Array<Int>): Boolean {
        costs.forEachIndexed { index, i ->
            if (i > resources[index])
                return false
        }
        return true
    }

    fun canBuildAnotherGeodeRobot(time: Int,
                                  obsidianResources: Int,
                                  obsidianRobotCount: Int): Boolean {
        if (time + blueprint.robotCosts[3][2] < timeLimit)
            return true

        val timeToEnd = timeLimit - time - 1
        return obsidianResources +
                (obsidianRobotCount + obsidianRobotCount + timeToEnd - 1) * timeToEnd / 2 >=
                blueprint.robotCosts[3][2]
    }

    fun nextStep(resources: Array<Int>, robots: Array<Int>, time: Int, geodesCount: Int) {
        if (time == timeLimit || !canBuildAnotherGeodeRobot(time, resources[2], robots[2])) {
            if (result < geodesCount)
                result = geodesCount

            return
        }

        enumValues<Actions>().forEach {
            when (it) {
                Actions.WAIT -> {
                    if (blueprint.robotCosts.any { costArray ->
                            var needToWaitResource = false
                            costArray.forEachIndexed { index, i ->
                                if (i != 0) {
                                    if (robots[index] == 0)
                                        return@any false

                                    if (resources[index] < i)
                                        needToWaitResource = true
                                }
                            }
                            needToWaitResource
                        }) {
                        nextStep(
                            arrayOf(
                                resources[0] + robots[0],
                                resources[1] + robots[1],
                                resources[2] + robots[2]
                            ),
                            robots,
                            time + 1,
                            geodesCount
                        )
                    }
                }

                Actions.ORE, Actions.CLAY, Actions.OBSIDIAN -> {
                    if (robots[it.ordinal - 1] >= maxCosts[it.ordinal - 1])
                        return@forEach

                    if (resources[it.ordinal - 1] + robots[it.ordinal - 1] * (timeLimit - time) >
                            maxCosts[it.ordinal - 1] * (timeLimit - time))
                        return@forEach

                    if (it == Actions.ORE && (2..3).any {
                        canBuildRobot(blueprint.robotCosts[it], resources)
                        })
                        return@forEach

                    if (canBuildRobot(blueprint.robotCosts[it.ordinal - 1], resources)) {
                        nextStep(
                            arrayOf(
                                resources[0] + robots[0] - blueprint.robotCosts[it.ordinal - 1][0],
                                resources[1] + robots[1] - blueprint.robotCosts[it.ordinal - 1][1],
                                resources[2] + robots[2] - blueprint.robotCosts[it.ordinal - 1][2],
                            ),
                            arrayOf(
                                robots[0] + if (it == Actions.ORE) 1 else 0,
                                robots[1] + if (it == Actions.CLAY) 1 else 0,
                                robots[2] + if (it == Actions.OBSIDIAN) 1 else 0,
                            ),
                            time + 1,
                            geodesCount
                        )
                    }
                }

                Actions.GEODE -> {
                    if (canBuildRobot(blueprint.robotCosts[3], resources)) {
                        nextStep(
                            arrayOf(
                                resources[0] + robots[0] - blueprint.robotCosts[3][0],
                                resources[1] + robots[1] - blueprint.robotCosts[3][1],
                                resources[2] + robots[2] - blueprint.robotCosts[3][2],
                            ),

                            robots,
                            time + 1,
                            geodesCount + timeLimit - time
                        )
                    }
                }
            }
        }
    }

    nextStep(arrayOf(1, 0, 0), arrayOf(1, 0, 0), 2, 0)

    println("id = ${blueprint.id} result = $result")
    return result
}