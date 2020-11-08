package com.github.mmodzel3.lostfinder.security.encryption

import com.github.mmodzel3.lostfinder.security.encryption.aes.EncryptorAes
import com.github.mmodzel3.lostfinder.security.encryption.plain.EncryptorPlain
import com.github.mmodzel3.lostfinder.security.encryption.rsa.EncryptorRsa

class Encryptor : CryptorAbstract() {
    companion object {
        fun getInstance() : EncryptorInterface {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                return EncryptorAes.getInstance()
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return EncryptorRsa.getInstance()
            } else {
                return EncryptorPlain.getInstance()
            }
        }
    }
}
