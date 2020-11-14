package com.github.mmodzel3.lostfinder.security.encryption.aes

import android.content.Context
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import com.github.mmodzel3.lostfinder.security.encryption.EncryptorInterface
import javax.crypto.Cipher

class EncryptorAes : CryptorAesAbstract(), EncryptorInterface {
    companion object {
        fun getInstance() : EncryptorAes {
            return EncryptorAes()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun encrypt(data: String, context: Context) : String {
        val cipher: Cipher = createCipher()
        val key = aesKey ?: generateAesKey()

        cipher.init(Cipher.ENCRYPT_MODE, key)

        val encryptedData: ByteArray = cipher.doFinal(data.toByteArray())
        val encryptedDataBase64: ByteArray = Base64.encode(encryptedData, Base64.DEFAULT)
        val ivBase64: ByteArray = Base64.encode(cipher.iv, Base64.DEFAULT)

        return String(encryptedDataBase64) + "," + String(ivBase64)
    }
}
