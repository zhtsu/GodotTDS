package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.core.GodotTdsPluginModule
import cc.zhtsu.godot_tds_plugin.core.StateCode
import cc.zhtsu.godot_tds_plugin.core.tapsdk_interface.AchievementInterface
import com.taptap.sdk.achievement.TapAchievementCallback
import com.taptap.sdk.achievement.TapTapAchievement
import com.taptap.sdk.achievement.TapTapAchievementResult

class Achievement(activity : Activity, godotTdsPlugin: GodotTdsPlugin):
    GodotTdsPluginModule(activity, godotTdsPlugin),
    AchievementInterface
{
    override fun initialize()
    {
        TapTapAchievement.registerCallback(_achievementCallback)
    }

    override fun destroy()
    {
        TapTapAchievement.unregisterCallback(_achievementCallback)
    }

    override fun showAchievements()
    {
        TapTapAchievement.showAchievements()
    }

    override fun unlock(achievementId : String)
    {
        TapTapAchievement.unlock(achievementId)
    }

    override fun increment(achievementId : String, steps : Int)
    {
        TapTapAchievement.increment(achievementId, steps)
    }

    override fun setToastEnable(enable : Boolean)
    {
        TapTapAchievement.setToastEnable(enable)
    }

    private var _achievementCallback : TapAchievementCallback = object : TapAchievementCallback
    {
        override fun onAchievementSuccess(code: Int, result: TapTapAchievementResult?)
        {
            _godotTdsPlugin.emitPluginSignal("onAchievementReturn", StateCode.UPDATE_ACHIEVEMENT_SUCCESS, result!!.achievementId)
        }

        override fun onAchievementFailure(achievementId: String, errorCode: Int, errorMessage: String)
        {
            _godotTdsPlugin.emitPluginSignal("onAchievementReturn", StateCode.UPDATE_ACHIEVEMENT_FAIL, errorMessage)
        }
    }
}