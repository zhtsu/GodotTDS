package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.core.GodotTdsPluginModule
import cc.zhtsu.godot_tds_plugin.core.tapsdk_interface.AchievementInterface
import com.taptap.sdk.achievement.TapAchievementCallback
import com.taptap.sdk.achievement.TapTapAchievement
import com.taptap.sdk.achievement.TapTapAchievementResult

class Achievement(activity : Activity, godotTdsPlugin: GodotTdsPlugin) :
    GodotTdsPluginModule(activity, godotTdsPlugin),
    AchievementInterface
{
    private var _achievementCallback : TapAchievementCallback = object : TapAchievementCallback
    {
        override fun onAchievementSuccess(code: Int, result: TapTapAchievementResult?)
        {
            _godotTdsPlugin.emitPluginSignal("onAchievementReturn", code, result!!.achievementId)
        }

        override fun onAchievementFailure(achievementId: String, errorCode: Int, errorMessage: String)
        {
            _godotTdsPlugin.emitPluginSignal("onAchievementReturn", errorCode, errorMessage)
        }
    }

    fun init()
    {
        TapTapAchievement.registerCallback(_achievementCallback)
    }

    fun showAchievementPage()
    {
        TapTapAchievement.showAchievements()
    }

    fun unlockAchievement(achievementId : String)
    {
        TapTapAchievement.unlock(achievementId)
    }

    fun growAchievementSteps(achievementId : String, steps : Int)
    {
        TapTapAchievement.increment(achievementId, steps)
    }

    fun setShowAchievementToast(show : Boolean)
    {
        TapTapAchievement.setToastEnable(show)
    }
}