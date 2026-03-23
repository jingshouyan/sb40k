package com.github.jingshouyan.sb40k.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object AesGcmUtil {

    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_LENGTH = 12
    private const val TAG_LENGTH = 128

    private val secureRandom = SecureRandom()

    //  关键：自动处理 key 长度
    private fun normalizeKey(key: String): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(key.toByteArray(Charsets.UTF_8)) // 32 bytes
        return SecretKeySpec(keyBytes, "AES")
    }

    fun encryptBytes(plainBytes: ByteArray, key: String): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)

        val iv = ByteArray(IV_LENGTH)
        secureRandom.nextBytes(iv)

        val spec = GCMParameterSpec(TAG_LENGTH, iv)

        cipher.init(Cipher.ENCRYPT_MODE, normalizeKey(key), spec)

        val encrypted = cipher.doFinal(plainBytes)

        // iv + cipherText + tag（GCM 已包含 tag）
        return iv + encrypted
    }

    fun decryptBytes(cipherBytes: ByteArray, key: String): ByteArray {
        val iv = cipherBytes.copyOfRange(0, IV_LENGTH)
        val encrypted = cipherBytes.copyOfRange(IV_LENGTH, cipherBytes.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(TAG_LENGTH, iv)

        cipher.init(Cipher.DECRYPT_MODE, normalizeKey(key), spec)

        return cipher.doFinal(encrypted)

    }

    //  加密
    fun encrypt(plainText: String, key: String): String {
        val result = encryptBytes(plainText.toByteArray(Charsets.UTF_8), key)

        return Base64.getEncoder().encodeToString(result)
    }

    //  解密
    fun decrypt(cipherText: String, key: String): String {
        val decrypted = decryptBytes(cipherText.toByteArray(Charsets.UTF_8), key)

        return String(decrypted, Charsets.UTF_8)
    }
}