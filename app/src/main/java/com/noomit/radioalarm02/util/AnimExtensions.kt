package com.noomit.radioalarm02.util

import android.graphics.Color

inline val Int.red: Int
  get() = Color.red(this)

inline val Int.green: Int
  get() = Color.green(this)

inline val Int.blue: Int
  get() = Color.blue(this)

infix fun Int.withAlpha(value: Int) = Color.argb(value, this.red, this.green, this.blue)
