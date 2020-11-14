package com.github.mmodzel3.lostfinder.security.encryption

import com.github.mmodzel3.lostfinder.security.encryption.aes.DecryptorAes
import com.github.mmodzel3.lostfinder.security.encryption.plain.DecryptorPlain
import com.github.mmodzel3.lostfinder.security.encryption.rsa.DecryptorRsa

open class Decryptor : CryptorAbstract() {
    companion object {
        fun getInstance() : DecryptorInterface {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                return DecryptorAes.getInstance()
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return DecryptorRsa.getInstance()
            } else {
                return DecryptorPlain.getInstance()
            }
        }
    }
}
