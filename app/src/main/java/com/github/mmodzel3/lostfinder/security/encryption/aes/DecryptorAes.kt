package com.github.mmodzel3.lostfinder.security.encryption.aes

import android.content.Context
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import com.github.mmodzel3.lostfinder.security.encryption.DecryptorInterface
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec


class DecryptorAes : CryptorAesAbstract(), DecryptorInterface {
    companion object {
        fun getInstance() : DecryptorAes {
            return DecryptorAes()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun decrypt(data: String, context: Context) : String {
        val cipher: Cipher = createCipher()
        val dataParts: List<String> = data.split(",")
        val encryptedData: ByteArray = Base64.decode(dataParts[0].toByteArray(), Base64.DEFAULT)
        val iv: ByteArray = Base64.decode(dataParts[1].toByteArray(), Base64.DEFAULT)
        val parameterSpec = GCMParameterSpec(KEY_SIZE, iv)

        cipher.init(Cipher.DECRYPT_MODE, aesKey, parameterSpec)
        val decryptedData: ByteArray = cipher.doFinal(encryptedData)

        return String(decryptedData)
    }
}
