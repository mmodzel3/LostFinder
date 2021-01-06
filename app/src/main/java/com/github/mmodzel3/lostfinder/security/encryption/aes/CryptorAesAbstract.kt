package com.github.mmodzel3.lostfinder.security.encryption.aes

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import com.github.mmodzel3.lostfinder.security.encryption.CryptorAbstract
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


abstract class CryptorAesAbstract : CryptorAbstract() {
    companion object {
        private const val SECRET_KEY_ALIAS = "key_aes"
        private const val CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"
        const val KEY_SIZE = 128
    }

    val aesKey: SecretKey?
        get() {
            val secretKeyEntry = keyStore.getEntry(SECRET_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
            return secretKeyEntry?.secretKey
        }

    @RequiresApi(Build.VERSION_CODES.M)
    fun generateAesKey(): SecretKey {
        val keyGenerator: KeyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)

        val keyGenParameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(SECRET_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(KEY_SIZE)
                .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    fun createCipher() : Cipher {
        return Cipher.getInstance(CIPHER_TRANSFORMATION)
    }
}
