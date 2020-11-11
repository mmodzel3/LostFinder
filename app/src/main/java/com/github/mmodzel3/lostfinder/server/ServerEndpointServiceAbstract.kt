package com.github.mmodzel3.lostfinder.server

import android.app.Service
import android.app.job.JobInfo
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


abstract class ServerEndpointServiceAbstract : Service() {
    private val SERVER_URL = "http://192.168.0.107:8080/"

    protected inline fun <reified T: ServerEndpointInterface> createEndpoint(): T {
        return createRetrofit().create(T::class.java)
    }

    protected fun createRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}