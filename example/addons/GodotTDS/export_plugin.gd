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

	const tap_sdk_version : String = "3.29.2"
	const tap_ad_version : String = "3.16.3.31"
	var tap_sdk_libs : PackedStringArray = [
		"GodotTDS/bin/TapSDK/AntiAddiction_{0}.aar".format([tap_sdk_version]),
		"GodotTDS/bin/TapSDK/AntiAddictionUI_{0}.aar".format([tap_sdk_version]),
		"GodotTDS/bin/TapSDK/TapAD_{0}.aar".format([tap_ad_version]),
		"GodotTDS/bin/TapSDK/TapBootstrap_{0}.aar".format([tap_sdk_version]),
		"GodotTDS/bin/TapSDK/TapCommon_{0}.aar".format([tap_sdk_version]),
		"GodotTDS/bin/TapSDK/TapConnect_{0}.aar".format([tap_sdk_version]),
		"GodotTDS/bin/TapSDK/TapDB_{0}.aar".format([tap_sdk_version]),
		"GodotTDS/bin/TapSDK/TapLogin_{0}.aar".format([tap_sdk_version]),
		"GodotTDS/bin/TapSDK/TapMoment_{0}.aar".format([tap_sdk_version]),
		"GodotTDS/bin/TapSDK/TapAchievement_{0}.aar".format([tap_sdk_version])
	]

	func _get_android_libraries(platform, debug):
		var android_libs : PackedStringArray = []
		if debug:
			android_libs.append("GodotTDS/bin/GodotTdsPlugin-debug.aar")
			android_libs.append_array(tap_sdk_libs)
		else:
			android_libs.append("GodotTDS/bin/GodotTdsPlugin-release.aar")
			android_libs.append_array(tap_sdk_libs)
		return android_libs
		
	func _get_android_dependencies(platform: EditorExportPlatform, debug: bool) -> PackedStringArray:
		return PackedStringArray([
			"com.taptap:lc-storage-android:8.2.24",
			"com.taptap:lc-realtime-android:8.2.24",
			"androidx.core:core-ktx:1.13.1",
			"androidx.appcompat:appcompat:1.7.0",
			"com.squareup.okhttp3:okhttp:4.9.2"
		])
		
	func _get_android_dependencies_maven_repos(platform: EditorExportPlatform, debug: bool) -> PackedStringArray:
		return PackedStringArray([
			"https://jitpack.io"
		])
		
	func _get_android_manifest_element_contents(platform: EditorExportPlatform, debug: bool) -> String:
		return """
		<uses-permission android:name="android.permission.INTERNET"></uses-permission>
		<uses-permission android:name="android.permission.WAKE_LOCK"></uses-permission>
		<uses-permission android:name="android.permission.GET_TASKS"></uses-permission>
		<uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
		<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
		<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"></uses-permission>
		<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
		<uses-permission android:name="android.permission.READ_MEDIA_IMAGES"/>
		<uses-permission android:name="android.permission.READ_MEDIA_VIDEO"/>
		<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
		<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"></uses-permission>
		<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"></uses-permission>
		<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"></uses-permission>
		"""

	func _get_name():
		return _plugin_name
