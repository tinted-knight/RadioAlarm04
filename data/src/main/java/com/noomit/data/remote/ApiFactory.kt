package com.noomit.data.remote

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

interface ApiFactoryContract {
    fun get(baseUrl: String): RadioBrowserApi
}

class ApiFactory @Inject constructor() : ApiFactoryContract {
    override fun get(baseUrl: String): RadioBrowserApi {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
        return retrofit.create(RadioBrowserApi::class.java)
    }
}
