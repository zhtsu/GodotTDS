package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.StateCode
import cc.zhtsu.godot_tds_plugin.TapTdsInterface
import com.tapsdk.bootstrap.account.TDSUser
import com.tds.achievement.AchievementCallback
import com.tds.achievement.AchievementException
import com.tds.achievement.TapAchievement
import com.tds.achievement.TapAchievementBean
import org.json.JSONObject

class Achievement(activity : Activity, godotTdsPlugin: GodotTdsPlugin) : TapTdsInterface
{
    override var _activity : Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    private lateinit var _achievementCallback : AchievementCallback

    init
    {
        _initCallbacks()
    }

    fun init()
    {
        TapAchievement.registerCallback(_achievementCallback)

        _activity.runOnUiThread {
            if (TDSUser.currentUser() != null)
            {
                TapAchievement.initData()
            }
        }
    }

    fun showAchievementPage()
    {
        if (TDSUser.currentUser() != null)
        {
            TapAchievement.showAchievementPage()
        }
    }

    fun reachAchievement(displayId : String)
    {
        TapAchievement.reach(displayId)
    }

    fun growAchievementSteps(displayId : String, steps : Int)
    {
        TapAchievement.growSteps(displayId, steps)
    }

    fun makeAchievementSteps(displayId : String, steps : Int)
    {
        TapAchievement.makeSteps(displayId, steps)
    }

    fun setShowAchievementToast(show : Boolean)
    {
        TapAchievement.setShowToast(show)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun fetchAllAchievementList()
    {
        TapAchievement.fetchAllAchievementList { achievementList, exception ->
            if (exception != null)
            {
                _godotTdsPlugin.emitPluginSignal("onAchievementReturn", exception.errorCode, exception.message.toString())
            }
            else
            {
                val jsonObject = JSONObject()
                for (achievementBean in achievementList)
                {
                    jsonObject.append("list", achievementBean.toJson())
                }
                _godotTdsPlugin.emitPluginSignal("onAchievementReturn",
                    StateCode.ACHIEVEMENT_LIST_FETCH_SUCCESS, jsonObject.toString())
            }
        }
    }

    fun getLocalAllAchievementList() : List<TapAchievementBean>
    {
        return TapAchievement.getLocalAllAchievementList()
    }

    fun _initCallbacks()
    {
        _achievementCallback = object : AchievementCallback
        {
            override fun onAchievementSDKInitSuccess()
            {
                _godotTdsPlugin.emitPluginSignal("onAchievementReturn",
                    StateCode.ACHIEVEMENT_INIT_SUCCESS,
                    "Achievement initialized successful"
                )
            }

            override fun onAchievementSDKInitFail(exception: AchievementException)
            {
                _godotTdsPlugin.emitPluginSignal("onAchievementReturn",
                    StateCode.ACHIEVEMENT_INIT_FAIL, exception.message.toString())
            }

            override fun onAchievementStatusUpdate(item: TapAchievementBean?, exception: AchievementException?)
            {
                if (exception != null)
                {
                    _godotTdsPlugin.emitPluginSignal("onAchievementReturn",
                        StateCode.ACHIEVEMENT_UPDATE_FAIL, exception.message.toString())
                }

                if (item != null)
                {
                    _godotTdsPlugin.emitPluginSignal("onAchievementReturn",
                        StateCode.ACHIEVEMENT_UPDATE_SUCCESS, item.toJson().toString())
                }
            }
        }
    }
}