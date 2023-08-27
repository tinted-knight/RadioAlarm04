package com.noomit.data.remote

import android.util.Log
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.noomit.data.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

interface ApiFactoryContract {
  fun get(baseUrl: String): RadioBrowserApi
}

class ApiFactory @Inject constructor() : ApiFactoryContract {
  override fun get(baseUrl: String): RadioBrowserApi {
    val gson = GsonBuilder().setLenient().create()
    val client = OkHttpClient.Builder()

    if (BuildConfig.DEBUG) {
      client.addInterceptor { chain ->
        val request = chain.request()
        Log.d("tagg", request.url().toString())
        Log.d("tagg", request.body().toString())
        chain.proceed(request)
      }
    }

    val retrofit = Retrofit.Builder()
      .baseUrl(baseUrl)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .addCallAdapterFactory(CoroutineCallAdapterFactory())
      .client(client.build())
      .build()

    return retrofit.create(RadioBrowserApi::class.java)
  }
}
