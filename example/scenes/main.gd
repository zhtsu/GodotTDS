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
	GodotTDS.start_up_compliance()


func _on_tap_moment_button_down() -> void:
	GodotTDS.tap_moment()


func _on_logout_button_down() -> void:
	GodotTDS.logout()


func _on_get_user_profile_button_down() -> void:
	$Text.text = GodotTDS.get_user_profile()


func _on_achievement_page_button_down() -> void:
	GodotTDS.show_achievement_page()


func _on_unlock_achievement_button_down() -> void:
	GodotTDS.unlock_achievement("robot_dash_02")


func _on_grow_achievement_button_down() -> void:
	GodotTDS.grow_achievement_steps("robot_dash_04", 1)


var show_achievement_toast : bool = true

func _on_achievement_toast_button_down() -> void:
	show_achievement_toast = not show_achievement_toast
	if show_achievement_toast:
		$TabContainer/TapSDK/AchievementToast.text = "成就弹窗(开)"
		GodotTDS.set_show_achievement_toast(show_achievement_toast)
	else:
		$TabContainer/TapSDK/AchievementToast.text = "成就弹窗(关)"
		GodotTDS.set_show_achievement_toast(show_achievement_toast)


func _on_submit_gift_code_button_down() -> void:
	GodotTDS.submit_gift_code("114514")


func _on_sync_achievement_button_down() -> void:
	$Text.text = str(GodotTDS.get_network_all_achievement_list())


func _on_submit_leaderboard_score_button_down() -> void:
	GodotTDS.submit_leaderboard_score("Score", 100)


func _on_fetch_section_rankings_button_down() -> void:
	GodotTDS.fetch_leaderboard_section_rankings("Score", 0, 10)


func _on_fetch_user_ranking_button_down() -> void:
	GodotTDS.fetch_leaderboard_user_around_rankings("Score")


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
