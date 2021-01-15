package com.noomit.radioalarm02.base

import timber.log.Timber

interface WithLogTag {
    val logTag: String

    fun plog(message: String) = Timber.tag("tagg-$logTag")
        .i("$message [${Thread.currentThread().name}]")
}
