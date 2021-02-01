package com.noomit.radioalarm02.util

import timber.log.Timber

interface WithLogTag {
    val logTag: String

    fun plog(message: String) = Timber.tag("tagg-$logTag")
        .i("$message [${Thread.currentThread().name}]")
}
