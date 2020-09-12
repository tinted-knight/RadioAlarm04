package com.noomit.radioalarm02.model

val days = listOf(1, 2, 4, 8, 16, 32, 64)

fun zipDaysInBits(daysOfWeek: List<Int>): Int {
    var bits = 0
    daysOfWeek.forEach {
        bits = if (it == 1) {
            bits or days[6]
        } else {
            bits or days[it - 2]
        }
    }
    return bits
}

fun switchBitByDay(day: Int, zippedDays: Int): Int {
    return when (day) {
        1 -> zippedDays xor days[6]
        else -> zippedDays xor days[day - 2]
    }
}

fun Int.swithBitFor(day: Int) = when (day) {
    1 -> this xor days[6]
    else -> this xor days[day - 2]
}