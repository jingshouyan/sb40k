package com.github.jingshouyan.sb40k.util

import org.junit.jupiter.api.Test

class JsonMaskingTest {

    val json = """
        {
            "username": "jingshouyan",

            "email": [{"username":"hahahaha","password": "123456","aa":"bbbb"   },"jing", "shou", "yan",123,{}],
                        "password": "123456",
            "age": 18,
            "married": false
        }
         """

    val jsonMasking = JsonMasking(mapOf("username" to 14, "password" to 22, "email" to 11))

    @Test
    fun masking() {
        val masked = jsonMasking.masking2(json)
        println(masked)
    }

}