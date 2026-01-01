extends Node

# TapTapSDK配置
const client_id: String = ""
const client_token: String = ""
const server_url: String = ""
# TapTapADN配置
const media_id: int = 0
const media_name: String = ""
const media_key: String = ""
# 是否开启TapSDK和TapADNSDK的调试日志输出
# 正式版发布时记得关闭掉调试日志输出
const log_enabled: bool = false
# 是否显示切换账号按钮
const show_switch_account_enabled: bool = true
# 是否使用年龄段授权
const use_age_range_enabled: bool = false
# 成就达成时SDK是否需要展示一个气泡弹窗提示
const achievement_toast_enabled: bool = true
# 屏幕方向，横屏传 0，竖屏传 1
const screen_orientation: int = 1
# 决定是否在加载广告SDK时申请位置信息权限和电话权限
# 详情参考 https://github.com/zhtsu/GodotTDS/issues/4
const request_permission_if_necessary_enabled: bool = false
