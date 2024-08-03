extends Node


# user_profile
# uuid
# userName
# shortId
# nickName
# avatar
signal on_login_success(user_profile : Dictionary)
signal on_login_fail(code : int, error : String)


var _plugin_name : String = "GodotTdsPlugin"
var _plugin_singleton : Object


func _ready() -> void:
	if Engine.has_singleton(_plugin_name):
		_plugin_singleton = Engine.get_singleton(_plugin_name)
		_plugin_singleton.init(
			"qj7nkppf3iltbsyk4b",
			"Ybw1yeEmPXCnbEu29oM1ffb5IKZAsY9bDKXHFQ1d",
			"https://server.zhtsu.cn")
			
		_plugin_singleton.connect("onLoginSuccess", _on_login_success)
		_plugin_singleton.connect("onLoginFail", _on_login_fail)


func login() -> void:
	if OS.has_feature("android"):
		_plugin_singleton.login()
	else:
		push_warning("Only work on Android")
	
	
func get_user_profile() -> String:
	if OS.has_feature("android"):
		return _plugin_singleton.getUserProfile()
	else:
		push_warning("Only work on Android")
		return ""
	
	
func set_show_popup_tips(enabled : bool) -> void:
	_plugin_singleton.setShowPopupTips(enabled)
	
	
func _on_login_success(user_profile_string : String) -> void:
	on_login_success.emit(JSON.parse_string(user_profile_string))
	
	
func _on_login_fail(code : int, error : String) -> void:
	on_login_fail.emit(code, error)
