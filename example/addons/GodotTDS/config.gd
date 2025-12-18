extends Node

# TapTapSDK配置
const client_id : String = ""
const client_token : String = ""
const server_url : String = ""
# TapTapADN配置
const media_id : int = -1
const media_name : String = ""
const media_key : String = ""
# 是否开启SDK日志
const log_enabled : bool = false
# 是否显示切换账号按钮
const show_switch_account_enabled : bool = true
# 是否使用头像昵称授权
const public_profile_enabled : bool = false
# 是否使用年龄段授权
const use_age_range_enabled : bool = false
# 是否使用好友关系授权
const user_friends_enabled : bool = false
# 成就达成时SDK是否需要展示一个气泡弹窗提示
const achievement_toast_enabled: bool = true
# 屏幕方向，横屏传 0，竖屏传 1
const screen_orientation: int = 0
# 决定是否在加载广告SDK时申请位置信息权限和电话权限
# 详情参考 https://github.com/zhtsu/GodotTDS/issues/4
const request_permission_if_necessary_enabled : bool = false
