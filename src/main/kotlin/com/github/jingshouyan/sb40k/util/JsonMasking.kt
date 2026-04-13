package com.github.jingshouyan.sb40k.util


import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import org.slf4j.LoggerFactory
import kotlin.math.pow

class JsonMasking(private val cfg: Map<String, Int>) {

    companion object {
        const val BIT_STRING_END = 0
        const val BIT_STRING_START = 1

        const val CHAR_MASK = '*'
        const val CHAR_NULL = '\u0000'
        const val CHAR_ZERO = '0'


        private val JSON_FACTORY = JsonFactory()
    }


    private val log = LoggerFactory.getLogger(JsonMasking::class.java)


    /**
     * json 字符串脱敏
     */
    fun masking(json: String?): String? {
        if (json == null || cfg.isEmpty()) {
            return json
        }

        return try {
            val chars = json.toCharArray()
            val parser = JSON_FACTORY.createParser(chars)

            do {
                var token = parser.nextToken()
                if (token == JsonToken.FIELD_NAME) {
                    val key = parser.currentName()
                    token = parser.nextToken()
                    val name2 = parser.currentName()
                    val setting = cfg[key]

                    if (setting != null) {
                        when (token) {
                            JsonToken.VALUE_STRING -> stringMasking(chars, parser, setting)

                            JsonToken.VALUE_NUMBER_INT,
                            JsonToken.VALUE_NUMBER_FLOAT -> numberMasking(chars, parser, setting)

                            JsonToken.START_OBJECT,
                            JsonToken.START_ARRAY -> objectMasking(chars, parser, setting)

                            else -> {}
                        }
                    }
                }
            } while (parser.hasCurrentToken())

            val sb = StringBuilder()
            for (c in chars) {
                if (c != CHAR_NULL) {
                    sb.append(c)
                }
            }
            sb.toString()

        } catch (e: Throwable) {
            log.warn("json mask failed, data: {}", json, e)
            json
        }
    }


    /**
     * json 字符串脱敏,并去除格式，object,array 不整体脱敏，而是对其中的字段进行脱敏
     */
    fun masking2(json: String?): String? {
        if (json == null || cfg.isEmpty()) {
            return json
        }

        return try {
            val parser = JSON_FACTORY.createParser(json)
            val sb = StringBuilder()
            var token: JsonToken?
            var previousToken: JsonToken? = null
            val keyIndex = mutableMapOf<Int, String>()
            var index = 0
            while (parser.nextToken().also { token = it } != null) {
                if (needComma(previousToken, token)) {
                    sb.append(',')
                }
                if (token == JsonToken.START_OBJECT) {
                    index++
                }
                if (token == JsonToken.END_OBJECT) {
                    index--
                }

                when (token) {
                    JsonToken.START_OBJECT,
                    JsonToken.END_OBJECT,
                    JsonToken.START_ARRAY,
                    JsonToken.END_ARRAY,
                    JsonToken.VALUE_TRUE,
                    JsonToken.VALUE_FALSE,
                    JsonToken.VALUE_NULL
                        -> sb.append(token.asString())

                    JsonToken.FIELD_NAME -> {
                        val key = parser.currentName()
                        keyIndex[index] = key
                        sb.append('"').append(key).append('"').append(':')
                    }

                    JsonToken.VALUE_STRING -> {

                        val setting = cfg[keyIndex[index]]
                        val value = parser.valueAsString
                        val masked = stringMaskingInPlace(value, setting)
                        sb.append('"').append(masked).append('"')
                    }

                    JsonToken.VALUE_NUMBER_INT,
                    JsonToken.VALUE_NUMBER_FLOAT -> {
                        val setting = cfg[keyIndex[index]]
                        if (setting != null) {
                            sb.append('0')
                        } else {
                            sb.append(parser.valueAsString)
                        }
                    }

                    else -> sb.append(token?.asString())
                }

                previousToken = token
            }
            sb.toString()
        } catch (e: Throwable) {
            log.warn("json mask failed, data: {}", json, e)
            json
        }
    }

    fun needComma(previousToken: JsonToken?, currentToken: JsonToken?): Boolean {
        if (previousToken == null || currentToken == null) {
            return false
        }
        when (previousToken) {
            JsonToken.START_OBJECT, JsonToken.START_ARRAY, JsonToken.FIELD_NAME -> return false
            else -> {}
        }
        when (currentToken) {
            JsonToken.END_OBJECT, JsonToken.END_ARRAY -> return false
            else -> {}
        }

        return true
    }

    fun stringMaskingInPlace(value: String, setting: Int?): String {
        if (setting == null) {
            return value
        }
        val prefixLen = getDigit(setting, BIT_STRING_START)
        val suffixLen = getDigit(setting, BIT_STRING_END)
        val valueLen = value.length
        if (prefixLen + suffixLen < valueLen) {
            val sb = StringBuilder()
            sb.append(value.substring(0, prefixLen))
            sb.append(CHAR_MASK)
            sb.append(value.substring(valueLen - suffixLen))
            return sb.toString()
        } else {
            return value
        }
    }


    /**
     * 字符串脱敏（完全保留原逻辑）
     */
    private fun stringMasking(chars: CharArray, parser: JsonParser, setting: Int) {
        val prefixLen = getDigit(setting, BIT_STRING_START)
        val suffixLen = getDigit(setting, BIT_STRING_END)

        val value = parser.valueAsString
        val valueLen = value.length

        if (prefixLen + suffixLen < valueLen) {
            val offset = parser.textOffset
            val start = offset + prefixLen
            val end = offset + valueLen - suffixLen

            for (i in start until end) {
                if (i == start) {
                    chars[i] = CHAR_MASK
                } else {
                    chars[i] = CHAR_NULL
                }
            }
        }
    }

    /**
     * 数字脱敏（完全保留原逻辑 ⚠️ 可能导致 JSON 非法）
     */
    private fun numberMasking(chars: CharArray, parser: JsonParser, setting: Int) {
        val start = parser.textOffset
        val value = parser.valueAsString
        val valueLen = value.length
        val end = start + valueLen

        for (i in start until end) {
            if (i == start) {
                chars[i] = CHAR_ZERO
            } else {
                chars[i] = CHAR_NULL
            }
        }
    }

    /**
     * 对象脱敏（完全保留原逻辑）
     */
    private fun objectMasking(chars: CharArray, parser: JsonParser, setting: Int) {
        var flag = 1
        val start = parser.currentLocation().charOffset.toInt()

        while (flag > 0) {
            val token = parser.nextToken()
            if (token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY) {
                flag++
            }
            if (token == JsonToken.END_OBJECT || token == JsonToken.END_ARRAY) {
                flag--
            }
        }

        val end = parser.currentLocation().charOffset.toInt()

        for (i in start until end - 1) {
            chars[i] = CHAR_NULL
        }
    }

    private fun getDigit(source: Int, n: Int): Int {

        val x = 10.0.pow(n.toDouble()).toInt()
        return source / x % 10
    }
}