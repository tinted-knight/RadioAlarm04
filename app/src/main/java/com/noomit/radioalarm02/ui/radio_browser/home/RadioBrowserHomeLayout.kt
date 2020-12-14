package com.noomit.radioalarm02.ui.radio_browser.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.view.isVisible
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.example.radiobrowser.ServerInfo
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.tplog
import com.squareup.contour.ContourLayout

interface RadioBrowserHomeDelegate {
    fun onLanguageClick()
    fun onTagClick()
}

interface IRadioBrowserHomeLayout {
    var delegate: RadioBrowserHomeDelegate?
    fun setServerAdapter(adapter: ServerListAdapter)
    fun showLoading()
    fun update(content: List<ServerInfo>)
    fun update(activerServer: ServerInfo?)
}

@SuppressLint("SetTextI18n")
class RadioBrowserHomeLayout(context: Context, attributeSet: AttributeSet? = null) :
    ContourLayout(context, attributeSet), IRadioBrowserHomeLayout {

    override var delegate: RadioBrowserHomeDelegate? = null

    private val btnLanguages = MaterialButton(context).apply {
        text = "Languages"
        setOnClickListener {
            delegate?.onLanguageClick()
        }
    }

    private val btnTags = MaterialButton(context).apply {
        text = "Tags"
        setOnClickListener {
            delegate?.onTagClick()
        }
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

    private val serverList = ServerListView(context)

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

        val xPadding = { if (!serverList.isSelected) 0.xdip else 32.xdip }
        val yPadding = { if (!serverList.isSelected) 0.ydip else 64.ydip }

        serverList.layoutBy(
            x = leftTo { parent.left() + xPadding() }.rightTo { parent.right() - xPadding() },
            y = topTo {
                when {
                    serverList.isSelected -> parent.top() + yPadding()
                    else -> searchTag.bottom()
                }
            }
        )

        serverList.setOnClickListener(::serverListClick)
    }

    override fun setServerAdapter(adapter: ServerListAdapter) {
        serverList.recycler.adapter = adapter
    }

    override fun showLoading() {
        serverList.recycler.isVisible = false
        btnLanguages.isEnabled = false
        btnTags.isEnabled = false
        btnTopVoted.isEnabled = false
    }

    override fun update(content: List<ServerInfo>) {
        (serverList.recycler.adapter as ServerListAdapter).submitList(content)
        serverList.recycler.isVisible = true
        btnLanguages.isEnabled = true
        btnTags.isEnabled = true
        btnTopVoted.isEnabled = true
    }

    override fun update(activerServer: ServerInfo?) {
        tplog("update, $activerServer")
        serverList.active.text = activerServer?.urlString ?: "No activer server..."
    }

    private fun serverListClick(view: View) {
        TransitionManager.beginDelayedTransition(this, ChangeBounds()
            .setInterpolator(OvershootInterpolator(1f))
            .setDuration(400)
        )
        serverList.isSelected = !serverList.isSelected
        requestLayout()
    }
}
