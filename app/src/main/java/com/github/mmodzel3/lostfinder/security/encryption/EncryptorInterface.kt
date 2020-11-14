package com.github.mmodzel3.lostfinder.security.encryption

import android.content.Context

interface EncryptorInterface {
    fun encrypt(data: String, context: Context): String
}
