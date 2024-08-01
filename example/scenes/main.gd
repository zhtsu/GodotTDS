extends CanvasLayer


func _on_button_button_down() -> void:
	$Label.text = GodotTDS.get_hello()
