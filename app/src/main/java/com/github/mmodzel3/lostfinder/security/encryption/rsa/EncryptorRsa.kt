package com.github.mmodzel3.lostfinder.security.encryption.rsa

import android.content.Context
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import com.github.mmodzel3.lostfinder.security.encryption.EncryptorInterface
import java.security.PublicKey
import javax.crypto.Cipher

class EncryptorRsa : CryptorRsaAbstract(), EncryptorInterface {
    companion object {
        fun getInstance() : EncryptorRsa {
            return EncryptorRsa()
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun encrypt(data: String, context: Context): String {
        val cipher: Cipher = createCipher()
        val rsaEncryptKey: PublicKey = rsaPublicKey ?: generateRsaKeys(context).public

        cipher.init(Cipher.ENCRYPT_MODE, rsaEncryptKey)

        val encryptedData: ByteArray = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(encryptedData, Base64.DEFAULT)
    }
}
