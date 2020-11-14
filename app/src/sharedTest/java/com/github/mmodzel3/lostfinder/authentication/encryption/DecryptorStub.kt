package com.github.mmodzel3.lostfinder.authentication.encryption

import android.content.Context
import com.github.mmodzel3.lostfinder.security.encryption.DecryptorInterface

class DecryptorStub : DecryptorInterface {
    override fun decrypt(data: String, context: Context): String {
        return data
    }
}