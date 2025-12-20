extends CanvasLayer


func _ready() -> void:
	GodotTDS.on_login_return.connect(_on_test_return)
	GodotTDS.on_compliance_return.connect(_on_test_return)
	GodotTDS.on_tap_moment_return.connect(_on_test_return)
	GodotTDS.on_achievement_return.connect(_on_test_return)
	GodotTDS.on_gift_return.connect(_on_test_return)
	GodotTDS.on_leaderboard_return.connect(_on_test_return)
	GodotTDS.on_splash_ad_return.connect(_on_test_return)
	GodotTDS.on_reward_video_ad_return.connect(_on_test_return)
	GodotTDS.on_banner_ad_return.connect(_on_test_return)
	GodotTDS.on_interstitial_ad_return.connect(_on_test_return)
	GodotTDS.on_feed_ad_return.connect(_on_test_return)
	
	
func _on_test_return(code : int, msg : String) -> void:
	$Code.text = str(code)
	$Text.text = msg
	if code == GodotTDS.StateCode.AD_SPLASH_TIME_OVER:
		GodotTDS.dispose_splash_ad()
		
		
func _on_login_button_down() -> void:
	GodotTDS.login()
	

func _on_compliance_button_down() -> void:
	GodotTDS.startup_compliance()


func _on_tap_moment_button_down() -> void:
	GodotTDS.open_tap_moment()


func _on_logout_button_down() -> void:
	GodotTDS.logout()


func _on_get_user_profile_button_down() -> void:
	$Text.text = str(GodotTDS.get_current_tap_account())


func _on_achievement_page_button_down() -> void:
	GodotTDS.show_achievements()


func _on_unlock_achievement_button_down() -> void:
	GodotTDS.unlock_achievement($Text.text)


func _on_grow_achievement_button_down() -> void:
	GodotTDS.increment_achievement($Text.text, 1)

func _on_submit_gift_code_button_down() -> void:
	GodotTDS.submit_gift_code($Text.text)


func _on_load_splash_ad_button_down() -> void:
	GodotTDS.load_splash_ad(1038037)


func _on_show_splash_ad_button_down() -> void:
	GodotTDS.show_splash_ad()


func _on_dispose_splash_ad_button_down() -> void:
	GodotTDS.dispose_splash_ad()


func _on_load_reward_video_ad_button_down() -> void:
	var data : GodotTDS.RewardVideoAdData = GodotTDS.RewardVideoAdData.new()
	data.space_id = 1037811
	GodotTDS.load_reward_video_ad(data)


func _on_show_reward_video_ad_button_down() -> void:
	GodotTDS.show_reward_video_ad()


func _on_load_banner_ad_button_down() -> void:
	GodotTDS.load_banner_ad(1038038)


func _on_show_banner_ad_button_down() -> void:
	GodotTDS.show_banner_ad(GodotTDS.GRAVITY_BOTTOM)


func _on_load_feed_ad_button_down() -> void:
	GodotTDS.load_feed_ad(1038039, "原神")


func _on_show_feed_ad_button_down() -> void:
	GodotTDS.show_feed_ad(GodotTDS.GRAVITY_BOTTOM)


func _on_load_interstitial_ad_button_down() -> void:
	GodotTDS.load_interstitial_ad(1038040)


func _on_show_interstitial_ad_button_down() -> void:
	GodotTDS.show_interstitial_ad()


func _on_open_user_page_button_down() -> void:
	GodotTDS.show_tap_user_profile($Text.text)


func _on_open_leaderboard_button_down() -> void:
	GodotTDS.open_leaderboard($Text.text, 0)


func _on_submit_leaderboard_score_button_down() -> void:
	print_debug($Text.text)
	GodotTDS.submit_leaderboard_score($Text.text, 100)


func _on_load_leaderboard_scores_button_down() -> void:
	GodotTDS.load_leaderboard_scores($Text.text, 0)


func _on_load_current_player_leaderboard_score_button_down() -> void:
	GodotTDS.load_current_player_leaderboard_score($Text.text, 0)


func _on_load_player_centered_leaderboard_scores_button_down() -> void:
	GodotTDS.load_player_centered_leaderboard_scores($Text.text, 0, "weekly", 100)
