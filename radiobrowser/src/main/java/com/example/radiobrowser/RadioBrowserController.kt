package com.example.radiobrowser

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

class RadioBrowserController {

    interface RadioBrowserApi {
        @GET("stations/topvote")
        suspend fun getTopVoted(): List<StationNetworkEntity>

        @GET("stations")
        suspend fun getAllStations(): List<StationNetworkEntity>

        @GET("tags")
        suspend fun getTagList(): List<LanguageNetworkEntity>

        @GET("languages")
        suspend fun getLanguageList(): List<LanguageNetworkEntity>

        @GET("stations/bylanguage/{lang}")
        suspend fun getStationsByLanguage(@Path("lang") lang: String): List<StationNetworkEntity>

        @GET("stations/bytag/{tag}")
        suspend fun getStationsByTag(@Path("tag") tag: String): List<StationNetworkEntity>

        @POST("stations/search")
        suspend fun search(@Body searchRequest: SearchRequest): List<StationNetworkEntity>
    }

    companion object {
        // $todo Should get list of available servers https://api.radio-browser.info/
        private const val BASE_URL = "https://de1.api.radio-browser.info/json/"

        fun getApi(): RadioBrowserApi {
            val gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
            return retrofit.create(RadioBrowserApi::class.java)
        }
    }
}