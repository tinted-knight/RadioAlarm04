package com.noomit.radioalarm02.ui.radio_browser.languagelist

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.noomit.domain.entities.CategoryModel
import com.squareup.contour.ContourLayout

interface ICategoryLayout {
    fun setAdapter(adapter: LanguageListAdapter)
    fun showLoading()
    fun showContent(values: List<CategoryModel>)
    fun getRecyclerState(): Parcelable?
    fun setRecyclerState(state: Parcelable)
}

class CategoryListLayout(context: Context, attrSet: AttributeSet? = null) :
    ContourLayout(context, attrSet), ICategoryLayout {

    private val recycler = RecyclerView(context).apply {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
        isVerticalScrollBarEnabled = true
    }

    private val loadingIndicator = ProgressBar(context)

    init {
        contourWidthMatchParent()

        loadingIndicator.layoutBy(
            x = centerHorizontallyTo { parent.centerX() },
            y = centerVerticallyTo { parent.centerY() },
        )

        recycler.layoutBy(
            x = matchParentX(),
            y = matchParentY(),
        )
    }

    override fun setAdapter(adapter: LanguageListAdapter) {
        recycler.adapter = adapter
    }

    override fun showLoading() {
        loadingIndicator.isVisible = true
        recycler.isVisible = false
    }

    override fun showContent(values: List<CategoryModel>) {
        loadingIndicator.isVisible = false
        recycler.isVisible = true
        (recycler.adapter as LanguageListAdapter).submitList(values)
    }

    override fun getRecyclerState() = recycler.layoutManager?.onSaveInstanceState()

    override fun setRecyclerState(state: Parcelable) {
        recycler.layoutManager?.onRestoreInstanceState(state)
    }

}
