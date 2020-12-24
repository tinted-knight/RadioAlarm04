package com.noomit.radioalarm02.ui.radio_browser.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.view.isVisible
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.example.radiobrowser.ServerInfo
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.noomit.radioalarm02.ui.theme.appTheme
import com.squareup.contour.ContourLayout

interface RadioBrowserHomeDelegate {
    fun onLanguageClick()
    fun onTagClick()
    fun onTopVotedClick()
}

interface IRadioBrowserHomeLayout {
    var delegate: RadioBrowserHomeDelegate?
    fun setServerAdapter(adapter: ServerListAdapter)
    fun serverListCollapse()
    fun showLoading()
    fun update(content: List<ServerInfo>)
    fun update(activerServer: ServerInfo?)
}

@SuppressLint("SetTextI18n")
class RadioBrowserHomeLayout(context: Context, attributeSet: AttributeSet? = null) :
    ContourLayout(context, attributeSet), IRadioBrowserHomeLayout {

    override var delegate: RadioBrowserHomeDelegate? = null

    private val btnLanguages = materialButton.apply {
        text = "Languages"
        setOnClickListener { delegate?.onLanguageClick() }
    }

    private val btnTags = materialButton.apply {
        text = "Tags"
        setOnClickListener { delegate?.onTagClick() }
    }

    private val btnTopVoted = materialButton.apply {
        text = "Top Voted"
        setOnClickListener { delegate?.onTopVotedClick() }
    }

    private val btnStations = materialButton.apply {
        text = "Stations"
    }

    private val searchName = textInputLayout.apply {
        hint = "name hint"
        boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
        val cornerRadius = 12.0f
        setBoxCornerRadii(cornerRadius, cornerRadius, cornerRadius, cornerRadius)
        addView(TextInputEditText(this.context))
    }

    private val searchTag = textInputLayout.apply {
        hint = "tag hint"
        boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
        val cornerRadius = 12.0f
        setBoxCornerRadii(cornerRadius, cornerRadius, cornerRadius, cornerRadius)
        addView(TextInputEditText(this.context))
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

        val xPadding = { if (!serverList.isSelected) 8.xdip else 16.xdip }

        serverList.layoutBy(
            x = leftTo { parent.left() + xPadding() }.rightTo { parent.right() - xPadding() },
            y = topTo {
                when {
                    serverList.isSelected -> parent.top()
                    else -> searchTag.bottom()
                }
            }
        )

        serverList.setOnClickListener(::serverListClick)
    }

    override fun setServerAdapter(adapter: ServerListAdapter) {
        serverList.recycler.adapter = adapter
    }

    override fun serverListCollapse() {
        serverListClick(serverList)
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
        serverList.active.text = activerServer?.urlString ?: "No activer server..."
    }

    private fun serverListClick(view: View) {
        TransitionManager.beginDelayedTransition(this, ChangeBounds()
            .setInterpolator(LinearInterpolator())
            .setDuration(200)
        )
        serverList.isSelected = !serverList.isSelected
        requestLayout()
    }

    private val materialButton: MaterialButton
        get() = MaterialButton(
            ContextThemeWrapper(context, appTheme.btns.text.style),
            null,
            appTheme.btns.text.attr
        )

    private val textInputLayout: TextInputLayout
        get() = TextInputLayout(
            ContextThemeWrapper(context, appTheme.textInput.layout.style),
            null,
            appTheme.textInput.layout.attr
        )
}
