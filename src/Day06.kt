fun main() {
    fun part1(line: String, count: Int): Int {
        val map = mutableMapOf<Char, Int>()
        line.forEachIndexed { index, c ->
            if (index >= count) {
                map.computeIfPresent(line[index - count]){_, v -> if (v > 1) v - 1 else null }
            }

            if (map.size == count - 1 && !map.containsKey(c)) {
                return index + 1
            }

            map.merge(c, 1){a, b -> a + b}
        }
        return -1
    }

    check(part1("bvwbjplbgvbhsrlpgdmjqwftvncz", 4) == 5)
    check(part1("nppdvjthqldpwncqszvftbrmjlhg", 4) == 6)
    check(part1("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 4) == 10)
    check(part1("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 4) == 11)

    check(part1("bvwbjplbgvbhsrlpgdmjqwftvncz", 14) == 23)

    println(part1(readLine("Day06"), 4))
    println(part1(readLine("Day06"), 14))
}