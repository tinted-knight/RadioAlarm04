package com.noomit.radioalarm02.ui.radio_browser.languagelist

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.noomit.radioalarm02.data.CategoryModel
import com.noomit.radioalarm02.tplog
import com.squareup.contour.ContourLayout

interface ICategoryLayout {
    fun setAdapter(adapter: LanguageListAdapter)
    fun showLoading()
    fun showContent(values: List<CategoryModel>)
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
        tplog("contour init. ${recycler.adapter}")
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
        tplog("setAdapter, ${recycler.adapter}")
        recycler.adapter = adapter
    }

    override fun showLoading() {
        tplog("showLoading, ${recycler.adapter}")
        loadingIndicator.isVisible = true
        recycler.isVisible = false
    }

    override fun showContent(values: List<CategoryModel>) {
        tplog("showContent, ${values.size}, ${recycler.adapter}")
        loadingIndicator.isVisible = false
        recycler.isVisible = true
        (recycler.adapter as LanguageListAdapter).submitList(values)
    }
}