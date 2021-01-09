package com.noomit.radioalarm02.ui.radio_browser.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.example.radiobrowser.ServerInfo
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.ui.animations.PushOnPressAnimator
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
        text = "Language list"
        setOnClickListener { delegate?.onLanguageClick() }
    }

    private val btnTags = materialButton.apply {
        text = "Tag list"
        setOnClickListener { delegate?.onTagClick() }
    }

    private val btnTopVoted = materialButton.apply {
        text = "Top Voted Stations"
        setOnClickListener { delegate?.onTopVotedClick() }
    }

    private val searchLabel = TextView(context).apply {
        text = "Try to search by station name and(or) tag:"
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

    private val btnSearch = MaterialButton(context).apply {
        text = "Search"
    }

    private val serverList = ServerListView(context)

    private val loadingIndicator = ProgressBar(
        ContextThemeWrapper(context, R.style.LightTheme_ProgressBar),
        null,
        R.attr.progressBarColors
    )

    private val loadingBackground = View(context).apply {
        setBackgroundColor(Color.parseColor("#12000000"))
    }

    init {
        contourHeightWrapContent()
        setPadding(16.dip, 0.dip, 16.dip, 0.dip)

        loadingBackground.layoutBy(
            leftTo { loadingIndicator.left() - 16.xdip }.rightTo { loadingIndicator.right() + 16.xdip },
            topTo { loadingIndicator.top() - 16.ydip }.bottomTo { loadingIndicator.bottom() + 16.ydip }
        )
        loadingIndicator.layoutBy(
            centerHorizontallyTo { parent.centerX() }.widthOf { 60.xdip },
            topTo { parent.top() + 100.ydip }.heightOf { 60.ydip }
        )
        btnLanguages.layoutBy(
            x = matchParentX(),
            y = topTo { parent.top() }
        )
        btnTags.layoutBy(
            x = matchParentX(),
            y = topTo { btnLanguages.bottom() }
        )
        btnTopVoted.layoutBy(
            x = matchParentX(),
            y = topTo { btnTags.bottom() }
        )
        searchLabel.layoutBy(
            x = matchParentX(),
            topTo { btnTopVoted.bottom() }
        )
        searchName.layoutBy(
            x = matchParentX(),
            y = topTo { searchLabel.bottom() }
        )
        searchTag.layoutBy(
            x = matchParentX(),
            y = topTo { searchName.bottom() }
        )
        btnSearch.layoutBy(
            x = rightTo { parent.right() },
            y = topTo { searchTag.bottom() }
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
        loadingIndicator.isVisible = true
        loadingBackground.isVisible = false

        serverList.recycler.isVisible = false
        btnLanguages.isEnabled = false
        btnLanguages.isVisible = false

        btnTags.isEnabled = false
        btnTags.isVisible = false

        btnTopVoted.isEnabled = false
        btnTopVoted.isVisible = false

        searchLabel.isVisible = false
        searchName.isVisible = false
        searchTag.isVisible = false
        btnSearch.isVisible = false

        serverList.isVisible = false
    }

    override fun update(content: List<ServerInfo>) {
        loadingIndicator.isVisible = false

        (serverList.recycler.adapter as ServerListAdapter).submitList(content)
        serverList.recycler.isVisible = true
//        serverList.isVisible = true

        btnLanguages.isEnabled = true
        btnLanguages.isVisible = true

        btnTags.isEnabled = true
        btnTags.isVisible = true

        btnTopVoted.isEnabled = true
        btnTopVoted.isVisible = true

        searchLabel.isVisible = true
        searchName.isVisible = true
        searchTag.isVisible = true
        btnSearch.isVisible = true
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
        ).apply {
            stateListAnimator = PushOnPressAnimator(this)
        }

    private val textInputLayout: TextInputLayout
        get() = TextInputLayout(
            ContextThemeWrapper(context, appTheme.textInput.layout.style),
            null,
            appTheme.textInput.layout.attr
        )
}
