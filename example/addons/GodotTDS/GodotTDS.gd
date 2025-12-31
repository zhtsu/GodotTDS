extends Node

const StateCode = preload("res://addons/GodotTDS/state_code.gd")

# 登录相关操作的信号
signal on_login_return(code: int, msg: String)
# 防沉迷相关操作的信号
signal on_compliance_return(code: int, msg: String)
# 内嵌动态相关操作的信号
signal on_tap_moment_return(code: int, msg: String)
# 成就相关操作的信号
signal on_achievement_return(code: int, msg: String)
# 礼包相关操作的信号
signal on_gift_return(code: int, msg: String)
# 排行榜相关操作的信号
signal on_leaderboard_return(code: int, msg: String)
# 云储存相关操作的信号
signal on_cloud_save_return(code: int, msg: String)
# 开屏广告相关的信号
signal on_splash_ad_return(code: int, msg: String)
# 视频激励广告相关的信号
signal on_reward_video_ad_return(code: int, msg: String)
# 横幅广告相关的信号
signal on_banner_ad_return(code: int, msg: String)
# 信息流广告相关的信号
signal on_feed_ad_return(code: int, msg: String)
# 插屏广告相关的信号
signal on_interstitial_ad_return(code: int, msg: String)

# 广告使用的布局
enum
{
	GRAVITY_BOTTOM = 0,
	GRAVITY_TOP = 1
}
	
class RewardVideoAdData:
	var space_id: int
	var reward_name: String
	var reward_amount: int
	var extra_info: String
	var game_user_id: String
	
var _plugin_name: String = "GodotTdsPlugin"
var _plugin_singleton: Variant = null

func _ready() -> void:
	if Engine.has_singleton(_plugin_name):
		_plugin_singleton = Engine.get_singleton(_plugin_name)
			
		_plugin_singleton.connect("onLoginReturn",
			func(code: int, msg: String):
				on_login_return.emit(code, msg)
		)
		_plugin_singleton.connect("onComplianceReturn",
			func(code: int, msg: String):
				on_compliance_return.emit(code, msg)
		)
		_plugin_singleton.connect("onTapMomentReturn",
			func(code: int, msg: String):
				on_tap_moment_return.emit(code, msg)
		)
		_plugin_singleton.connect("onAchievementReturn",
			func(code: int, msg: String):
				on_achievement_return.emit(code, msg)
		)
		_plugin_singleton.connect("onGiftReturn",
			func(code: int, msg: String):
				on_gift_return.emit(code, msg)
		)
		_plugin_singleton.connect("onLeaderboardReturn",
			func(code: int, msg: String):
				on_leaderboard_return.emit(code, msg)
		)
		_plugin_singleton.connect("onCloudSaveReturn",
			func(code: int, msg: String):
				on_cloud_save_return.emit(code, msg)
		)
		_plugin_singleton.connect("onSplashAdReturn",
			func(code: int, msg: String):
				on_splash_ad_return.emit(code, msg)
		)
		_plugin_singleton.connect("onRewardVideoAdReturn",
			func(code: int, msg: String):
				on_reward_video_ad_return.emit(code, msg)
		)
		_plugin_singleton.connect("onBannerAdReturn",
			func(code: int, msg: String):
				on_banner_ad_return.emit(code, msg)
		)
		_plugin_singleton.connect("onInterstitialAdReturn",
			func(code: int, msg: String):
				on_interstitial_ad_return.emit(code, msg)
		)
		_plugin_singleton.connect("onFeedAdReturn",
			func(code: int, msg: String):
				on_feed_ad_return.emit(code, msg)
		)
		
# 在安卓平台输出日志
func push_log(msg: String, error: bool = false) -> void:
	_call_android_function("pushLog", [msg, error])
	
# 获取安卓平台的缓存路径
func get_cache_dir_path() -> String:
	var cache_dir_path: Variant = _call_android_function("getCacheDirPath")
	return "" if cache_dir_path == null else cache_dir_path
	
# 在安卓平台弹出一个吐司弹窗
func show_toast(msg: String) -> void:
	_call_android_function("showToast", [msg])
	
# 登录TapTap账户
# 默认开启 basic_info 权限
# 若想要获取到头像和好友数据等更多信息
# 需要在后台开启对应权限，并在调用登录方法时将对应的布尔值设为真
# 详情参考：https://developer.taptap.cn/docs/sdk/taptap-login/guide/
func login(public_profile_enabled: bool = false, user_friends_enabled: bool = false) -> void:
	_call_android_function("login", [public_profile_enabled, user_friends_enabled])
		
# 退出登录
func logout() -> void:
	_call_android_function("logout")
	
# 判断当前用户是否登录
func is_logged_in() -> bool:
	var logged_in: Variant = _call_android_function("isLoggedIn")
	return false if logged_in == null else logged_in
		
# 防沉迷
func startup_compliance() -> void:
	_call_android_function("startupCompliance")
		
# 打开内嵌动态
func open_tap_moment() -> void:
	_call_android_function("openMomentPage")
	
# 得到当前登录用户的信息
func get_current_tap_account() -> Dictionary:
	var json_string: Variant = _call_android_function("getCurrentTapAccountAsString")
	return {} if json_string == null else JSON.parse_string(json_string)
	
# 打开成就页面
func show_achievements() -> void:
	_call_android_function("showAchievements")
	
# 达成对应的单步成就
func unlock_achievement(achievement_id: String) -> void:
	_call_android_function("unlockAchievement", [achievement_id])
	
# 增加分步成就的进度
# 默认增加步数为 1
func increment_achievement(achievement_id: String, steps: int = 1) -> void:
	_call_android_function("incrementAchievement", [achievement_id, steps])
	
# 设置解锁新成就时是否弹窗
func set_achievement_toast_enable(enable: bool) -> void:
	_call_android_function("setAchievementToastEnable", [enable])
	
# 提交礼包兑换码（无服务器兑换）
func submit_gift_code(gift_code: String) -> void:
	_call_android_function("submitGiftCode", [gift_code])
	
# 向目标排行榜中提交数据
func submit_leaderboard_score(leaderboard_id: String, score: int) -> void:
	_call_android_function("submitLeaderboardScore", [leaderboard_id, score])
	
# 打开排行榜H5页面对话框，支持总榜和好友榜两种类型
# leaderboard_collection 用来指定排行榜的类型
# 0: Public
# 1: Friends
func open_leaderboard(leaderboard_id: String, leaderboard_collection: int) -> void:
	_call_android_function("openLeaderboard", [leaderboard_id, leaderboard_collection])

# 展示指定用户的个人资料对话框，传入用户的openId
func show_tap_user_profile(open_id: String) -> void:
	_call_android_function("showTapUserProfile", [open_id])
	
# 分页获取排行榜数据，支持总榜和好友榜
# 首次请求 next_page 留空即可
func load_leaderboard_scores(leaderboard_id: String, leaderboard_collection: int, next_page: String = "") -> void:
	_call_android_function("loadLeaderboardScores", [leaderboard_id, leaderboard_collection, next_page])

# 获取当前登录用户在指定排行榜的分数和排名
func load_current_player_leaderboard_score(leaderboard_id: String, leaderboard_collection: int):
	_call_android_function("loadCurrentPlayerLeaderboardScore", [leaderboard_id, leaderboard_collection])
	
# 查询当前用户相近的其他用户成绩（上下X位）
func load_player_centered_leaderboard_scores(leaderboard_id: String, leaderboard_collection: int, period_token: String, max_count: int) -> void:
	_call_android_function("loadPlayerCenteredLeaderboardScores", [leaderboard_id, leaderboard_collection, period_token, max_count])
	
# 创建游戏存档并上传云端
func create_cloud_save_archive(
		name: String,
		summary: String,
		extra: String,
		playtime: int,
		archive_file_path: String,
		archive_cover_path: String
	):
	if not OS.has_feature("android"):
		push_warning("Only works on Android")
		return
		
	var image_cache_result : Array = _cache_image_get_path(archive_cover_path)
	if image_cache_result[0] == false:
		push_log("Invalid image! Failed to cache image!", true)
		return
		
	var file_cache_result : Array = _cache_file_get_path(archive_file_path)
	if file_cache_result[0] == false:
		push_log("Invalid file! Failed to cache file!", true)
		return
		
	_call_android_function("createCloudSaveArchive", [name, summary, extra, playtime, file_cache_result[1], image_cache_result[1]])
	
# 获取当前用户的存档列表
func get_cloud_save_archive_list():
	_call_android_function("getCloudSaveArchiveList", [])
	
# 下载指定的存档文件
func get_cloud_save_archive_data(archive_uuid: String, archive_file_id: String):
	_call_android_function("getCloudSaveArchiveData", [archive_uuid, archive_file_id])
	
# 更新指定的存档文件
func update_cloud_save_archive(
		archive_uuid: String,
		name: String,
		summary: String,
		extra: String,
		playtime: int,
		archive_file_path: String,
		archive_cover_path: String
	):
	if not OS.has_feature("android"):
		push_warning("Only works on Android")
		return
		
	var image_cache_result : Array = _cache_image_get_path(archive_cover_path)
	if image_cache_result[0] == false:
		push_log("Invalid image! Failed to cache image!", true)
		return
		
	var file_cache_result : Array = _cache_file_get_path(archive_file_path)
	if file_cache_result[0] == false:
		push_log("Invalid file! Failed to cache file!", true)
		return
		
	_call_android_function("updateCloudSaveArchive", [archive_uuid, name, summary, extra, playtime, file_cache_result[1], image_cache_result[1]])
	
# 删除指定的存档文件
func delete_cloud_save_archive(archive_uuid: String):
	_call_android_function("deleteCloudSaveArchive", [archive_uuid])
	
# 获取指定存档的封面图片
func get_cloud_save_archive_cover(archive_uuid: String, archive_file_id: String):
	_call_android_function("getCloudSaveArchiveCover", [archive_uuid, archive_file_id])

func load_splash_ad(space_id: int) -> void:
	_call_android_function("loadSplashAd", [space_id])

func show_splash_ad() -> void:
	_call_android_function("showSplashAd")

func dispose_splash_ad() -> void:
	_call_android_function("disposeSplashAd")
	
func load_reward_video_ad(data: RewardVideoAdData) -> void:
	_call_android_function("loadRewardVideoAd", [
		data.space_id, data.reward_name, data.reward_amount,
		data.extra_info, data.game_user_id
	])

func show_reward_video_ad() -> void:
	_call_android_function("showRewardVideoAd")
	
func load_banner_ad(space_id: int) -> void:
	_call_android_function("loadBannerAd", [space_id])

func show_banner_ad(gravity: int = GRAVITY_BOTTOM, height = -1) -> void:
	_call_android_function("showBannerAd", [gravity, height])
	
func dispose_banner_ad() -> void:
	_call_android_function("disposeBannerAd")
	
func load_interstitial_ad(space_id: int) -> void:
	_call_android_function("loadInterstitialAd", [space_id])
	
func show_interstitial_ad() -> void:
	_call_android_function("showInterstitialAd")
	
func load_feed_ad(space_id: int, query: String = "") -> void:
	_call_android_function("loadFeedAd", [space_id, query])

func show_feed_ad(gravity: int = GRAVITY_BOTTOM, height = -1) -> void:
	_call_android_function("showFeedAd", [gravity, height])
	
func load_image_from_base64(base64_string: String) -> ImageTexture:
	var image_data: PackedByteArray = Marshalls.base64_to_raw(base64_string)
	if image_data.is_empty():
		return null
	
	var image: Image = Image.new()
	var error: Error
	
	error = image.load_png_from_buffer(image_data)
	if error != OK:
		error = image.load_jpg_from_buffer(image_data)
	if error != OK:
		error = image.load_webp_from_buffer(image_data)
	
	if error != OK:
		push_error(str(error))
		return null
		
	return ImageTexture.create_from_image(image)
	
func load_json_from_base64(base64_string: String) -> Dictionary:
	var raw_data: PackedByteArray = Marshalls.base64_to_raw(base64_string)
	if raw_data.is_empty():
		push_error("Failed to decode Base64 string")
		return {}
	var json_string: String = raw_data.get_string_from_utf8()
	return JSON.parse_string(json_string)
	
func _json_to_array(json_string: Variant) -> Array:
	if json_string == null:
		return []
	var dict: Dictionary = JSON.parse_string(json_string)
	if dict.has("list"):
		return dict["list"]
	else:
		return []
		
func _generate_unique_filepath(id: int, extension: String) -> String:
	var date_str: String = Time.get_date_string_from_system()
	var time_str: String = Time.get_time_string_from_system().replace(":", "-")
	var prefix_str: String = date_str + "-" + time_str
	var unique_id: String = prefix_str + "_" + str(hash(id))
	var cache_dir: String = get_cache_dir_path()
	var cache_path: String = cache_dir + "/" + unique_id + "." + extension
	return cache_path
		
func _cache_file_get_path(file_path: String) -> Array:
	if not FileAccess.file_exists(file_path):
		return [false, null]
		
	var cache_path: String = _generate_unique_filepath(hash(file_path), file_path.get_extension())
	if FileAccess.file_exists(cache_path):
		return [true, cache_path]
		
	var input_file: FileAccess = FileAccess.open(file_path, FileAccess.READ)
	var output_file: FileAccess = FileAccess.open(cache_path, FileAccess.WRITE)
	output_file.store_string(input_file.get_as_text())
	
	return [true, cache_path]
		
func _cache_image_get_path(image_path: String) -> Array:
	var tex: Texture2D = load(image_path) as Texture2D
	var image: Image = tex.get_image()
	if image == null:
		return [false, null]
		
	var cache_path: String = _generate_unique_filepath(image.get_rid().get_id(), "png")
	if FileAccess.file_exists(cache_path):
		return [true, cache_path]
		
	var error: Error = image.save_png(cache_path)
	if error != OK:
		var err_msg = "Failed to saving the png image! Error: " + str(error)
		push_log(err_msg, true)
		push_log("Error file: " + cache_path, true)
	else:
		push_log("Save the png image successful: " + cache_path)
		
	return [true, cache_path]
		
func _call_android_function(android_func: String, args: Array = []) -> Variant:
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
	elif args.size() == 4:
		return _plugin_singleton.call(android_func, args[0], args[1], args[2], args[3])
	elif args.size() == 5:
		return _plugin_singleton.call(android_func, args[0], args[1], args[2], args[3], args[4])
	elif args.size() == 6:
		return _plugin_singleton.call(android_func, args[0], args[1], args[2], args[3], args[4], args[5])
	elif args.size() == 7:
		return _plugin_singleton.call(android_func, args[0], args[1], args[2], args[3], args[4], args[5], args[6])
	else:
		return null
