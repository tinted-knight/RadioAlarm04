package com.noomit.radioalarm02.domain.language_manager

import com.noomit.radioalarm02.data.LanguageModel

sealed class LanguageManagerState {
    object Loading : LanguageManagerState()
    object Empty : LanguageManagerState()
    data class Values(val values: List<LanguageModel>) : LanguageManagerState()
    data class Failure(val e: Throwable) : LanguageManagerState()
}
