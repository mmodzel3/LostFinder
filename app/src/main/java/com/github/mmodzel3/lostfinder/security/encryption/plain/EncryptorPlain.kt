package com.github.mmodzel3.lostfinder.security.encryption.plain

import android.content.Context
import android.util.Base64
import com.github.mmodzel3.lostfinder.security.encryption.EncryptorInterface

class EncryptorPlain : CryptorPlainAbstract(), EncryptorInterface {
    companion object {
        fun getInstance() : EncryptorPlain {
            return EncryptorPlain()
        }
    }

    override fun encrypt(data: String, context: Context) : String {
        val encryptedData: ByteArray = Base64.encode(data.toByteArray(), Base64.DEFAULT)
        return String(encryptedData)
    }
}
