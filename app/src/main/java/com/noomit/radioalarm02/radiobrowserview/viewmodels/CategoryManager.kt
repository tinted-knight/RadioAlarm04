package com.noomit.radioalarm02.radiobrowserview.viewmodels

import timber.log.Timber

interface WithLogTag {
    val logTag: String

    fun plog(message: String) = Timber.tag(logTag).i("$message [${Thread.currentThread().name}]")
}

interface CategoryManager : WithLogTag {
}
