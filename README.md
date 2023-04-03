# FastAir

[简体中文](README_zh-CN.md)

A Android APP design to transferring files and IM between two Android device.Using WIFIP2P and Socket API.
# Feature
- Local host-implement a toy http server which can be use transfer file between PC and Phone;
- Svelte Front End Project-simple project use to display files on the phone and download files from PC.

# What technology was used?
1. Custom View[LoadingView](app/src/main/java/com/mob/lee/fastair/view/LoadView.kt)
2. Custom CoordinatorLayout layout behavior[TranslationBehavior](app/src/main/java/com/mob/lee/fastair/view/TranslationBehavior.kt)
3. Simple Socket abstract and encapsulated[SocketService](app/src/main/java/com/mob/lee/fastair/io/SocketService.kt)
4. Kotlin Coroutines
5. Kotlin use case
6. Jetpack use case
7. Other interesting
8. Http implement
9. Svelte

# Download

[<img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png"
     alt="Get it on IzzyOnDroid"
     height="70">](https://apt.izzysoft.de/fdroid/index/apk/com.mob.lee.fastair)

Or get the latest APK from the [Releases Section](https://github.com/hongui/FastAir/releases/latest).

# Preview
## PC
<img width="50%" src="Screenshots/fronteng.png" alt="pc home" /><img width="50%" src="Screenshots/file-upload.png" alt="pc file upload" />

## Android
<img width="25%" src="Screenshots/discover.png" alt="discover page" /><img width="25%" src="Screenshots/list.png" alt="home list page" /><img width="25%" src="Screenshots/chat.png" alt="chat list page" /><img width="25%" src="Screenshots/file.png" alt="transmission page" />
