package com.noomit.radioalarm02.ui.theme

import android.graphics.Color

interface IViewTheme {
    val bgColor: Int
    val textColor: Int
}

val appTheme = AppThemeLight()

data class AppThemeLight(
    val nowPlaying: IViewTheme = object : IViewTheme {
        override val bgColor = Color.parseColor("#ffFFFFFA")
        override val textColor: Int = Color.parseColor("#FF414141")
    },
)