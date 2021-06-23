package com.noomit.domain.alarm_manager.alarm_composer

import com.noomit.domain.entities.AlarmModel

interface AlarmComposer {
    fun composeDbEntity(hour: Int, minute: Int): AlarmModel
    fun reCompose(alarm: AlarmModel, dayOfWeek: Int): AlarmModel
    fun reComposeFired(alarm: AlarmModel): AlarmModel
}

fun AlarmComposer(): AlarmComposer = AlarmComposerImpl()
