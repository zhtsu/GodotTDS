<img src="example/icon.svg" width="128" height="128">

# Godot TDS

Godot plugin for supporting TapTap Developer Services

在 Godot 中使用 TapTap 开发者服务

# Environment

- OpenJDK 17.0.11
- TapSDK Android 4.9.2
  - 最新版本的云存储相关依赖存在问题，因此使用 4.9.1 版本
- TapADN Android SDK 3.16.3.45
- Godot 4.5.1

# Feature

### TapSDK

- [x] 登录（已完成）
- [x] 合规认证（已完成）
- [x] 内嵌动态（已完成）
- [x] 成就系统（已完成）
- [x] 礼包系统（已完成）
- [x] 排行榜（已完成）
  - 获取玩家周围排名数据的接口目前存在问题，无法使用
- [x] 云存档（已完成）
  - 目前在测试应用中获取云存档封面时存在明显卡顿
- [ ] ~~深度链接（目前不在开发计划中）~~

### TapADN

**注意事项：TapADN 即将全面迁移到新的 Dirichlet 平台，需要认证公司资质才能使用对应服务**

新平台地址：[https://www.dirichlet.cn/](https://www.dirichlet.cn/)

- [ ] ~~开屏广告（目前不在开发计划中）~~
- [ ] ~~激励广告（目前不在开发计划中）~~
- [ ] ~~横幅广告（目前不在开发计划中）~~
- [ ] ~~信息流广告（目前不在开发计划中）~~
- [ ] ~~插屏广告（目前不在开发计划中）~~

# How to use

[B站视频讲解]()

example 目录下存放的是用于展示插件功能的 Godot 项目

GodotTDS 目录下存放的是用于生成***安卓插件***的安卓项目

# Thanks

[B站岩岩大佬](https://space.bilibili.com/55245483)

[B站咲夜大佬](https://space.bilibili.com/2706229)

[PukkyCoopie:](https://github.com/PukkyCoopie) [#3](https://github.com/zhtsu/GodotTDS/pull/3)
