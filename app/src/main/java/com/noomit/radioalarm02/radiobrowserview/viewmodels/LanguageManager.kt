package com.noomit.radioalarm02.radiobrowserview.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.bumptech.glide.load.HttpException
import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.model.LanguageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

typealias LanguageList = List<LanguageModel>

class LanguageManager(apiService: RadioBrowserService) : CategoryManager<LanguageModel> {

    override val chosenCategory = MutableLiveData<LanguageModel>()

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
        if (chosenCategory.value == value) return
        chosenCategory.value = value
    }

}
