extends CanvasLayer


func _ready() -> void:
	GodotTDS.on_anti_addiction_return.connect(_on_test_return)


func _on_button_button_down() -> void:
	GodotTDS.anti_addiction()
	
	
func _on_test_return(code : int, msg : String):
	$Label.text = msg
	
