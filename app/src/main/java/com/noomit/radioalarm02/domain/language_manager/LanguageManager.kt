package com.noomit.radioalarm02.domain.language_manager

import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.base.WithLogTag
import com.noomit.radioalarm02.data.LanguageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

sealed class ChosedLanguage {
    object None : ChosedLanguage()
    data class Value(val value: LanguageModel) : ChosedLanguage()
}

class LanguageManager(private val apiService: RadioBrowserService) :
    ICategoryManager<LanguageModel, LanguageManagerState, ChosedLanguage>, WithLogTag {

    override val logTag = "lang_manager"

    private val _state = MutableStateFlow<LanguageManagerState>(LanguageManagerState.Loading)
    override val state: StateFlow<LanguageManagerState> = _state

    suspend fun getLanguages() {
        apiService.getLanguageListFlow()
            .flowOn(Dispatchers.IO)
            // #fake delay
            .onEach { delay(500) }
            .map { languageList ->
                languageList.sortedByDescending { it.stationcount }
                    .map {
                        LanguageModel(
                            name = it.name,
                            stationCount = it.stationcount.toString(),
                        )
                    }
            }
            .flowOn(Dispatchers.Default)
            .catch { e ->
                plog("LanguageManager.catch: ${e.localizedMessage}")
                _state.value = LanguageManagerState.Failure(e)
            }
            .collect {
                _state.value = when {
                    it.isEmpty() -> LanguageManagerState.Empty
                    else -> LanguageManagerState.Values(it)
                }
            }
    }

//    override fun onCategoryChoosed(value: LanguageModel) {
//        _chosenCategory.value = ChosedLanguage.Value(value)
//    }

}
