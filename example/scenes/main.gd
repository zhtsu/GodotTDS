extends CanvasLayer


func _ready() -> void:
	GodotTDS.on_login_success.connect(_on_login_success)


func _on_button_button_down() -> void:
	GodotTDS.login()
	
	
func _on_login_success(user_profile : Dictionary):
	$Label.text = user_profile["nickName"]
	
