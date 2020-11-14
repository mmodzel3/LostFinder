package com.github.mmodzel3.lostfinder.security.encryption.rsa

import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import androidx.annotation.RequiresApi
import com.github.mmodzel3.lostfinder.security.encryption.CryptorAbstract
import java.math.BigInteger
import java.security.*
import java.util.*
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

abstract class CryptorRsaAbstract : CryptorAbstract() {
    companion object {
        private const val CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding"
        private const val CIPHER_PROVIDER = "AndroidOpenSSL"
        private const val CERTIFICATE_SUBJECT = "CN=Sample Name, O=Android Authority"
        private const val SECRET_KEY_ALIAS = "key_rsa"
        private const val RSA = "RSA"
    }

    protected val rsaPublicKey: PublicKey?
        get() {
            val rsaPublicKeyEntry = keyStore.getEntry(SECRET_KEY_ALIAS, null)
                    as? KeyStore.PrivateKeyEntry
            return rsaPublicKeyEntry?.certificate?.publicKey
        }

    protected val rsaPrivateKey: PrivateKey?
        get() {
            val rsaPrivateKeyEntry = keyStore.getEntry(SECRET_KEY_ALIAS, null)
                    as? KeyStore.PrivateKeyEntry
            return rsaPrivateKeyEntry?.privateKey
        }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected fun generateRsaKeys(context: Context): KeyPair {
        val start: Calendar = Calendar.getInstance()
        val end: Calendar = Calendar.getInstance()
        end.add(Calendar.YEAR, 1)

        val spec = KeyPairGeneratorSpec.Builder(context)
                .setAlias(SECRET_KEY_ALIAS)
                .setSubject(X500Principal(CERTIFICATE_SUBJECT))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(start.time)
                .setEndDate(end.time)
                .build()

        val generator: KeyPairGenerator = KeyPairGenerator.getInstance(RSA, ANDROID_KEY_STORE)
        generator.initialize(spec)

        return generator.generateKeyPair()
    }

    fun createCipher() : Cipher {
        return Cipher.getInstance(CIPHER_TRANSFORMATION, CIPHER_PROVIDER)
    }
}
