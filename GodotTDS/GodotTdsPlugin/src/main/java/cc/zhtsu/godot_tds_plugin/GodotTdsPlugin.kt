package cc.zhtsu.godot_tds_plugin

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import cc.zhtsu.godot_tds_plugin.tapadn.BannerAD
import cc.zhtsu.godot_tds_plugin.tapadn.FeedAD
import cc.zhtsu.godot_tds_plugin.tapadn.InterstitialAD
import cc.zhtsu.godot_tds_plugin.tapadn.RewardVideoAD
import cc.zhtsu.godot_tds_plugin.tapadn.SplashAD
import cc.zhtsu.godot_tds_plugin.tapsdk.Account
import cc.zhtsu.godot_tds_plugin.tapsdk.Achievement
import cc.zhtsu.godot_tds_plugin.tapsdk.AntiAddiction
import cc.zhtsu.godot_tds_plugin.tapsdk.GameSave
import cc.zhtsu.godot_tds_plugin.tapsdk.Gift
import cc.zhtsu.godot_tds_plugin.tapsdk.Leaderboard
import cc.zhtsu.godot_tds_plugin.tapsdk.Moment
import com.tapsdk.tapad.TapAdConfig
import com.tapsdk.tapad.TapAdCustomController
import com.tapsdk.tapad.TapAdManager
import com.tapsdk.tapad.TapAdSdk
import com.tds.achievement.TapAchievementBean
import org.godotengine.godot.Godot
import org.godotengine.godot.GodotFragment
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot
import org.json.JSONObject

class GodotTdsPlugin(godot : Godot) : GodotPlugin(godot)
{
    override fun getPluginName() = "GodotTdsPlugin"

    override fun getPluginSignals(): MutableSet<SignalInfo>
    {
        return mutableSetOf(
            SignalInfo("onLogInReturn", Integer::class.java, String::class.java),
            SignalInfo("onAntiAddictionReturn", Integer::class.java, String::class.java),
            SignalInfo("onTapMomentReturn", Integer::class.java, String::class.java),
            SignalInfo("onAchievementReturn", Integer::class.java, String::class.java),
            SignalInfo("onGiftReturn", Integer::class.java, String::class.java),
            SignalInfo("onLeaderboardReturn", Integer::class.java, String::class.java),
            SignalInfo("onGameSaveReturn", Integer::class.java, String::class.java),
            SignalInfo("onLaunchFromDeepLink", String::class.java),
            SignalInfo("onSplashAdReturn", Integer::class.java, String::class.java)
        )
    }

    private var _showTipsToast: Boolean = true

    private val _tapAccount = Account(activity!!, this)
    private val _tapAntiAddiction = AntiAddiction(activity!!, this)
    private val _tapMoment = Moment(activity!!, this)
    private val _tapAchievement = Achievement(activity!!, this)
    private val _tapGift = Gift(activity!!, this)
    private val _tapLeaderboard = Leaderboard(activity!!, this)
    private val _tapGameSave = GameSave(activity!!, this)

    private val _tapAdNative = TapAdManager.get().createAdNative(activity)
    private lateinit var _tapAdnCallback: TapAdCustomController

    private val _bannerAd = BannerAD(activity!!, this, _tapAdNative)
    private val _feedAd = FeedAD(activity!!, this, _tapAdNative)
    private val _interstitialAd = InterstitialAD(activity!!, this, _tapAdNative)
    private val _rewardVideoAd = RewardVideoAD(activity!!, this, _tapAdNative)
    private val _splashAd = SplashAD(activity!!, this, _tapAdNative)

    @UsedByGodot
    fun init(
        clientId: String, clientToken: String, serverUrl: String,
        mediaId: Long, mediaName: String, mediaKey: String
    )
    {
        _tapAccount.init(clientId, clientToken, serverUrl)
        _tapAntiAddiction.init(clientId)
        _tapMoment.init()
        _tapAchievement.init()
        _tapGift.init(clientId)
        _tapLeaderboard.init()
        _tapGameSave.init()

        _initAdSdk(mediaId, mediaName, mediaKey)
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
    fun getUserProfile(): String
    {
        return _tapAccount.getUserProfile()
    }

    @UsedByGodot
    fun getUserObjectId(): String
    {
        return _tapAccount.getUserObjectId()
    }

    @UsedByGodot
    fun isLoggedIn(): Boolean
    {
        return _tapAccount.isLoggedIn()
    }

    @UsedByGodot
    fun antiAddiction()
    {
        _tapAntiAddiction.startUpWithTapTap()
    }

    @UsedByGodot
    fun setShowTipsToast(show: Boolean)
    {
        _showTipsToast = show
    }

    @UsedByGodot
    fun tapMoment(orientation: Int)
    {
        _tapMoment.showPage(orientation)
    }

    @UsedByGodot
    fun setEntryVisible(visible: Boolean)
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
    fun getLocalAllAchievementList(): String
    {
        val allAchievementList: List<TapAchievementBean> =
            _tapAchievement.getLocalAllAchievementList()
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
    fun reachAchievement(displayId: String)
    {
        _tapAchievement.reachAchievement(displayId)
    }

    @UsedByGodot
    fun growAchievementSteps(displayId: String, steps: Int)
    {
        _tapAchievement.growAchievementSteps(displayId, steps)
    }

    @UsedByGodot
    fun makeAchievementSteps(displayId: String, steps: Int)
    {
        _tapAchievement.makeAchievementSteps(displayId, steps)
    }

    @UsedByGodot
    fun setShowAchievementToast(show: Boolean)
    {
        _tapAchievement.setShowAchievementToast(show)
    }

    @UsedByGodot
    fun submitGiftCode(giftCode: String)
    {
        _tapGift.submitGiftCode(giftCode)
    }

    @UsedByGodot
    fun submitLeaderboardScore(leaderboardName: String, score: Long)
    {
        _tapLeaderboard.submitLeaderboardScore(leaderboardName, score)
    }

    @UsedByGodot
    fun fetchLeaderboardSectionRankings(leaderboardName: String, start: Int, end: Int)
    {
        _tapLeaderboard.fetchLeaderboardSectionRankings(leaderboardName, start, end)
    }

    @UsedByGodot
    fun fetchLeaderboardUserAroundRankings(leaderboardName: String, count: Int)
    {
        _tapLeaderboard.fetchLeaderboardUserAroundRankings(leaderboardName, count)
    }

    @UsedByGodot
    fun submitGameSave(
        name: String,
        summary: String,
        playedTime: Long,
        progressValue: Int,
        coverPath: String,
        gameFilePath: String,
        modifiedAt: Long
    )
    {
        _tapGameSave.submitGameSave(
            name,
            summary,
            playedTime,
            progressValue,
            coverPath,
            gameFilePath,
            modifiedAt
        )
    }

    @UsedByGodot
    fun fetchGameSaves()
    {
        _tapGameSave.fetchGameSaves()
    }

    @UsedByGodot
    fun deleteGameSave(gameSaveId: String)
    {
        _tapGameSave.deleteGameSave(gameSaveId)
    }

    @UsedByGodot
    fun pushLog(msg: String, error: Boolean)
    {
        if (error) {
            Log.e("GodotTdsPlugin", msg)
        } else {
            Log.v("GodotTdsPlugin", msg)
        }
    }

    @UsedByGodot
    fun getCacheDirPath(): String
    {
        return activity!!.baseContext.cacheDir.absolutePath
    }

    @UsedByGodot
    fun loadSplashAd(spaceId : Int)
    {
        _splashAd.load(spaceId)
    }

    @UsedByGodot
    fun showSplashAd()
    {
        _splashAd.show()
    }

    @UsedByGodot
    fun disposeSplashAd()
    {
        _splashAd.dispose()
    }

    override fun onGodotMainLoopStarted()
    {
        val uri = GodotFragment.getCurrentIntent().dataString
        if (uri != null)
        {
            emitSignal("onLaunchFromDeepLink", uri)
        }

        super.onGodotMainLoopStarted()
    }

    fun getShowTipsToast(): Boolean
    {
        return _showTipsToast
    }

    // Useful for emit signal
    fun emitPluginSignal(signal: String, code: Int, msg: String)
    {
        emitSignal(signal, code, msg)
    }

    private fun _initAdSdk(mediaId: Long, mediaName: String, mediaKey: String)
    {
        TapAdManager.get().requestPermissionIfNecessary(activity)

        val config = TapAdConfig.Builder()
            .withMediaId(mediaId)
            .withMediaName(mediaName)
            .withMediaKey(mediaKey)
            .withMediaVersion("1")
            .enableDebug(true)
            .withGameChannel("TapTap")
            .shakeEnabled(false)
            .withCustomController(_tapAdnCallback)
            .build()

        TapAdSdk.init(activity, config)
    }

    private fun initTapAdnCallback()
    {
        _tapAdnCallback = object : TapAdCustomController()
        {
            // https://developer.taptap.cn/docs/sdk/tap-adn/tds-tapad/#%E5%88%9D%E5%A7%8B%E5%8C%96
        }
    }
}