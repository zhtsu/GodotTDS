extends CanvasLayer


func _ready() -> void:
	GodotTDS.on_login_return.connect(_on_test_return)
	GodotTDS.on_anti_addiction_return.connect(_on_test_return)
	GodotTDS.on_tap_moment_return.connect(_on_test_return)
	
	
func _on_test_return(code : int, msg : String):
	$Label.text = msg
	

func _on_login_button_down() -> void:
	GodotTDS.login()


func _on_anti_addiction_button_down() -> void:
	GodotTDS.anti_addiction()


func _on_tap_moment_button_down() -> void:
	GodotTDS.tap_moment(GodotTDS.Orientation_Portrait)


func _on_logout_button_down() -> void:
	GodotTDS.logout()


func _on_show_entry_button_down() -> void:
	GodotTDS.set_entry_visible(true)


func _on_hide_entry_button_down() -> void:
	GodotTDS.set_entry_visible(false)


func _on_enable_toast_button_down() -> void:
	GodotTDS.set_toast_enabled(true)


func _on_disable_toast_button_down() -> void:
	GodotTDS.set_toast_enabled(false)
