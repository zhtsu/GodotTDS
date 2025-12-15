<img src="example/icon.svg" width="128" height="128">

# Godot TDS

Godot plugin for supporting TapTap Developer Services

在 Godot 中使用 TapTap 开发者服务

关于新版 TapSDK 的详细信息：https://developer.taptap.cn/docs/sdk/access/v3-to-v4/

**注意事项：4.4 版本插件开发完成后尚未经过任何测试（2025.05.06）**

**[4.2.2 版本插件](https://github.com/zhtsu/GodotTDS/tree/4.2.2)**

# Version

- OpenJDK 17.0.11
- TapSDK Android 4.5.5
- TapADN Android SDK 3.16.3.45
- Godot 4.4

# Feature

### TapSDK

- [x] 登录
- [x] 合规认证
- [x] 内嵌动态
- [ ] ~~悬浮窗~~（v4 版本 TapSDK 暂不支持）
- [x] 成就系统
- [x] 礼包系统
- [x] 排行榜（v4 版本文档中没有对应示例，但是老版本接口没有被删除，谨慎使用）
- [ ] ~~云存档~~（v4 版本 TapSDK 暂不支持）
- [ ] ~~深度链接~~（不支持）

### TapAD

- [x] 开屏广告
- [x] 激励广告
- [x] 横幅广告（目前只支持竖屏显示）
- [x] 信息流广告（目前只支持模板渲染）
- [x] 插屏广告

# How to use

[B站视频讲解]()

example 目录下存放的是用于展示插件功能的 Godot 项目

GodotTDS 目录下存放的是用于生成***安卓插件***的安卓项目

# Thanks

[B站岩岩大佬](https://space.bilibili.com/55245483)

[B站咲夜大佬](https://space.bilibili.com/2706229)

[PukkyCoopie](https://github.com/PukkyCoopie): [#3](https://github.com/zhtsu/GodotTDS/pull/3)
