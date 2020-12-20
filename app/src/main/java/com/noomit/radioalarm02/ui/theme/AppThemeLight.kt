package com.noomit.radioalarm02.ui.theme

import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.noomit.radioalarm02.R.attr as A
import com.noomit.radioalarm02.R.color as C
import com.noomit.radioalarm02.R.drawable as D
import com.noomit.radioalarm02.R.style as S

data class ViewStyle(
    @StyleRes val style: Int,
    @AttrRes val attr: Int,
)

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
    val dayColorInactive: Int
    val dayColorActive: Int
    val timeTextStyle: Int
}

interface ThemeButton {
    val text: ViewStyle
    val outline: ViewStyle
}

val appTheme = AppThemeLight()

data class AppThemeLight(
    val nowPlaying: ThemeNowPlaying = object : ThemeNowPlaying {
        override val bgColor = C.clCardBackground
        override val textColor = Color.parseColor("#FF414141")
        override val favoriteStyleId = S.LightTheme_ActionButton
        override val iconFavorite = D.ic_favorite_24
        override val iconNotFavorite = D.ic_favorite_border_24
    },
    val serverList: ThemeServerList = object : ThemeServerList {
        override val bgColor = C.clCardBackground
    },
    val alarmItem: ThemeAlarmItem = object : ThemeAlarmItem {
        override val bgColor = C.clCardBackground
        override val favoriteStyleId = S.LightTheme_ActionButton
        override val iconFavorite = D.ic_delete_24
        override val dayOfWeekStyle = S.LightTheme_DayOfWeek
        override val dayColorInactive = C.colorDayTextInactive
        override val dayColorActive = C.colorDayTextActive
        override val timeTextStyle = S.LightTheme_TextTime
    },
    val btns: ThemeButton = object : ThemeButton {
        override val text = ViewStyle(S.LightTheme_MaterialButtonText, A.buttonText)
        override val outline = ViewStyle(S.LightTheme_ButtonOutline, A.buttonOutline)
    },
)
