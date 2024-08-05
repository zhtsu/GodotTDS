extends Node


# Success code: 200
signal on_login_return(code : int, msg : String)
signal on_anti_addiction_return(code : int, msg : String)
signal on_tap_moment_return(code : int, msg : String)


var _plugin_name : String = "GodotTdsPlugin"
var _plugin_singleton : Object


func _ready() -> void:
	if Engine.has_singleton(_plugin_name):
		_plugin_singleton = Engine.get_singleton(_plugin_name)
		_plugin_singleton.init(
			"qj7nkppf3iltbsyk4b",
			"Ybw1yeEmPXCnbEu29oM1ffb5IKZAsY9bDKXHFQ1d",
			"https://server.zhtsu.cn")
			
		_plugin_singleton.connect("onLogInReturn", _dont_call_on_login_return)
		_plugin_singleton.connect("onAntiAddictionReturn", _dont_call_on_anti_addiction_return)
		_plugin_singleton.connect("onTapMomentReturn", _dont_call_on_anti_addiction_return)


func login() -> void:
	if OS.has_feature("android"):
		_plugin_singleton.logIn()
	else:
		push_warning("Only work on Android")
		
		
func logout() -> void:
	if OS.has_feature("android"):
		_plugin_singleton.logOut()
	else:
		push_warning("Only work on Android")
		
		
func anti_addiction() -> void:
	if OS.has_feature("android"):
		_plugin_singleton.antiAddiction()
	else:
		push_warning("Only work on Android")
		
		
func get_age_range() -> int:
	if OS.has_feature("android"):
		return _plugin_singleton.getAgeRange()
	else:
		push_warning("Only work on Android")
		return -1
		
		
const Orientation_Default : int = 0
const Orientation_Landscape : int = 1
const Orientation_Portrait : int = 2
const Orientation_Sensor : int = 3

func tap_moment(orientation : int = Orientation_Default) -> void:
	if OS.has_feature("android"):
		_plugin_singleton.tapMoment(orientation)
	else:
		push_warning("Only work on Android")
	
	
func get_user_profile() -> Dictionary:
	if OS.has_feature("android"):
		return JSON.parse_string(_plugin_singleton.getUserProfile())
	else:
		push_warning("Only work on Android")
		return Dictionary()
		
		
func set_entry_visible(visible : bool) -> void:
	if OS.has_feature("android"):
		_plugin_singleton.setEntryVisible(visible)
	else:
		push_warning("Only work on Android")
	
	
func set_toast_enabled(enabled : bool) -> void:
	_plugin_singleton.setToastEnabled(enabled)
	
	
# Dont call these functions from outside
func _dont_call_on_login_return(code : int, msg : String) -> void:
	on_login_return.emit(code, msg)
	
	
func _dont_call_on_anti_addiction_return(code : int, msg : String) -> void:
	on_anti_addiction_return.emit(code, msg)
	
	
func _dont_call_on_tap_moment_return(code : int, msg : String) -> void:
	on_tap_moment_return.emit(code, msg)
