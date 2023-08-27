#!/bin/zsh

./gradlew :app:assembleRelease

#adb shell pm uninstall com.noomit.radioalarm02
#adb install app-release.apk
