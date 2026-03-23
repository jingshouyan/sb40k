package com.github.jingshouyan.sb40k.util

import org.junit.jupiter.api.Test

class AesGcmUtilTest {
    @Test
    fun encrypt() {

        val key = "12345678901234567890123456789012111111111113233"
        val plaintext = "Hello, World!Hello, World!Hello, World!Hello, World!Hello, World!Hello, World!Hello, World!"

        for (i in 0..9) {
            val ciphertext = AesGcmUtil.encrypt(plaintext, key)
            println("Ciphertext $i: $ciphertext")
        }

//        val decryptedText = AesGcmUtil.decrypt(ciphertext, key)
//        println("Decrypted Text: $decryptedText")
    }


}