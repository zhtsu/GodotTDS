extends Node


const config = preload("res://addons/GodotTDS/config.gd")


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
# 当程序从深度链接启动时，此信号被触发
signal on_launch_from_deep_link(uri : String)
# 开屏广告相关的信号
signal on_splash_ad_return(code : int, msg : String)
# 视频激励广告相关的信号
signal on_reward_video_ad_return(code : int, msg : String)
# 横幅广告相关的信号
signal on_banner_ad_return(code : int, msg : String)
# 信息流广告相关的信号
signal on_feed_ad_return(code : int, msg : String)
# 插屏广告相关的信号
signal on_interstitial_ad_return(code : int, msg : String)


enum
{
	# 使用后台配置的默认朝向
	ORIENTATION_DEFAULT = 0,
	# 横屏
	ORIENTATION_LANDSCAPE = 1,
	# 竖屏
	ORIENTATION_PORTRAIT = 2,
	# 根据陀螺仪旋转
	ORIENTATION_SENSOR = 3
}

# 广告使用的布局
enum
{
	GRAVITY_BOTTOM = 0,
	GRAVITY_TOP = 1
}

class GameSaveData:
	var save_name : String
	var summary : String
	# played_time 的单位为毫秒
	var played_time : int
	var progress_value : int
	# 存档封面图片的路径
	var cover_path : String
	# 存档文件的路径
	var game_file_path : String
	# modified_at 的值应该设置为对应 Date 的时间戳
	var modified_at : int
	
class RewardVideoAdData:
	var space_id : int
	var reward_name : String
	var reward_amount : int
	var extra_info : String
	var game_user_id : String
	
	
var _plugin_name : String = "GodotTdsPlugin"
var _plugin_singleton : Variant = null


func _ready() -> void:
	if Engine.has_singleton(_plugin_name):
		_plugin_singleton = Engine.get_singleton(_plugin_name)
		_plugin_singleton.init(
			config.client_id, config.client_token, config.server_url,
			config.media_id, config.media_name, config.media_key
		)
			
		_plugin_singleton.connect("onLogInReturn", _dont_call_on_login_return)
		_plugin_singleton.connect("onAntiAddictionReturn", _dont_call_on_anti_addiction_return)
		_plugin_singleton.connect("onTapMomentReturn", _dont_call_on_anti_addiction_return)
		_plugin_singleton.connect("onAchievementReturn", _dont_call_on_achievement_return)
		_plugin_singleton.connect("onGiftReturn", _dont_call_on_gift_return)
		_plugin_singleton.connect("onLeaderboardReturn", _dont_call_on_leaderboard_return)
		_plugin_singleton.connect("onGameSaveReturn", _dont_call_on_game_save_return)
		_plugin_singleton.connect("onLaunchFromDeepLink", _dont_call_on_launch_from_deep_link)
		_plugin_singleton.connect("onSplashAdReturn", _dont_call_on_splash_ad_return)
		_plugin_singleton.connect("onRewardVideoAdReturn", _dont_call_on_reward_video_ad_return)
		_plugin_singleton.connect("onBannerAdReturn", _dont_call_on_banner_ad_return)
		_plugin_singleton.connect("onInterstitialAdReturn", _dont_call_on_interstitial_ad_return)
		_plugin_singleton.connect("onFeedAdReturn", _dont_call_on_feed_ad_return)
		
		
# 在安卓平台输出日志
func push_log(msg : String, error : bool = false) -> void:
	_call_android_function("pushLog", [msg, error])
	
	
# 获取安卓平台的缓存路径
func get_cache_dir_path() -> String:
	var cache_dir_path : Variant = _call_android_function("getCacheDirPath")
	return "" if cache_dir_path == null else cache_dir_path
		
		
# 使用内建账户登录
func login() -> void:
	_call_android_function("logIn")
		
		
# 退出登录
func logout() -> void:
	_call_android_function("logOut")
		
		
# 防沉迷
func anti_addiction() -> void:
	_call_android_function("antiAddiction")
		
		
# 打开内嵌动态
func tap_moment(orientation : int = ORIENTATION_DEFAULT) -> void:
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
# 这是一个异步操作，请处理对应的信号获取返回数据
func fetch_all_achievement_list() -> void:
	_call_android_function("fetchAllAchievementList")
	
	
# 得到本地存储存储的所有成就数据
# 本地的成就数据会在拉取服务端数据时进行同步
# 优先使用本地的成就数据
func get_local_all_achievement_list() -> Array:
	var json_str : Variant = _call_android_function("getLocalAllAchievementList")
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
func fetch_leaderboard_section_rankings(leaderboard_name : String, start : int, end : int) -> void:
	_call_android_function("fetchLeaderboardSectionRankings", [leaderboard_name, start, end])
	

# 获取目标排行榜中用户周围指定个数的排名（包括用户自己）
# 如果不指定个数（count 使用默认数值 1），则代表只获取当前用户的排名
# 这是一个异步操作，请处理对应的信号以获取返回数据
func fetch_leaderboard_user_around_rankings(leaderboard_name : String, count : int = 1) -> void:
	_call_android_function("fetchLeaderboardUserAroundRankings", [leaderboard_name, count])
	
	
# 将游戏数据提交到云存档
# 这是一个异步操作，请处理对应的信号以获取提交结果
func submit_game_save(data : GameSaveData) -> void:
	if not OS.has_feature("android"):
		push_warning("Only works on Android")
		return
		
	var image_cache_result : Array = _cache_image_get_path(data.cover_path)
	if image_cache_result[0] == false:
		push_log("Invalid image! Failed to cache image!", true)
		return
		
	var file_cache_result : Array = _cache_file_get_path(data.game_file_path)
	if file_cache_result[0] == false:
		push_log("Invalid file! Failed to cache file!", true)
		return
		
	_call_android_function("submitGameSave", [
		data.save_name, data.summary, data.played_time,
		data.progress_value, image_cache_result[1], file_cache_result[1], data.modified_at
	])
	
	
# 获取当前登录用户的所有存档数据
# 这是一个异步操作，请处理对应的信号以获取返回数据
func fetch_game_saves() -> void:
	_call_android_function("fetchGameSaves")
	
	
# 删除指定 Id 的存档
# 存档的 Id 包含在通过 fetch_game_saves 函数返回的数据中
# 这是一个异步操作，请处理对应的信号以获取删除结果
func delete_game_save(game_save_id) -> void:
	_call_android_function("deleteGameSave", [game_save_id])
	
	
func load_splash_ad(space_id : int) -> void:
	_call_android_function("loadSplashAd", [space_id])


func show_splash_ad() -> void:
	_call_android_function("showSplashAd")


func dispose_splash_ad() -> void:
	_call_android_function("disposeSplashAd")
	
	
func load_reward_video_ad(data : RewardVideoAdData) -> void:
	_call_android_function("loadRewardVideoAd", [
		data.space_id, data.reward_name, data.reward_amount,
		data.extra_info, data.game_user_id
	])


func show_reward_video_ad() -> void:
	_call_android_function("showRewardVideoAd")
	
	
func load_banner_ad(space_id : int) -> void:
	_call_android_function("loadBannerAd", [space_id])


func show_banner_ad(gravity : int = GRAVITY_BOTTOM, height = -1) -> void:
	_call_android_function("showBannerAd", [gravity, height])
	
	
func load_interstitial_ad(space_id : int) -> void:
	_call_android_function("loadInterstitialAd", [space_id])


func show_interstitial_ad() -> void:
	_call_android_function("showInterstitialAd")
	
	
func load_feed_ad(space_id : int, query : String = "") -> void:
	_call_android_function("loadFeedAd", [space_id, query])


func show_feed_ad(gravity : int = GRAVITY_BOTTOM, height = -1) -> void:
	_call_android_function("showFeedAd", [gravity, height])
	
	
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
	
	
func _dont_call_on_launch_from_deep_link(uri : String) -> void:
	on_launch_from_deep_link.emit(uri)
	
	
func _dont_call_on_splash_ad_return(code : int, msg : String) -> void:
	on_splash_ad_return.emit(code, msg)
	
	
func _dont_call_on_reward_video_ad_return(code : int, msg : String) -> void:
	on_reward_video_ad_return.emit(code, msg)
	
	
func _dont_call_on_banner_ad_return(code : int, msg : String) -> void:
	on_banner_ad_return.emit(code, msg)
	
	
func _dont_call_on_feed_ad_return(code : int, msg : String) -> void:
	on_feed_ad_return.emit(code, msg)
	
	
func _dont_call_on_interstitial_ad_return(code : int, msg : String) -> void:
	on_interstitial_ad_return.emit(code, msg)
	
	
func _json_to_array(json_string : Variant) -> Array:
	if json_string == null:
		return []
	var dict : Dictionary = JSON.parse_string(json_string)
	if dict.has("list"):
		return dict["list"]
	else:
		return []
		
		
func _generate_unique_filepath(id : int, extension : String) -> String:
	var date_str : String = Time.get_date_string_from_system()
	var time_str : String = Time.get_time_string_from_system().replace(":", "-")
	var prefix_str : String = date_str + "-" + time_str
	var unique_id : String = prefix_str + "_" + str(hash(id))
	var cache_dir : String = get_cache_dir_path()
	var cache_path : String = cache_dir + "/" + unique_id + "." + extension
	return cache_path
		
		
func _cache_file_get_path(file_path : String) -> Array:
	if not FileAccess.file_exists(file_path):
		return [false, null]
		
	var cache_path : String = _generate_unique_filepath(hash(file_path), file_path.get_extension())
	if FileAccess.file_exists(cache_path):
		return [true, cache_path]
		
	var input_file : FileAccess = FileAccess.open(file_path, FileAccess.READ)
	var output_file : FileAccess = FileAccess.open(cache_path, FileAccess.WRITE)
	output_file.store_string(input_file.get_as_text())
	
	return [true, cache_path]
		
		
func _cache_image_get_path(image_path : String) -> Array:
	var tex : Texture2D = load(image_path) as Texture2D
	var image : Image = tex.get_image()
	if image == null:
		return [false, null]
		
	var cache_path : String = _generate_unique_filepath(image.get_rid().get_id(), "png")
	if FileAccess.file_exists(cache_path):
		return [true, cache_path]
		
	var error : Error = image.save_png(cache_path)
	if error != OK:
		var err_msg = "Failed to saving the png image! Error: " + str(error)
		push_log(err_msg, true)
		push_log("Error file: " + cache_path, true)
	else:
		push_log("Save the png image successful: " + cache_path)
		
	return [true, cache_path]
		
		
func _call_android_function(android_func : String, args : Array = []) -> Variant:
	if not OS.has_feature("android"):
		push_warning("Only works on Android")
		return null
		
	if args.size() == 0:
		return _plugin_singleton.call(android_func)
	elif args.size() == 1:
		return _plugin_singleton.call(android_func, args[0])
	elif args.size() == 2:
		return _plugin_singleton.call(android_func, args[0], args[1])
	elif args.size() == 3:
		return _plugin_singleton.call(android_func, args[0], args[1], args[2])
	elif args.size() == 5:
		return _plugin_singleton.call(android_func,
			args[0], args[1], args[2], args[3], args[4])
	elif args.size() == 7:
		return _plugin_singleton.call(android_func,
			args[0], args[1], args[2], args[3], args[4], args[5], args[6])
	else:
		return null
		
