package cc.zhtsu.godot_tds_plugin

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.tds.achievement.TapAchievementBean
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot
import org.json.JSONObject

class GodotTdsPlugin(godot : Godot) : GodotPlugin(godot)
{
    override fun getPluginName() = "GodotTdsPlugin"

    override fun getPluginSignals() : MutableSet<SignalInfo>
    {
        return mutableSetOf(
            SignalInfo("onLogInReturn", Integer::class.java, String::class.java),
            SignalInfo("onAntiAddictionReturn", Integer::class.java, String::class.java),
            SignalInfo("onTapMomentReturn", Integer::class.java, String::class.java),
            SignalInfo("OnAchievementReturn", Integer::class.java, String::class.java),
            SignalInfo("OnGiftReturn", Integer::class.java, String::class.java),
            SignalInfo("OnLeaderboardReturn", Integer::class.java, String::class.java),
            SignalInfo("OnGameSaveReturn", Integer::class.java, String::class.java),
            SignalInfo("OnTapLinkReturn", Integer::class.java, String::class.java)
        )
    }

    private val _tapAccount = Account(activity!!, this)
    private val _tapAntiAddiction = AntiAddiction(activity!!, this)
    private val _tapMoment = Moment(activity!!, this)
    private val _tapAchievement = Achievement(activity!!, this)
    private val _tapGift = Gift(activity!!, this)
    private val _tapLeaderboard = Leaderboard(activity!!, this)
    private val _tapGameSave = GameSave(activity!!, this)

    private var _showTipsToast : Boolean = true

    @UsedByGodot
    fun init(clientId : String, clientToken : String, serverUrl : String)
    {
        _tapAccount.init(clientId, clientToken, serverUrl)
        _tapAntiAddiction.init(clientId)
        _tapMoment.init()
        _tapAchievement.init()
        _tapGift.init(clientId)
        _tapLeaderboard.init()
        _tapGameSave.init()
    }

    @UsedByGodot
    fun logIn()
    {
        _tapAccount.logIn()
    }

    @UsedByGodot
    fun logOut()
    {
        _tapAccount.logOut()
    }

    @UsedByGodot
    fun getUserProfile() : String
    {
        return _tapAccount.getUserProfile()
    }

    @UsedByGodot
    fun getUserObjectId() : String
    {
        return _tapAccount.getUserObjectId()
    }

    @UsedByGodot
    fun isLoggedIn() : Boolean
    {
        return _tapAccount.isLoggedIn()
    }

    @UsedByGodot
    fun antiAddiction()
    {
        _tapAntiAddiction.startUpWithTapTap()
    }

    @UsedByGodot
    fun setShowTipsToast(show : Boolean)
    {
        _showTipsToast = show
    }

    @UsedByGodot
    fun tapMoment(orientation : Int)
    {
        _tapMoment.showPage(orientation)
    }

    @UsedByGodot
    fun setEntryVisible(visible : Boolean)
    {
        _tapAccount.setEntryVisible(visible)
    }

    @UsedByGodot
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun fetchAllAchievementList()
    {
        _tapAchievement.fetchAllAchievementList()
    }

    @UsedByGodot
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getLocalAllAchievementList() : String
    {
        val allAchievementList : List<TapAchievementBean> = _tapAchievement.getLocalAllAchievementList()
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
        _tapAchievement.showAchievementPage()
    }

    @UsedByGodot
    fun reachAchievement(displayId : String)
    {
        _tapAchievement.reachAchievement(displayId)
    }

    @UsedByGodot
    fun growAchievementSteps(displayId : String, steps : Int)
    {
        _tapAchievement.growAchievementSteps(displayId, steps)
    }

    @UsedByGodot
    fun makeAchievementSteps(displayId : String, steps : Int)
    {
        _tapAchievement.makeAchievementSteps(displayId, steps)
    }

    @UsedByGodot
    fun setShowAchievementToast(show : Boolean)
    {
        _tapAchievement.setShowAchievementToast(show)
    }

    @UsedByGodot
    fun submitGiftCode(giftCode : String)
    {
        _tapGift.submitGiftCode(giftCode)
    }

    @UsedByGodot
    fun submitLeaderboardScore(leaderboardName : String, score : Long)
    {
        _tapLeaderboard.submitLeaderboardScore(leaderboardName, score)
    }

    @UsedByGodot
    fun fetchLeaderboardSectionRankings(leaderboardName : String, start : Int, end : Int)
    {
        _tapLeaderboard.fetchLeaderboardSectionRankings(leaderboardName, start, end)
    }

    @UsedByGodot
    fun fetchLeaderboardUserAroundRankings(leaderboardName : String, count : Int)
    {
        _tapLeaderboard.fetchLeaderboardUserAroundRankings(leaderboardName, count)
    }

    @UsedByGodot
    fun submitGameSave(name : String, summary : String, playedTime : Long, progressValue : Int, coverPath : String, gameFilePath : String, modifiedAt : Long)
    {
        _tapGameSave.submitGameSave(name, summary, playedTime, progressValue, coverPath, gameFilePath, modifiedAt)
    }

    @UsedByGodot
    fun fetchGameSaves()
    {
        _tapGameSave.fetchGameSaves()
    }

    @UsedByGodot
    fun deleteGameSave(gameSaveId : String)
    {
        _tapGameSave.deleteGameSave(gameSaveId)
    }

    @UsedByGodot
    fun pushLog(msg : String, error : Boolean)
    {
        if (error)
        {
            Log.e("GodotTdsPlugin", msg)
        }
        else
        {
            Log.v("GodotTdsPlugin", msg)
        }
    }

    @UsedByGodot
    fun getCacheDirPath() : String
    {
        return activity!!.baseContext.cacheDir.absolutePath
    }

    fun getShowTipsToast() : Boolean { return _showTipsToast }

    // Useful for emit signal
    fun emitPluginSignal(signal : String, code : Int, msg : String)
    {
        emitSignal(signal, code, msg)
    }
}