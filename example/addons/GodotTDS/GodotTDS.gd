extends Node


signal on_login_return(code : int, msg : String)
signal on_anti_addiction_return(code : int, msg : String)
signal on_tap_moment_return(code : int, msg : String)
signal on_achievement_return(code : int, msg : String)


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


func call_android_function(android_func : String, args : Array = []) -> Variant:
	if OS.has_feature("android"):
		if args.size() == 0:
			return _plugin_singleton.call(android_func)
		elif args.size() == 1:
			return _plugin_singleton.call(android_func, args[0])
		elif args.size() == 2:
			return _plugin_singleton.call(android_func, args[0], args[1])
		else:
			return null
	else:
		push_warning("Only works on Android")
		return null


func login() -> void:
	call_android_function("logIn")
		
		
func logout() -> void:
	call_android_function("logOut")
		
		
func anti_addiction() -> void:
	call_android_function("antiAddiction")
		
		
func tap_moment(orientation : int = Orientation_Default) -> void:
	call_android_function("tapMoment", [orientation])
	
	
func get_user_profile() -> Dictionary:
	var json_string : Variant = call_android_function("getUserProfile")
	return Dictionary() if json_string == null else JSON.parse_string(json_string)
	
	
func get_user_object_id() -> String:
	var object_id : Variant = call_android_function("getUserObjectId")
	return "" if object_id == null else object_id
		
		
func set_entry_visible(visible : bool) -> void:
	call_android_function("setEntryVisible", [visible])
	
	
func set_show_tips_toast(show : bool) -> void:
	call_android_function("setShowTipsToast", [show])
	
	
func fetch_all_achievement_list() -> void:
	call_android_function("fetchAllAchievementList")
	
	
func get_local_all_achievement_list() -> String:
	var json_string : Variant = call_android_function("getLocalAllAchievementList")
	return "null" if json_string == null else json_string
	
	
func get_network_all_achievement_list() -> String:
	var json_string : Variant = call_android_function("getNetworkAllAchievementList")
	return "null" if json_string == null else json_string
	
	
func show_achievement_page() -> void:
	call_android_function("showAchievementPage")
	
	
func reach_achievement(display_id : String) -> void:
	call_android_function("reachAchievement", [display_id])
	
	
func grow_achievement_steps(display_id : String, steps : int) -> void:
	call_android_function("growAchievementSteps", [display_id, steps])
	
	
func make_achievement_steps(display_id : String, steps : int) -> void:
	call_android_function("makeAchievementSteps", [display_id, steps])
	
	
func set_show_achievement_toast(show : bool) -> void:
	call_android_function("setShowAchievementToast", [show])
	
	
# Dont call these functions from outside
func _dont_call_on_login_return(code : int, msg : String) -> void:
	on_login_return.emit(code, msg)
	
	
func _dont_call_on_anti_addiction_return(code : int, msg : String) -> void:
	on_anti_addiction_return.emit(code, msg)
	
	
func _dont_call_on_tap_moment_return(code : int, msg : String) -> void:
	on_tap_moment_return.emit(code, msg)
	
	
func _dont_call_on_achievement_return(code : int, msg : String) -> void:
	on_achievement_return.emit(code, msg)
