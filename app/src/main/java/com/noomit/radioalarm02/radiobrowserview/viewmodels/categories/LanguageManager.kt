package com.noomit.radioalarm02.radiobrowserview.viewmodels.categories

import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.base.WithLogTag
import com.noomit.radioalarm02.model.LanguageModel
import com.noomit.radioalarm02.radiobrowserview.viewmodels.Action
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

typealias LanguageList = List<LanguageModel>

sealed class ChosedLanguage {
    object None : ChosedLanguage()
    data class Value(val value: LanguageModel) : ChosedLanguage()
}

@ExperimentalCoroutinesApi
class LanguageManager(
    private val apiService: RadioBrowserService,
    private val actions: Flow<Action>,
    private val scope: CoroutineScope,
) : CategoryManager<LanguageModel, LanguageManagerState, ChosedLanguage>, WithLogTag {

    override val logTag = "lang_manager"

//    private val _chosenCategory = MutableStateFlow<ChosedLanguage>(ChosedLanguage.None)
//    override val chosenCategory: Flow<ChosedLanguage> = _chosenCategory

    private val _state = MutableStateFlow<LanguageManagerState>(LanguageManagerState.Loading)
    override val state: StateFlow<LanguageManagerState> = _state

    init {
        scope.launch {
            actions.filterIsInstance<Action.Show.LanguageList>()
                .onEach { _state.value = LanguageManagerState.Loading }
                .flatMapLatest { apiService.getLanguageListFlow() }
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
    }

//    override fun onCategoryChoosed(value: LanguageModel) {
//        _chosenCategory.value = ChosedLanguage.Value(value)
//    }

}
