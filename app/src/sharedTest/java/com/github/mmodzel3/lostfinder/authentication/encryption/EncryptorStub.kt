package com.github.mmodzel3.lostfinder.authentication.encryption

import android.content.Context
import com.github.mmodzel3.lostfinder.security.encryption.EncryptorInterface

class EncryptorStub : EncryptorInterface {
    override fun encrypt(data: String, context: Context): String {
        return data
    }
}