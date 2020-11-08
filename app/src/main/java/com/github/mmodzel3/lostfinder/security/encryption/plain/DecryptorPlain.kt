package com.github.mmodzel3.lostfinder.security.encryption.plain

import android.content.Context
import android.util.Base64
import com.github.mmodzel3.lostfinder.security.encryption.DecryptorInterface

class DecryptorPlain : CryptorPlainAbstract(), DecryptorInterface {
    companion object {
        fun getInstance() : DecryptorPlain {
            return DecryptorPlain()
        }
    }

    override fun decrypt(data: String, context: Context) : String {
        val decryptedData: ByteArray = Base64.decode(data, Base64.DEFAULT)
        return String(decryptedData)
    }
}
