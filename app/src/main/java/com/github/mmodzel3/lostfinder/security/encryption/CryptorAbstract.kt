package com.github.mmodzel3.lostfinder.security.encryption

import java.security.KeyStore

abstract class CryptorAbstract protected constructor() {
    companion object {
        const val ANDROID_KEY_STORE = "AndroidKeyStore"
    }

    protected val keyStore: KeyStore
        get() = KeyStore.getInstance(ANDROID_KEY_STORE)
}
