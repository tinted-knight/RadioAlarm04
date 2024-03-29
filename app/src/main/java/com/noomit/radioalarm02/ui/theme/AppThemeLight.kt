package com.noomit.radioalarm02.ui.theme

import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.noomit.alarmtheme.R.attr as A
import com.noomit.alarmtheme.R.color as C
import com.noomit.alarmtheme.R.drawable as D
import com.noomit.alarmtheme.R.style as S

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
  val linkColor: Int
  val btnClose: ViewStyle
  val btnFavorite: ViewStyle
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
  val background: Int
}

interface ThemeHelp {
  val browseIcon: Int
  val deleteIcon: Int
  val melodyIcon: Int
  val iconAttr: Int
  val dividerAttr: Int
  val fabStyleId: Int
  val fabSize: Int
  val fabSizeSmall: Int
  val bgColor: Int
  val text: Int
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
    override val linkColor = C.clNowPlayingLink
    override val btnClose = ViewStyle(S.LightTheme_NowPlayingClose, A.nowplaying_close)
    override val btnFavorite = ViewStyle(S.LightTheme_NowPlayingFavorite, A.nowplaying_favorite)
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
    override val background = C.clBackground
  },
  val helpView: ThemeHelp = object : ThemeHelp {
    override val browseIcon = D.ic_search_24
    override val deleteIcon = D.ic_delete_24
    override val melodyIcon = D.ic_add_alarm_24
    override val iconAttr = A.help_icon
    override val dividerAttr = A.help_divider
    override val fabStyleId = A.help_fab
    override val fabSize = 56
    override val fabSizeSmall = 48
    override val bgColor = C.helpBackground
    override val text = A.help_text
  },
)
