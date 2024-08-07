extends Node


# 登录相关操作的信号
signal on_login_return(code : int, msg : String)
# 防沉迷相关操作的信号
signal on_anti_addiction_return(code : int, msg : String)
# 内嵌动态相关操作的信号
signal on_tap_moment_return(code : int, msg : String)
# 成就相关操作的信号
signal on_achievement_return(code : int, msg : String)
# 礼包相关操作的信号
signal on_gift_return(code : int, msg : String)
# 排行榜相关操作的信号
signal on_leaderboard_return(code : int, msg : String)
# 云存档相关操作的信号
signal on_game_save_return(code : int, msg : String)


var _plugin_name : String = "GodotTdsPlugin"
var _plugin_singleton : Object

const Orientation_Default : int = 0
const Orientation_Landscape : int = 1
const Orientation_Portrait : int = 2
const Orientation_Sensor : int = 3

class GameSaveData:
	var save_name : String
	var summary : String
	# played_time 的单位为毫秒
	var played_time : int
	var progress_value : int
	# 存档封面图片的路径
	var cover_path : String
	# 存档文件的路径
	# 确保存档文件不会被作为资源打包，否则无法正常获取
	# （存档文件一般是存放在 user:// 目录下的文件）
	var game_file_path : String
	# modified_at 的值应该设置为对应 Date 的时间戳
	var modified_at : int


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
		_plugin_singleton.connect("OnGameSaveReturn", _dont_call_on_game_save_return)
		
		
# 调试用
# 用来在安卓平台输出日志
func push_log(msg : String, error : bool = false) -> void:
	_call_android_function("PushLog", [msg, error])
		
		
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
# 这是一个异步操作，请处理对应的信号并使用 get_network_all_achievement_list 方法获取返回数据
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
# 这是一个异步操作，请处理对应的信号以获取成就更新结果
func reach_achievement(display_id : String) -> void:
	_call_android_function("reachAchievement", [display_id])
	
	
# 增加分步成就的步数（累加）
# 这是一个异步操作，请处理对应的信号以获取成就更新结果
func grow_achievement_steps(display_id : String, steps : int) -> void:
	_call_android_function("growAchievementSteps", [display_id, steps])
	
	
# 设置分步成就的步数（直接设置）
# 这是一个异步操作，请处理对应的信号以获取成就更新结果
func make_achievement_steps(display_id : String, steps : int) -> void:
	_call_android_function("makeAchievementSteps", [display_id, steps])
	
	
# 设置解锁新成就时是否弹窗
func set_show_achievement_toast(show : bool) -> void:
	_call_android_function("setShowAchievementToast", [show])
	
	
# 提交礼包兑换码（无服务器兑换）
# 这是一个异步操作，请处理对应的信号以获取兑换结果
func submit_gift_code(gift_code : String) -> void:
	_call_android_function("submitGiftCode", [gift_code])
	
	
# 向目标排行榜中提交数据
# 这是一个异步操作，请处理对应的信号以获取提交结果
func submit_leaderboard_score(leaderboard_name : String, score : int) -> void:
	_call_android_function("submitLeaderboardScore", [leaderboard_name, score])
	
	
# 获取目标排行榜中指定区间的排名
# 这是一个异步操作，请处理对应的信号以获取返回数据
func access_leaderboard_section_rankings(leaderboard_name : String, start : int, end : int) -> void:
	_call_android_function("accessLeaderboardSectionRankings", [leaderboard_name, start, end])
	

# 获取目标排行榜中用户周围指定个数的排名（包括用户自己）
# 如果不指定个数（count 使用默认数值 1），则代表只获取当前用户的排名
# 这是一个异步操作，请处理对应的信号以获取返回数据
func access_leaderboard_user_around_rankings(leaderboard_name : String, count : int = 1) -> void:
	_call_android_function("accessLeaderboardUserAroundRankings", [leaderboard_name, count])
	
	
# 将游戏数据提交到云存档
# 这是一个异步操作，请处理对应的信号以获取提交结果
func submit_game_save(data : GameSaveData) -> void:
	var cover_path = ProjectSettings.globalize_path(data.cover_path)
	var game_file_path = ProjectSettings.globalize_path(data.game_file_path)
	_call_android_function("submitGameSave", [
		data.save_name, data.summary, data.played_time,
		data.progress_value, data.cover_path, game_file_path, data.modified_at
	])
	
	
# 获取当前登录用户的存档数据
# 这是一个异步操作，请处理对应的信号以获取返回数据
func access_game_saves() -> void:
	_call_android_function("accessGameSaves")
	
	
# 删除指定 Id 的存档
# 存档的 Id 包含在通过 access_game_save 函数返回的数据中
# 这是一个异步操作，请处理对应的信号以获取删除结果
func delete_game_save(game_save_id) -> void:
	_call_android_function("deleteGameSave", [game_save_id])
	
	
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
	
	
func _dont_call_on_game_save_return(code : int, msg : String) -> void:
	on_game_save_return.emit(code, msg)
	
	
func _json_to_array(json_string : Variant) -> Array:
	if json_string == null:
		return []
	var dict : Dictionary = JSON.parse_string(json_string)
	if dict.has("list"):
		return dict["list"]
	else:
		return []
		
		
func _save_image_get_path(image_path : String) -> String:
	var tex : Texture2D = load(image_path) as Texture2D
	tex.get_image().flip_y()
	
	var unique_id : String = str(Time.get_unix_time_from_system()) + "_" + str(hash(tex.get_rid().get_id()))
	var user_path : String = "user://" + unique_id + ".png"
	if (FileAccess.file_exists(user_path)):
		return ProjectSettings.globalize_path(user_path)
		
	var dir_path : String = user_path.get_base_dir()
	var dir : DirAccess = DirAccess.open(dir_path)
	if not dir.dir_exists(dir_path):
		var error : Error = dir.make_dir_recursive(dir_path)
		if error != OK:
			push_log(str(error), true)
			return ""
			
	var g_user_path : String = ProjectSettings.globalize_path(user_path)
	var error : Error = tex.get_image().save_png(user_path)
	if error != OK:
		var err_msg = "Failed to saving the png image! Error: " + str(error)
		push_log(err_msg, true)
		push_log("Error file: " + g_user_path, true)
	else:
		push_log("Save the png image successful: " + g_user_path)
		
	return g_user_path
		
		
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
		elif args.size() == 7:
			return _plugin_singleton.call(android_func,
				args[0], args[1], args[2], args[3], args[4], args[5], args[6])
		else:
			return null
	else:
		push_warning("Only works on Android")
		return null
