package com.example.radiobrowser

class RadioBrowserService(private val api: RadioBrowserController.RadioBrowserApi) {

    suspend fun getAllStations(): List<StationNetworkEntity> {
        return api.getAllStations()
    }

    suspend fun getTopVote(): List<StationNetworkEntity> {
        return api.getTopVoted()
    }

    suspend fun getTags(): List<LanguageNetworkEntity> {
        return api.getTagList()
    }

    suspend fun getLanguageList(): List<LanguageNetworkEntity> {
        return api.getLanguageList()
    }

    suspend fun stationsByCountry(langString: String): List<StationNetworkEntity> {
        return api.getStationsByLanguage(langString)
    }

    suspend fun stationsByTag(tag: String): List<StationNetworkEntity> {
        return api.getStationsByTag(tag)
    }

    suspend fun search(name: String, tag: String): List<StationNetworkEntity> {
        return api.search(SearchRequest(name, tag))
    }

}