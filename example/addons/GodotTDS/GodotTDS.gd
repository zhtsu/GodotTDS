extends Node


signal on_login_return(code : int, msg : String)
signal on_anti_addiction_return(code : int, msg : String)
signal on_tap_moment_return(code : int, msg : String)
signal on_achievement_return(code : int, msg : String)
signal on_gift_return(code : int, msg : String)
signal on_leaderboard_return(code : int, msg : String)


var _plugin_name : String = "GodotTdsPlugin"
var _plugin_singleton : Object

const Orientation_Default : int = 0
const Orientation_Landscape : int = 1
const Orientation_Portrait : int = 2
const Orientation_Sensor : int = 3


func _ready() -> void:
	if Engine.has_singleton(_plugin_name):
		_plugin_singleton = Engine.get_singleton(_plugin_name)
		# Configure your own client info here
		_plugin_singleton.init(
			"qj7nkppf3iltbsyk4b",
			"Ybw1yeEmPXCnbEu29oM1ffb5IKZAsY9bDKXHFQ1d",
			"https://server.zhtsu.cn")
			
		_plugin_singleton.connect("onLogInReturn", _dont_call_on_login_return)
		_plugin_singleton.connect("onAntiAddictionReturn", _dont_call_on_anti_addiction_return)
		_plugin_singleton.connect("onTapMomentReturn", _dont_call_on_anti_addiction_return)
		_plugin_singleton.connect("OnAchievementReturn", _dont_call_on_achievement_return)
		_plugin_singleton.connect("OnGiftReturn", _dont_call_on_gift_return)
		_plugin_singleton.connect("OnLeaderboardReturn", _dont_call_on_leaderboard_return)
		
		
# 使用内建账户登录
func login() -> void:
	_call_android_function("logIn")
		
		
# 退出登录
func logout() -> void:
	_call_android_function("logOut")
		
		
# 防沉迷
func anti_addiction() -> void:
	_call_android_function("antiAddiction")
		
		
# 内嵌动态
func tap_moment(orientation : int = Orientation_Default) -> void:
	_call_android_function("tapMoment", [orientation])
	
	
# 得到当前登录用户的信息
func get_user_profile() -> Dictionary:
	var json_string : Variant = _call_android_function("getUserProfile")
	return {} if json_string == null else JSON.parse_string(json_string)
	
	
# 得到当前登录用户的 objectId
func get_user_object_id() -> String:
	var object_id : Variant = _call_android_function("getUserObjectId")
	return "" if object_id == null else object_id
		
		
# 设置悬浮窗是否可见
func set_entry_visible(visible : bool) -> void:
	_call_android_function("setEntryVisible", [visible])
	
	
# 设置信号响应时的弹窗是否可见
func set_show_tips_toast(show : bool) -> void:
	_call_android_function("setShowTipsToast", [show])
	
	
# 从 TapTap 服务器拉取所有的成就数据
# 这是一个异步操作，请绑定对应的信号并使用 get_network_all_achievement_list 方法获取返回的数据
func fetch_all_achievement_list() -> void:
	_call_android_function("fetchAllAchievementList")
	
	
# 得到本地存储所有的成就数据
func get_local_all_achievement_list() -> Array:
	var json_str : Variant = _call_android_function("getLocalAllAchievementList")
	return _json_to_array(json_str)
	
	
# 得到拉取到的所有的成就数据
# 请配合 fetch_all_achievement_list 函数使用
func get_network_all_achievement_list() -> Array:
	var json_str : Variant = _call_android_function("getNetworkAllAchievementList")
	return _json_to_array(json_str)
	
	
# 打开内置的成就页面
func show_achievement_page() -> void:
	_call_android_function("showAchievementPage")
	
	
# 达成对应的单步成就
func reach_achievement(display_id : String) -> void:
	_call_android_function("reachAchievement", [display_id])
	
	
# 增加分步成就的步数（累加）
func grow_achievement_steps(display_id : String, steps : int) -> void:
	_call_android_function("growAchievementSteps", [display_id, steps])
	
	
# 设置分步成就的步数（直接设置）
func make_achievement_steps(display_id : String, steps : int) -> void:
	_call_android_function("makeAchievementSteps", [display_id, steps])
	
	
# 设置解锁新成就时是否弹窗
func set_show_achievement_toast(show : bool) -> void:
	_call_android_function("setShowAchievementToast", [show])
	
	
# 提交礼包兑换码（无服务器兑换）
func submit_gift_code(gift_code : String) -> void:
	_call_android_function("submitGiftCode", [gift_code])
	
	
# 向目标排行榜中提交数据
func submit_leaderboard_score(leaderboard_name : String, score : int) -> void:
	_call_android_function("submitLeaderboardScore", [leaderboard_name, score])
	
	
# 获取目标排行榜中指定区间的排名
# 这是一个异步操作，请绑定对应的信号并从返回的 msg 中获取数据
func access_leaderboard_section_rankings(leaderboard_name : String, start : int, end : int) -> void:
	_call_android_function("accessLeaderboardSectionRankings", [leaderboard_name, start, end])
	

# 获取目标排行榜中用户周围指定个数的排名（包括用户自己）
# 如果不指定个数（count 使用默认数值 1），则代表只获取当前用户的排名
# 这是一个异步操作，请绑定对应的信号并从返回的 msg 中获取数据
func access_leaderboard_user_around_rankings(leaderboard_name : String, count : int = 1) -> void:
	_call_android_function("accessLeaderboardUserAroundRankings", [leaderboard_name, count])
	
	
# Dont call these functions from outside
func _dont_call_on_login_return(code : int, msg : String) -> void:
	on_login_return.emit(code, msg)
	
	
func _dont_call_on_anti_addiction_return(code : int, msg : String) -> void:
	on_anti_addiction_return.emit(code, msg)
	
	
func _dont_call_on_tap_moment_return(code : int, msg : String) -> void:
	on_tap_moment_return.emit(code, msg)
	
	
func _dont_call_on_achievement_return(code : int, msg : String) -> void:
	on_achievement_return.emit(code, msg)
	
	
func _dont_call_on_gift_return(code : int, msg : String) -> void:
	on_gift_return.emit(code, msg)
	
	
func _dont_call_on_leaderboard_return(code : int, msg : String) -> void:
	on_leaderboard_return.emit(code, msg)
	
	
func _json_to_array(json_string : Variant) -> Array:
	if json_string == null:
		return []
	var dict : Dictionary = JSON.parse_string(json_string)
	if dict.has("list"):
		return dict["list"]
	else:
		return []
		
		
func _call_android_function(android_func : String, args : Array = []) -> Variant:
	if OS.has_feature("android"):
		if args.size() == 0:
			return _plugin_singleton.call(android_func)
		elif args.size() == 1:
			return _plugin_singleton.call(android_func, args[0])
		elif args.size() == 2:
			return _plugin_singleton.call(android_func, args[0], args[1])
		elif args.size() == 3:
			return _plugin_singleton.call(android_func, args[0], args[1], args[2])
		else:
			return null
	else:
		push_warning("Only works on Android")
		return null
