package com.noomit.radioalarm02.ui.common

import android.widget.SearchView
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@FlowPreview
fun SearchView.textFlow(scope: LifecycleCoroutineScope): Flow<String?> {
    val flow = MutableStateFlow<String?>(null)

    setOnSearchClickListener { scope.launchWhenStarted { flow.emit(null) } }

    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            query?.let { scope.launchWhenStarted { flow.emit(query) } }
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            newText?.let { scope.launchWhenStarted { flow.emit(newText) } }
            return true
        }

    })

    return flow
}
