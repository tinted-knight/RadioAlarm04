# Inprogress

## Project structure and key technologies

- Kotlin + Coroutines + Flow
- No XML layouts (almost). [Contour layout](https://github.com/cashapp/contour) is used. There are two XML's for the only two Activities in the project. I promise to get rid of them somewhen. Aaand there are two more for ExoPlayer controls 🐱 Aaand there is one more for Notification 🐶
- No feature-modules. Project is small so there is no need. Only three modules for "Clean-way": `app` (aka presentation), `data` and `domain`. And `alarmtheme` for ui-related components like styles, colors and drawables.
- Project is mostly an experiment and playground for `ContourLayout`, `Kotlin Flow` and `SQLDelight` and there are inconsistencies in some points. Some unnecessary interfaces, some strange naming.
- Release build configured **temporarily** to be signed with debug key
  - Release build works fine, just one proguard rule used to keep entities from domain module

## Q&A

Q: Why project's name is RadioAlarm04?<br>
A: Previous four fell in battle.

Q: Why no XML's? Do you hate it so much?<br>
A: No, I do not. It's an experiment. And I consider it succesfull. ContourLayout works great, no bugs. Definitely need more convenient way to create views in code. Should check [Splitties](https://github.com/LouisCAD/Splitties).

## Contacts

[Telegram](https://t.me/pinq_punq)

[LinkedIn](https://www.linkedin.com/in/faserschreiber)

## Screenshots and videos


#### Light theme
Start screen              | Wake up           | Favorites          | Radio browser  |
:------------------------:|:-----------------:|:------------------:|:--------------:|
![](https://user-images.githubusercontent.com/25702530/113482705-8a431200-94a8-11eb-9737-e7d67f9b9903.png)|![](https://user-images.githubusercontent.com/25702530/123433363-0e062980-d5d4-11eb-86d7-7e1a25bc1175.png)|![](https://user-images.githubusercontent.com/25702530/113482756-c1192800-94a8-11eb-98ca-2d975d9548d1.png)|![](https://user-images.githubusercontent.com/25702530/113482918-7e0b8480-94a9-11eb-92fa-d9bc2ce2aed4.png)

#### Dark theme
Start screen              | Wake up           | Favorites          | Radio browser|
:------------------------:|:-----------------:|:------------------:|:------------:|
![](https://user-images.githubusercontent.com/25702530/113483170-bf506400-94aa-11eb-8477-3d4843abbe6a.png)|![](https://user-images.githubusercontent.com/25702530/123433363-0e062980-d5d4-11eb-86d7-7e1a25bc1175.png)|![](https://user-images.githubusercontent.com/25702530/123434602-58d47100-d5d5-11eb-9bb4-2dd05721903b.gif)|![](https://user-images.githubusercontent.com/25702530/113483228-08a0b380-94ab-11eb-88f1-e162478bdc22.png)|

### Add alarm

<img src="https://user-images.githubusercontent.com/25702530/113482436-7054ff80-94a7-11eb-9072-78e196c99e5d.gif" width="300">

### Search in list

<img src="https://user-images.githubusercontent.com/25702530/113482625-2882a800-94a8-11eb-838e-11520b944532.gif" width="300">

## Copyright and thanks

### Server-side API
Awesome [Radio browser API](https://www.radio-browser.info)([github](https://github.com/segler-alex/radiobrowser-api-rust)) by [Alex](https://github.com/segler-alex)
### Images
- Question mark icon from [Material Design Icons](https://materialdesignicons.com) by [Templarian](https://twitter.com/Templarian)
- Launcher icon drawn by myself 😎
### Libraries
- DI: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- Layout in Kotlin: [Contour layout](https://github.com/cashapp/contour)
- Media player: [ExoPlayer](https://github.com/google/ExoPlayer)
- Wake me up!: [Slide to act](https://github.com/cortinico/slidetoact)
- Network: [Retrofit](https://github.com/square/retrofit)
- Logging: [Timber](https://github.com/JakeWharton/timber)
- [LeakCanary](https://github.com/square/leakcanary)
- Image loading: [Glide](https://github.com/bumptech/glide)
- Database: [SQLDelight](https://github.com/cashapp/sqldelight)
- Lots of 'androidx' libraries [Android Jetpack](https://developer.android.com/jetpack/androidx)
