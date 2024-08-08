package cc.zhtsu.godot_tds_plugin

import android.app.Activity
import com.tapsdk.bootstrap.account.TDSUser
import com.tds.achievement.AchievementCallback
import com.tds.achievement.AchievementException
import com.tds.achievement.TapAchievement
import com.tds.achievement.TapAchievementBean

class Achievement(activity : Activity, godotTdsPlugin: GodotTdsPlugin) : TapTDS
{
    override var _activity : Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    private var _networkAllAchievementList : List<TapAchievementBean> = listOf()

    private lateinit var _achievementCallback : AchievementCallback

    fun init()
    {
        _initCallbacks()
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
        else
        {
            _showToast("Not log in")
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

    fun fetchAllAchievementList()
    {
        TapAchievement.fetchAllAchievementList { achievementList, exception ->
            if (exception != null)
            {
                _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", exception.errorCode, exception.message.toString())
            }
            else
            {
                _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", StateCode.ACHIEVEMENT_LIST_FETCH_SUCCESS, StateCode.EMPTY_MSG)
                _networkAllAchievementList = achievementList
            }
        }
    }

    fun getLocalAllAchievementList() : List<TapAchievementBean>
    {
        return TapAchievement.getLocalAllAchievementList()
    }

    fun getNetworkAllAchievementList() : List<TapAchievementBean>
    {
        return _networkAllAchievementList
    }

    override fun _initCallbacks()
    {
        _achievementCallback = object : AchievementCallback
        {
            override fun onAchievementSDKInitSuccess()
            {
                _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", StateCode.ACHIEVEMENT_INIT_SUCCESS, StateCode.EMPTY_MSG)
            }

            override fun onAchievementSDKInitFail(exception: AchievementException)
            {
                _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", StateCode.ACHIEVEMENT_INIT_FAIL, exception.message.toString())
            }

            override fun onAchievementStatusUpdate(item: TapAchievementBean?, exception: AchievementException?)
            {
                if (exception != null)
                {
                    _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", StateCode.ACHIEVEMENT_UPDATE_FAIL, exception.message.toString())
                }

                if (item != null)
                {
                    _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", StateCode.ACHIEVEMENT_UPDATE_SUCCESS, item.toJson().toString())
                }
            }
        }
    }
}