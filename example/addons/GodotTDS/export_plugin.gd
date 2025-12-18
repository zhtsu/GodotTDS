@tool
extends EditorPlugin


var export_plugin: AndroidExportPlugin


func _enter_tree():
	export_plugin = AndroidExportPlugin.new()
	add_export_plugin(export_plugin)
	add_autoload_singleton("GodotTDS", "res://addons/GodotTDS/GodotTDS.gd")


func _exit_tree():
	remove_autoload_singleton("GodotTDS")
	remove_export_plugin(export_plugin)
	export_plugin = null


class AndroidExportPlugin extends EditorExportPlugin:
	var _plugin_name = "GodotTdsPlugin"

	func _supports_platform(platform):
		if platform is EditorExportPlatformAndroid:
			return true
		return false
	
	const tap_sdk_version: String = "4.9.1"
	const tap_adn_version: String = "3.16.3.45"
	var tap_adn_libs: PackedStringArray = [
		"GodotTDS/bin/TapAD_{0}.aar".format([tap_adn_version])
	]

	func _get_android_libraries(platform, debug):
		var android_libs: PackedStringArray = []
		if debug:
			android_libs.append("GodotTDS/bin/GodotTdsPlugin-debug.aar")
			android_libs.append_array(tap_adn_libs)
		else:
			android_libs.append("GodotTDS/bin/GodotTdsPlugin-release.aar")
			android_libs.append_array(tap_adn_libs)
		return android_libs
		
	func _get_android_dependencies(platform: EditorExportPlatform, debug: bool) -> PackedStringArray:
		return PackedStringArray([
			"com.taptap:lc-storage-android:8.2.24",
			"com.taptap:lc-realtime-android:8.2.24",
			"androidx.core:core-ktx:1.13.1",
			"androidx.appcompat:appcompat:1.7.0",
			"com.squareup.okhttp3:okhttp:4.9.2",
			"io.reactivex.rxjava2:rxandroid:2.1.1",
			"io.reactivex.rxjava2:rxjava:2.2.19",
			"com.android.support:appcompat-v7:28.0.0",
			"com.android.support:support-annotations:28.0.0",
			"com.android.support:support-v4:28.0.0",
			"com.github.bumptech.glide:glide:4.16.0",
			"com.android.support:recyclerview-v7:28.0.0",
			
			"com.taptap.sdk:tap-core:{0}".format([tap_sdk_version]),
			"com.taptap.sdk:tap-compliance:{0}".format([tap_sdk_version]),
			"com.taptap.sdk:tap-login:{0}".format([tap_sdk_version]),
			"com.taptap.sdk:tap-moment:{0}".format([tap_sdk_version]),
			"com.taptap.sdk:tap-achievement:{0}".format([tap_sdk_version]),
			"com.taptap.sdk:tap-leaderboard-androidx:{0}".format([tap_sdk_version]),
			"com.taptap.sdk:tap-cloudsave:{0}".format([tap_sdk_version]),
			"org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1"
		])
		
	func _get_android_dependencies_maven_repos(platform: EditorExportPlatform, debug: bool) -> PackedStringArray:
		return PackedStringArray([
			"https://jitpack.io"
		])
		
	func _get_android_manifest_element_contents(platform: EditorExportPlatform, debug: bool) -> String:
		return """
		<uses-permission android:name="android.permission.INTERNET"></uses-permission>
		<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
		<uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
		<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES"></uses-permission>
		<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"></uses-permission>
		<uses-permission android:name="android.permission.BLUETOOTH"/>
		<uses-permission android:name="android.permission.BLUETOOTH_CONNECT"/>
		<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"></uses-permission>
		<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"></uses-permission>
		<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
		<uses-permission android:name="android.permission.POST_NOTIFICATIONS"></uses-permission>
		"""
		
	func _get_android_manifest_activity_element_contents(platform: EditorExportPlatform, debug: bool) -> String:
		return """
		<intent-filter>
			<action android:name="android.intent.action.VIEW" />
			<category android:name="android.intent.category.DEFAULT" />
			<category android:name="android.intent.category.BROWSABLE" />
		</intent-filter>
		"""

	func _get_name():
		return _plugin_name
