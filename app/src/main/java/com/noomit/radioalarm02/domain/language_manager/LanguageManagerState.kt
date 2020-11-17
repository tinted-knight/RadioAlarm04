package com.noomit.radioalarm02.domain.language_manager

sealed class LanguageManagerState {
    object Loading : LanguageManagerState()
    object Empty : LanguageManagerState()
    data class Values(val values: LanguageList) : LanguageManagerState()
    data class Failure(val e: Throwable) : LanguageManagerState()
}
