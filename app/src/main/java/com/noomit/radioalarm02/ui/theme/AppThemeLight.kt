package com.noomit.radioalarm02.ui.theme

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

interface ThemeCommon {
    val clPrimary: Int
}

interface ThemeNowPlaying {
    val bgColor: Int
    val favoriteStyleId: Int
    val iconFavorite: Int
    val iconNotFavorite: Int
    val iconHomepage: Int
    val stationCount: ViewStyle
    val dimmColor: Int
    val tag: ViewStyle
    val titleStyle: ViewStyle
}

interface ThemeServerList {
    val bgColor: Int
}

interface ThemeAlarmItem {
    val bgColor: Int
    val actionButton: Int
    val iconDelete: Int
    val dayOfWeekStyle: Int
    val dayColorInactive: Int
    val dayColorActive: Int
    val timeTextStyle: Int
    val melodyText: ViewStyle
    val alarmIcon: ViewStyle
}

interface ThemeButton {
    val text: ViewStyle
    val outline: ViewStyle
    val bbarFav: ViewStyle
    val bbarBrowse: ViewStyle
    val bbarAddAlarm: ViewStyle
}

interface ThemeTextInput {
    val layout: ViewStyle
}

interface ThemeAlarmFire {
    val station: ViewStyle
    val time: ViewStyle
    val day: ViewStyle
}

val appTheme = AppThemeLight()

data class AppThemeLight(
    val common: ThemeCommon = object : ThemeCommon {
        override val clPrimary = C.clPrimary
    },
    val nowPlaying: ThemeNowPlaying = object : ThemeNowPlaying {
        override val bgColor = C.clCardBackground
        override val favoriteStyleId = S.LightTheme_ActionButton
        override val iconFavorite = D.ic_favorite_24
        override val iconNotFavorite = D.ic_favorite_border_24
        override val iconHomepage = D.ic_home_24
        override val stationCount = ViewStyle(S.LightTheme_TextStationCount, A.stationCountText)
        override val dimmColor = C.clNowplayingDimm
        override val tag = ViewStyle(S.LightTheme_StationTag, A.stationTag)
        override val titleStyle = ViewStyle(S.LightTheme_NowplayingTitle, A.nowplaying_title)
    },
    val serverList: ThemeServerList = object : ThemeServerList {
        override val bgColor = C.clCardBackground
    },
    val alarmItem: ThemeAlarmItem = object : ThemeAlarmItem {
        override val bgColor = C.clCardBackground
        override val actionButton = S.LightTheme_ActionButton
        override val iconDelete = D.ic_delete_24
        override val dayOfWeekStyle = S.LightTheme_DayOfWeek
        override val dayColorInactive = C.colorDayTextInactive
        override val dayColorActive = C.colorDayTextActive
        override val timeTextStyle = S.LightTheme_TextTime
        override val melodyText = ViewStyle(S.LightTheme_MelodyText, A.alarmCardMelody)
        override val alarmIcon = ViewStyle(S.LightTheme_AlarmIcon, A.alarmCardAlarmIcon)
    },
    val btns: ThemeButton = object : ThemeButton {
        override val text = ViewStyle(S.LightTheme_MaterialButtonText, A.buttonText)
        override val outline = ViewStyle(S.LightTheme_ButtonOutline, A.buttonOutline)
        override val bbarFav = ViewStyle(S.LightTheme_BBar_Favorites, A.buttonBarButtonFavorites)
        override val bbarBrowse = ViewStyle(S.LightTheme_BBar_Browse, A.buttonBarButtonBrowse)
        override val bbarAddAlarm = ViewStyle(S.LightTheme_BBar_Center, A.buttonBarButtonCenter)
    },
    val textInput: ThemeTextInput = object : ThemeTextInput {
        override val layout = ViewStyle(S.LightTheme_TextInputOutlined, A.textInputOutlined)
    },
    val alarmFire: ThemeAlarmFire = object : ThemeAlarmFire {
        override val station = ViewStyle(S.LightTheme_AlarmFireStation, A.textAlarmFireStation)
        override val time = ViewStyle(S.LightTheme_AlarmFireTime, A.textAlarmFireTime)
        override val day = ViewStyle(S.LightTheme_AlarmFireDay, A.textAlarmFireDay)
    },
)
