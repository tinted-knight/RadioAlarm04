package com.noomit.radioalarm02.util.fragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

inline fun <T> Fragment.collect(values: Flow<T>, crossinline block: suspend (T) -> Unit) =
  lifecycleScope.launchWhenStarted {
    values.collect { block(it) }
  }
