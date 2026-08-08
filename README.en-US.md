

<img src="example/icon.svg" width="128" height="128">

# Godot TDS

Godot plugin for supporting TapTap Developer Services

Use TapTap Developer Services in Godot

# Environment

- OpenJDK 17.0.11
- TapSDK Android 4.9.2
- Dirichlet Ad SDK 4.2.0.1
- Godot 4.5.1

# Feature

### TapSDK

- [x] Login (Completed)
- [x] Compliance Authentication (Completed)
- [x] In-game Feed (Completed)
- [x] Achievement System (Completed)
- [x] Package System (Completed)
- [x] Leaderboard (Completed)
  - The API for retrieving nearby player rankings currently has issues and cannot be used.
- [x] Cloud Save (Completed)
  - Currently experiencing noticeable lag when loading cloud save thumbnails in the test app.

### TapADN

> Note:
> 
> TapADN is being fully migrated to the new Dirichlet platform: [https://www.dirichlet.cn/](https://www.dirichlet.cn/)
> 
> To use TapADN services with personal credentials, you must select to import entity information from TapTap during account creation.

- [x] Splash Ad (Completed)
- [x] Rewarded Video Ad (Completed)
- [x] Banner Ad (Completed)
- [x] Interstitial Ad (Completed)
- [x] Feed Ad (Completed)

# How to use

The `example` directory contains a Godot project demonstrating the plugin's functionality.

The `GodotTDS` directory contains an Android project used to build the ***Android plugin***.

# Thanks

[Bilibili: 岩岩](https://space.bilibili.com/55245483)

[Bilibili: 咲夜](https://space.bilibili.com/2706229)

[PukkyCoopie:](https://github.com/PukkyCoopie) [#3](https://github.com/zhtsu/GodotTDS/pull/3)
