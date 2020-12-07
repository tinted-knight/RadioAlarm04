package com.noomit.radioalarm02.ui.theme

import android.graphics.Color
import com.noomit.radioalarm02.R

interface ThemeNowPlaying {
    val bgColor: Int
    val textColor: Int
    val favoriteStyleId: Int
}

val appTheme = AppThemeLight()

data class AppThemeLight(
    val nowPlaying: ThemeNowPlaying = object : ThemeNowPlaying {
        override val bgColor = Color.parseColor("#ffFFFFFA")
        override val textColor: Int = Color.parseColor("#FF414141")
        override val favoriteStyleId: Int = R.style.LightTheme_ActionButton
    },
)