package com.github.mmodzel3.lostfinder.security.encryption.rsa

import android.content.Context
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import com.github.mmodzel3.lostfinder.security.encryption.DecryptorInterface
import javax.crypto.Cipher

class DecryptorRsa : CryptorRsaAbstract(), DecryptorInterface {
    companion object {
        fun getInstance() : DecryptorRsa {
            return DecryptorRsa()
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun decrypt(data: String, context: Context): String {
        val cipher: Cipher = createCipher()
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey)

        val byteData: ByteArray = Base64.decode(data, Base64.DEFAULT)
        val decryptedData: ByteArray = cipher.doFinal(byteData)
        return String(decryptedData)
    }
}
