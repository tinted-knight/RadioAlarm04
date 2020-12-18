package com.noomit.radioalarm02.ui.theme

import android.graphics.Color
import com.noomit.radioalarm02.R

interface ThemeNowPlaying {
    val bgColor: Int
    val textColor: Int
    val favoriteStyleId: Int
    val iconFavorite: Int
    val iconNotFavorite: Int
}

interface ThemeServerList {
    val bgColor: Int
}

interface ThemeAlarmItem {
    val bgColor: Int
    val favoriteStyleId: Int
    val iconFavorite: Int
    val dayOfWeekStyle: Int
}

val appTheme = AppThemeLight()

data class AppThemeLight(
    val nowPlaying: ThemeNowPlaying = object : ThemeNowPlaying {
        override val bgColor = Color.parseColor("#ffFFFFFA")
        override val textColor = Color.parseColor("#FF414141")
        override val favoriteStyleId = R.style.LightTheme_ActionButton
        override val iconFavorite = R.drawable.ic_favorite_24
        override val iconNotFavorite = R.drawable.ic_favorite_border_24
    },
    val serverList: ThemeServerList = object : ThemeServerList {
        override val bgColor = Color.parseColor("#ffFFFFFF")
    },
    val alarmItem : ThemeAlarmItem = object : ThemeAlarmItem {
        override val bgColor = Color.parseColor("#ffFFFFFF")
        override val favoriteStyleId = R.style.LightTheme_ActionButton
        override val iconFavorite = R.drawable.ic_delete_24
        override val dayOfWeekStyle = R.style.LightTheme_DayOfWeek
    }
)
