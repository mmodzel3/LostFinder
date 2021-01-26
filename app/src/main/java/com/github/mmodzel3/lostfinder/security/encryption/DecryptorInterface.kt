package com.github.mmodzel3.lostfinder.security.encryption

import android.content.Context

interface DecryptorInterface {
    fun decrypt(data: String, context: Context) : String
}
