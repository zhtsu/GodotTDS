extends CanvasLayer


func _on_button_button_down() -> void:
	if Engine.has_singleton("GodotTdsPlugin"):
		$Label.text = GodotTDS.get_hello()
	else:
		$Label.text = "Failed to get singleton of the GodotTdsPlugin!"
	
