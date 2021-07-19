package com.noomit.data.remote

import com.noomit.domain.entities.CategoryNetworkEntity
import com.noomit.domain.entities.SearchRequest
import com.noomit.domain.entities.StationNetworkEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RadioBrowserApi {
    @GET("stations/topvote")
    suspend fun getTopVoted(): List<StationNetworkEntity>

    @GET("stations")
    suspend fun getAllStations(): List<StationNetworkEntity>

    @GET("tags")
    suspend fun getTagList(): List<CategoryNetworkEntity>

    @GET("languages?hidebroken=true")
    suspend fun getLanguageList(): List<CategoryNetworkEntity>

    @GET("stations/bylanguage/{lang}")
    suspend fun getStationsByLanguage(@Path("lang") lang: String): List<StationNetworkEntity>

    @GET("stations/bytag/{tag}")
    suspend fun getStationsByTag(@Path("tag") tag: String): List<StationNetworkEntity>

    @POST("stations/search")
    suspend fun search(@Body searchRequest: SearchRequest): List<StationNetworkEntity>
}
