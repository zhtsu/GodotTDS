@tool
extends EditorPlugin


var export_plugin : AndroidExportPlugin


func _enter_tree():
	export_plugin = AndroidExportPlugin.new()
	add_export_plugin(export_plugin)
	add_autoload_singleton("GodotTDS", "res://addons/GodotTDS/GodotTDS.gd")


func _exit_tree():
	remove_export_plugin(export_plugin)
	export_plugin = null


class AndroidExportPlugin extends EditorExportPlugin:
	var _plugin_name = "GodotTdsPlugin"

	func _supports_platform(platform):
		if platform is EditorExportPlatformAndroid:
			return true
		return false

	func _get_android_libraries(platform, debug):
		if debug:
			return PackedStringArray(["GodotTDS/bin/GodotTdsPlugin-debug.aar"])
		else:
			return PackedStringArray(["GodotTDS/bin/GodotTdsPlugin-release.aar"])

	func _get_name():
		return _plugin_name
