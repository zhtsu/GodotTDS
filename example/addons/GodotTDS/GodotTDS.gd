extends Node


var _plugin_name : String = "GodotTdsPlugin"
var _plugin_singleton : Object


func _ready() -> void:
	if Engine.has_singleton(_plugin_name):
		_plugin_singleton = Engine.get_singleton(_plugin_name)


func get_hello() -> String:
	return _plugin_singleton.helloGodotPlugin()
