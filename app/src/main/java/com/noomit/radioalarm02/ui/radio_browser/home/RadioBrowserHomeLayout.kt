package com.noomit.radioalarm02.ui.radio_browser.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.radiobrowser.ServerInfo
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.noomit.radioalarm02.R
import com.squareup.contour.ContourLayout

interface RadioBrowserHomeDelegate {
    fun onLanguageClick()
}

interface IRadioBrowserHomeLayout {
    var delegate: RadioBrowserHomeDelegate?
    fun setServerAdapter(adapter: ServerListAdapter)
    fun showLoading()
    fun showContent(values: List<ServerInfo>)
}

@SuppressLint("SetTextI18n")
class RadioBrowserHomeLayout(context: Context, attributeSet: AttributeSet? = null) :
    ContourLayout(context, attributeSet), IRadioBrowserHomeLayout {

    override var delegate: RadioBrowserHomeDelegate? = null

    override fun setServerAdapter(adapter: ServerListAdapter) {
        rvServerList.adapter = adapter
    }

    override fun showLoading() {
        rvServerList.isVisible = false
        btnLanguages.isEnabled = false
        btnTags.isEnabled = false
        btnTopVoted.isEnabled = false
    }

    override fun showContent(values: List<ServerInfo>) {
        (rvServerList.adapter as ServerListAdapter).submitList(values)
        rvServerList.isVisible = true
        btnLanguages.isEnabled = true
        btnTags.isEnabled = true
        btnTopVoted.isEnabled = true
    }

    private val btnLanguages = MaterialButton(context).apply {
        text = "Languages"
        setOnClickListener {
            delegate?.onLanguageClick()
        }
    }

    private val btnTags = MaterialButton(context).apply {
        text = "Tags"
    }

    private val btnTopVoted = MaterialButton(context).apply {
        text = "Top Voted"
    }

    private val btnStations = MaterialButton(context).apply {
        text = "Stations"
    }

    private val etName = TextInputEditText(context)

    private val searchName = TextInputLayout(context,
        null,
        R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox).apply {
        hint = "name hint"
        boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
        addView(etName)
    }

    private val etTag = TextInputEditText(context)

    private val searchTag = TextInputLayout(context,
        null,
        R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox).apply {
        hint = "tag hint"
        boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
        addView(etTag)
    }

    private val rvServerList = RecyclerView(context).apply {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
        isVerticalScrollBarEnabled = true
    }

    init {
        contourHeightWrapContent()

        val matchParentWidth = matchParentX(marginLeft = 16.dip, marginRight = 16.dip)

        btnLanguages.layoutBy(
            x = matchParentWidth,
            y = topTo { parent.top() }
        )

        btnTags.layoutBy(
            x = matchParentWidth,
            y = topTo { btnLanguages.bottom() }
        )

        btnTopVoted.layoutBy(
            x = matchParentWidth,
            y = topTo { btnTags.bottom() }
        )

        btnStations.layoutBy(
            x = matchParentWidth,
            y = topTo { btnTopVoted.bottom() }
        )

        searchName.layoutBy(
            x = matchParentWidth,
            y = topTo { btnStations.bottom() }
        )

        searchTag.layoutBy(
            x = matchParentWidth,
            y = topTo { searchName.bottom() }
        )

        rvServerList.layoutBy(
            x = matchParentWidth,
            y = topTo { searchTag.bottom() }
        )
    }
}
