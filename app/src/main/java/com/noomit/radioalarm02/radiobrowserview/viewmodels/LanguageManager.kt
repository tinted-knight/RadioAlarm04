package com.noomit.radioalarm02.radiobrowserview.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.model.LanguageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

typealias LanguageList = List<LanguageModel>
typealias LanguageListResponse = Result<LanguageList>

@FlowPreview
@ExperimentalCoroutinesApi
class LanguageManager(apiService: RadioBrowserService) : CategoryManager<LanguageModel> {

    override val logTag: String = "app-tag-lang_manager"

    override val chosenCategory = MutableLiveData<LanguageModel>()

    private val chosenChannel = ConflatedBroadcastChannel<LanguageModel?>()
    override val chosenFlow = chosenChannel.asFlow()

    override fun onCategoryChoosed(value: LanguageModel) {
        if (chosenCategory.value == value) return
        chosenCategory.value = value
        chosenChannel.offer(value)
    }

    override val categoryFlow: Flow<LanguageListResponse> = apiService
        .getLanguageFlow()
        .flowOn(Dispatchers.IO)
        .map { list ->
            Result.success(list.sortedByDescending { it.stationcount }
                .map {
                    LanguageModel(
                        name = it.name,
                        stationCount = it.stationcount.toString(),
                    )
                })
        }
        .flowOn(Dispatchers.Default)
        .onEach { chosenChannel.offer(null) }
        .catch { emit(Result.failure(java.lang.Exception(it.localizedMessage))) }

//    val languageList: LiveData<LanguageListResponse> = liveData(Dispatchers.Default) {
//        plog("get language list")
//        try {
//            val languageList = withContext(Dispatchers.IO) { apiService.getLanguageList() }
//            if (!languageList.isNullOrEmpty()) {
//                // #fake delay
//                delay(500)
//                val forViewList = languageList
//                    .sortedByDescending { it.stationcount }
//                    .map {
//                        LanguageModel(
//                            name = it.name,
//                            stationCount = it.stationcount.toString(),
//                        )
//                    }
//                plog("${forViewList.size}")
//                emit(Result.success(forViewList))
//            }
//        } catch (e: HttpException) {
//            plog(e.localizedMessage ?: "Exception: no message")
//            emit(Result.failure<LanguageList>(Exception("http exception")))
//        }
//    }
}
