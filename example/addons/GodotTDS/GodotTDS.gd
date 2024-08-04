extends Node


# Success code: 200
signal on_login_return(code : int, msg : String)
signal on_anti_addiction_return(code : int, msg : String)


var _plugin_name : String = "GodotTdsPlugin"
var _plugin_singleton : Object


func _ready() -> void:
	if Engine.has_singleton(_plugin_name):
		_plugin_singleton = Engine.get_singleton(_plugin_name)
		_plugin_singleton.init(
			"qj7nkppf3iltbsyk4b",
			"Ybw1yeEmPXCnbEu29oM1ffb5IKZAsY9bDKXHFQ1d",
			"https://server.zhtsu.cn")
			
		_plugin_singleton.connect("onLoginReturn", _on_login_return)
		_plugin_singleton.connect("onAntiAddictionReturn", _on_anti_addiction_return)


func login() -> void:
	if OS.has_feature("android"):
		_plugin_singleton.login()
	else:
		push_warning("Only work on Android")
		
		
func anti_addiction() -> void:
	if OS.has_feature("android"):
		_plugin_singleton.antiAddiction()
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
	
	
func _on_login_return(code : int, msg : String) -> void:
	on_login_return.emit(code, msg)
	
	
func _on_anti_addiction_return(code : int, msg : String) -> void:
	on_anti_addiction_return.emit(code, msg)
	
