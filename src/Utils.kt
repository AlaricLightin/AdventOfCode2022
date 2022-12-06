import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

fun getInputFile(name: String) = File("src", "$name.txt")

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = getInputFile(name).readLines()

fun readLine(name: String) = readInput(name)[0]

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')
