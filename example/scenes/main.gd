extends CanvasLayer


func _ready() -> void:
	GodotTDS.on_login_return.connect(_on_test_return)
	GodotTDS.on_anti_addiction_return.connect(_on_test_return)
	GodotTDS.on_tap_moment_return.connect(_on_test_return)
	GodotTDS.on_achievement_return.connect(_on_test_return)
	GodotTDS.on_gift_return.connect(_on_test_return)
	GodotTDS.on_leaderboard_return.connect(_on_test_return)
	GodotTDS.on_game_save_return.connect(_on_test_return)
	
	
func _on_test_return(code : int, msg : String) -> void:
	$Code.text = str(code)
	if code == 1006:
		$Text.text = GodotTDS.get_network_all_achievement_list()
	elif code == 1001:
		$Text.text = str(JSON.parse_string(msg))
	elif code == 1001:
		$Text.text = str(JSON.parse_string(msg))
	else:
		$Text.text = msg
	

func _on_login_button_down() -> void:
	GodotTDS.login()


func _on_anti_addiction_button_down() -> void:
	GodotTDS.anti_addiction()


func _on_tap_moment_button_down() -> void:
	GodotTDS.tap_moment(GodotTDS.ORIENTATION_DEFAULT)


func _on_logout_button_down() -> void:
	GodotTDS.logout()


func _on_show_entry_button_down() -> void:
	GodotTDS.set_entry_visible(true)


func _on_hide_entry_button_down() -> void:
	GodotTDS.set_entry_visible(false)


func _on_achievement_button_down() -> void:
	GodotTDS.fetch_all_achievement_list()


func _on_get_user_profile_button_down() -> void:
	$Text.text = GodotTDS.get_user_object_id()


func _on_achievement_page_button_down() -> void:
	GodotTDS.show_achievement_page()


func _on_reach_achievement_button_down() -> void:
	GodotTDS.reach_achievement("robot_dash_02")


func _on_grow_achievement_button_down() -> void:
	GodotTDS.grow_achievement_steps("robot_dash_04", 1)


func _on_show_tips_toast_button_down() -> void:
	GodotTDS.set_show_tips_toast(true)


func _on_hide_tips_toast_button_down() -> void:
	GodotTDS.set_show_tips_toast(false)


var show_achievement_toast : bool = true

func _on_achievement_toast_button_down() -> void:
	show_achievement_toast = not show_achievement_toast
	if show_achievement_toast:
		$GridContainer/AchievementToast.text = "成就弹窗(开)"
		GodotTDS.set_show_achievement_toast(show_achievement_toast)
	else:
		$GridContainer/AchievementToast.text = "成就弹窗(关)"
		GodotTDS.set_show_achievement_toast(show_achievement_toast)


func _on_submit_gift_code_button_down() -> void:
	GodotTDS.submit_gift_code("114514")


func _on_sync_achievement_button_down() -> void:
	$Text.text = str(GodotTDS.get_network_all_achievement_list())


func _on_submit_leaderboard_score_button_down() -> void:
	GodotTDS.submit_leaderboard_score("Score", 100)


func _on_access_section_rankings_button_down() -> void:
	GodotTDS.access_leaderboard_section_rankings("Score", 0, 10)


func _on_access_user_ranking_button_down() -> void:
	GodotTDS.access_leaderboard_user_around_rankings("Score")


func _on_submit_game_save_button_down() -> void:
	var game_data : GodotTDS.GameSaveData = GodotTDS.GameSaveData.new()
	game_data.save_name = "GameSave"
	game_data.summary = "This is a test game save"
	game_data.played_time = 6000
	game_data.progress_value = 60
	game_data.cover_path = "res://assets/logo.png"
	game_data.game_file_path = "res://assets/data.json"
	game_data.modified_at = Time.get_unix_time_from_system() as int
	GodotTDS.submit_game_save(game_data)


func _on_access_game_saves_button_down() -> void:
	GodotTDS.access_game_saves()


func _on_delete_game_save_button_down() -> void:
	GodotTDS.delete_game_save($GridContainer/GameSaveObjectId.text)
