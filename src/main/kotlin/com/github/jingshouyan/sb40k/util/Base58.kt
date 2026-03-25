package com.github.jingshouyan.sb40k.util

object Base58 {

    private const val ALPHABET =
        "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"

    private val BASE = ALPHABET.length
    private val INDEXES = IntArray(128)

    init {
        for (i in INDEXES.indices) {
            INDEXES[i] = -1
        }
        for (i in ALPHABET.indices) {
            INDEXES[ALPHABET[i].code] = i
        }
    }

    fun encode(input: ByteArray): String {
        if (input.isEmpty()) return ""

        var zeros = 0
        while (zeros < input.size && input[zeros].toInt() == 0) {
            zeros++
        }

        val temp = input.copyOf()
        val encoded = StringBuilder()

        var start = zeros
        while (start < temp.size) {
            val mod = divmod(temp, start, 256, BASE)
            encoded.append(ALPHABET[mod])
            if (temp[start].toInt() == 0) {
                start++
            }
        }

        repeat(zeros) { encoded.append('1') }

        return encoded.reverse().toString()
    }

    fun decode(input: String): ByteArray {
        if (input.isEmpty()) return ByteArray(0)

        val input58 = ByteArray(input.length)
        for (i in input.indices) {
            val c = input[i]
            val digit = if (c.code < 128) INDEXES[c.code] else -1
            require(digit >= 0) { "Invalid Base58 char: $c" }
            input58[i] = digit.toByte()
        }

        var zeros = 0
        while (zeros < input58.size && input58[zeros].toInt() == 0) {
            zeros++
        }

        val decoded = ByteArray(input.length)
        var j = decoded.size

        var start = zeros
        while (start < input58.size) {
            val mod = divmod(input58, start, BASE, 256)
            decoded[--j] = mod.toByte()
            if (input58[start].toInt() == 0) {
                start++
            }
        }

        while (j < decoded.size && decoded[j].toInt() == 0) {
            j++
        }

        return ByteArray(zeros + decoded.size - j).apply {
            System.arraycopy(decoded, j, this, zeros, decoded.size - j)
        }
    }

    private fun divmod(
        number: ByteArray,
        start: Int,
        base: Int,
        divisor: Int
    ): Int {
        var remainder = 0
        for (i in start until number.size) {
            val digit = number[i].toInt() and 0xFF
            val temp = remainder * base + digit
            number[i] = (temp / divisor).toByte()
            remainder = temp % divisor
        }
        return remainder
    }
}