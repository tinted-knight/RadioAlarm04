package com.noomit.radioalarm02.radiobrowserview.viewmodels.categories

import androidx.lifecycle.liveData
import com.bumptech.glide.load.HttpException
import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.model.LanguageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

typealias LanguageList = List<LanguageModel>

sealed class ChosedLanguage {
    object None : ChosedLanguage()
    data class Value(val value: LanguageModel) : ChosedLanguage()
}

@ExperimentalCoroutinesApi
class LanguageManager(apiService: RadioBrowserService) :
    CategoryManager<LanguageModel, ChosedLanguage> {

    private val _chosenFlow = MutableStateFlow<ChosedLanguage>(ChosedLanguage.None)
    override val chosenCategory: Flow<ChosedLanguage> = _chosenFlow

    override val values = liveData {
        try {
            val languageList = withContext(Dispatchers.IO) { apiService.getLanguageList() }
            if (!languageList.isNullOrEmpty()) {
                // #fake delay
                delay(500)
                val forViewList = languageList
                    .sortedByDescending { it.stationcount }
                    .map {
                        LanguageModel(
                            name = it.name,
                            stationCount = it.stationcount.toString(),
                        )
                    }
                emit(Result.success(forViewList))
            }
        } catch (e: HttpException) {
            emit(Result.failure<LanguageList>(Exception("http exception")))
        }
    }

    override fun onCategoryChoosed(value: LanguageModel) {
        _chosenFlow.value = ChosedLanguage.Value(value)
    }

}
