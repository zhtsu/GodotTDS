extends Node


var singleton


func _ready() -> void:
	if Engine.has_singleton("GodotTDS"):
		singleton = Engine.get_singleton("GodotTDS")


func get_hello() -> String:
	return singleton.helloGodotPlugin()
