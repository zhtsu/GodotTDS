package cc.zhtsu.godot_tds_plugin

import android.os.Build
import androidx.annotation.RequiresApi
import com.tds.achievement.TapAchievementBean
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot
import org.json.JSONObject

class GodotTdsPlugin(godot : Godot) : GodotPlugin(godot) {

    override fun getPluginName() = "GodotTdsPlugin"

    override fun getPluginSignals() : MutableSet<SignalInfo>
    {
        return mutableSetOf(
            SignalInfo("onLogInReturn", Integer::class.java, String::class.java),
            SignalInfo("onAntiAddictionReturn", Integer::class.java, String::class.java),
            SignalInfo("onTapMomentReturn", Integer::class.java, String::class.java),
            SignalInfo("OnAchievementReturn", Integer::class.java, String::class.java),
            SignalInfo("OnGiftReturn", Integer::class.java, String::class.java)
        )
    }

    private var _tapSDK : TapSDK = TapSDK()
    private var _showTipsToast : Boolean = true

    @UsedByGodot
    fun init(clientId : String, clientToken : String, serverUrl : String)
    {
        activity?.let { _tapSDK.init(it, clientId, clientToken, serverUrl, this) }
    }

    @UsedByGodot
    fun logIn()
    {
        _tapSDK.logIn()
    }

    @UsedByGodot
    fun logOut()
    {
        _tapSDK.logOut()
    }

    @UsedByGodot
    fun getUserProfile() : String
    {
        return _tapSDK.getUserProfile()
    }

    @UsedByGodot
    fun getUserObjectId() : String
    {
        return _tapSDK.getUserObjectId()
    }

    @UsedByGodot
    fun isLoggedIn() : Boolean
    {
        return _tapSDK.isLoggedIn()
    }

    @UsedByGodot
    fun antiAddiction()
    {
        _tapSDK.antiAddiction()
    }

    @UsedByGodot
    fun setShowTipsToast(show : Boolean)
    {
        _showTipsToast = show
    }

    @UsedByGodot
    fun tapMoment(orientation : Int)
    {
        _tapSDK.tapMoment(orientation)
    }

    @UsedByGodot
    fun setEntryVisible(visible : Boolean)
    {
        _tapSDK.setEntryVisible(visible)
    }

    @UsedByGodot
    fun fetchAllAchievementList()
    {
        _tapSDK.fetchAllAchievementList()
    }

    @UsedByGodot
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getLocalAllAchievementList() : String
    {
        val allAchievementList : List<TapAchievementBean> = _tapSDK.getLocalAllAchievementList()
        val jsonObject = JSONObject()
        for (achievementBean in allAchievementList)
        {
            jsonObject.append("list", achievementBean.toJson())
        }
        return jsonObject.toString()
    }

    @UsedByGodot
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getNetworkAllAchievementList() : String
    {
        val allAchievementList : List<TapAchievementBean> = _tapSDK.getNetworkAllAchievementList()
        val jsonObject = JSONObject()
        for (achievementBean in allAchievementList)
        {
            jsonObject.append("list", achievementBean.toJson())
        }
        return jsonObject.toString()
    }

    @UsedByGodot
    fun showAchievementPage()
    {
        _tapSDK.showAchievementPage()
    }

    @UsedByGodot
    fun reachAchievement(displayId : String)
    {
        _tapSDK.reachAchievement(displayId)
    }

    @UsedByGodot
    fun growAchievementSteps(displayId : String, steps : Int)
    {
        _tapSDK.growAchievementSteps(displayId, steps)
    }

    @UsedByGodot
    fun makeAchievementSteps(displayId : String, steps : Int)
    {
        _tapSDK.makeAchievementSteps(displayId, steps)
    }

    @UsedByGodot
    fun setShowAchievementToast(show : Boolean)
    {
        _tapSDK.setShowAchievementToast(show)
    }

    @UsedByGodot
    fun submitGiftCode(giftCode : String)
    {
        _tapSDK.submitGiftCode(giftCode)
    }

    fun getShowTipsToast() : Boolean { return _showTipsToast }

    // Useful for emit signal
    fun emitPluginSignal(signal : String, code : Int, msg : String)
    {
        emitSignal(signal, code, msg)
    }
}