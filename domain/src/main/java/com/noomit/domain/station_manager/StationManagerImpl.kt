package com.noomit.domain.station_manager

import com.noomit.domain.entities.CategoryModel
import com.noomit.domain.entities.StationModel
import com.noomit.domain.entities.StationNetworkEntity
import com.noomit.domain.radio_browser.RadioBrowser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class StationManagerImpl @Inject constructor(
  private val apiService: RadioBrowser,
) : StationManager {

  private val _state = MutableStateFlow<StationManagerState>(StationManagerState.Loading)
  override val state = _state

  override suspend fun stationsBy(category: CategoryModel) {
    val last = state.value
    if (last is StationManagerState.Success && last.category == category) {
      return
    }

    _state.value = StationManagerState.Loading

    val flow: Flow<List<StationNetworkEntity>> = when (category) {
      is CategoryModel.Language -> flowOf(apiService.stationsByLanguage(category.name))
      is CategoryModel.Tag -> flowOf(apiService.stationsByTag(category.name))
      is CategoryModel.TopVoted -> flowOf(apiService.getTopVoted())
      is CategoryModel.GlobalSearch -> flowOf(
        apiService.search(
          category.searchName,
          category.searchTag
        )
      )
    }

    flow.flowOn(Dispatchers.IO)
      .map { stationList ->
        stationList.sortedByDescending { it.votes }
          .map {
            val tagList = it.tags.split(",").onEach { tag -> tag.trim() }
            StationModel(
              name = it.name,
              upvotes = it.votes.toString(),
              streamUrl = it.url_resolved,
              country = it.country,
              homepage = it.homepage,
              codec = it.codec,
              bitrate = it.bitrate,
              favicon = it.favicon,
              tags = tagList,
            )
          }
      }
      .flowOn(Dispatchers.Default)
      .catch { e -> _state.value = StationManagerState.Failure(e) }
      .collect { _state.value = StationManagerState.Success(it, category) }
  }
}
