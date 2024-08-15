package cc.zhtsu.godot_tds_plugin

import android.os.Build
import android.util.Log
import android.widget.Toast
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
import com.tapsdk.tapad.TapAdNative
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
            SignalInfo("onSplashAdReturn", Integer::class.java, String::class.java),
            SignalInfo("onRewardVideoAdReturn", Integer::class.java, String::class.java),
            SignalInfo("onBannerAdReturn", Integer::class.java, String::class.java),
            SignalInfo("onInterstitialAdReturn", Integer::class.java, String::class.java),
            SignalInfo("onFeedAdReturn", Integer::class.java, String::class.java)
        )
    }

    var clientConfigValid : Boolean = true

    private val _tapAccount = Account(activity!!, this)
    private val _tapAntiAddiction = AntiAddiction(activity!!, this)
    private val _tapMoment = Moment(activity!!, this)
    private val _tapAchievement = Achievement(activity!!, this)
    private val _tapGift = Gift(activity!!, this)
    private val _tapLeaderboard = Leaderboard(activity!!, this)
    private val _tapGameSave = GameSave(activity!!, this)

    private var _tapAdNative : TapAdNative? = null
    private lateinit var _tapAdnCallback : TapAdCustomController

    private val _bannerAd = BannerAD(activity!!, this)
    private val _feedAd = FeedAD(activity!!, this)
    private val _interstitialAd = InterstitialAD(activity!!, this)
    private val _rewardVideoAd = RewardVideoAD(activity!!, this)
    private val _splashAd = SplashAD(activity!!, this)

    @UsedByGodot
    fun init(
        clientId: String, clientToken: String, serverUrl: String,
        mediaId: Long, mediaName: String, mediaKey: String,
    )
    {
        if (clientId == "" || clientToken == "" || serverUrl == "" ||
            mediaId == -1L || mediaName == "" || mediaKey == "")
        {
            clientConfigValid = false
        }

        _checkClientConfig {
            _tapAccount.init(clientId, clientToken, serverUrl)
            _tapAntiAddiction.init(clientId)
            _tapMoment.init()
            _tapAchievement.init()
            _tapGift.init(clientId)
            _tapLeaderboard.init()
            _tapGameSave.init()

            _initAdSdk(mediaId, mediaName, mediaKey, clientId)
        }
    }

    @UsedByGodot
    fun logIn()
    {
        _checkClientConfig {
            _tapAccount.logIn()
        }
    }

    @UsedByGodot
    fun logOut()
    {
        _checkClientConfig {
            _tapAccount.logOut()
        }
    }

    @UsedByGodot
    fun getUserProfile() : String
    {
        var userProfile = ""

        _checkClientConfig {
            userProfile = _tapAccount.getUserProfile()
        }

        return userProfile
    }

    @UsedByGodot
    fun getUserObjectId() : String
    {
        var userObjectId = ""

        _checkClientConfig {
            userObjectId = _tapAccount.getUserObjectId()
        }

        return userObjectId
    }

    @UsedByGodot
    fun isLoggedIn() : Boolean
    {
        var loggedIn = false

        _checkClientConfig {
            loggedIn = _tapAccount.isLoggedIn()
        }

        return loggedIn
    }

    @UsedByGodot
    fun antiAddiction()
    {
        _checkClientConfig {
            _tapAntiAddiction.startUpWithTapTap()
        }
    }

    @UsedByGodot
    fun tapMoment(orientation: Int)
    {
        _checkClientConfig {
            _tapMoment.showPage(orientation)
        }
    }

    @UsedByGodot
    fun setEntryVisible(visible: Boolean)
    {
        _checkClientConfig {
            _tapAccount.setEntryVisible(visible)
        }
    }

    @UsedByGodot
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun fetchAllAchievementList()
    {
        _checkClientConfig {
            _tapAchievement.fetchAllAchievementList()
        }
    }

    @UsedByGodot
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getLocalAllAchievementList() : String
    {
        var msg = ""

        _checkClientConfig {
            val allAchievementList: List<TapAchievementBean> =
                _tapAchievement.getLocalAllAchievementList()
            val jsonObject = JSONObject()
            for (achievementBean in allAchievementList)
            {
                jsonObject.append("list", achievementBean.toJson())
            }
            msg = jsonObject.toString()
        }

        return msg
    }

    @UsedByGodot
    fun showAchievementPage()
    {
        _checkClientConfig {
            _tapAchievement.showAchievementPage()
        }
    }

    @UsedByGodot
    fun reachAchievement(displayId : String)
    {
        _checkClientConfig {
            _tapAchievement.reachAchievement(displayId)
        }
    }

    @UsedByGodot
    fun growAchievementSteps(displayId : String, steps : Int)
    {
        _checkClientConfig {
            _tapAchievement.growAchievementSteps(displayId, steps)
        }
    }

    @UsedByGodot
    fun makeAchievementSteps(displayId : String, steps : Int)
    {
        _checkClientConfig {
            _tapAchievement.makeAchievementSteps(displayId, steps)
        }
    }

    @UsedByGodot
    fun setShowAchievementToast(show : Boolean)
    {
        _checkClientConfig {
            _tapAchievement.setShowAchievementToast(show)
        }
    }

    @UsedByGodot
    fun submitGiftCode(giftCode : String)
    {
        _checkClientConfig {
            _tapGift.submitGiftCode(giftCode)
        }
    }

    @UsedByGodot
    fun submitLeaderboardScore(leaderboardName : String, score : Long)
    {
        _checkClientConfig {
            _tapLeaderboard.submitLeaderboardScore(leaderboardName, score)
        }
    }

    @UsedByGodot
    fun fetchLeaderboardSectionRankings(leaderboardName : String, start : Int, end : Int)
    {
        _checkClientConfig {
            _tapLeaderboard.fetchLeaderboardSectionRankings(leaderboardName, start, end)
        }
    }

    @UsedByGodot
    fun fetchLeaderboardUserAroundRankings(leaderboardName : String, count : Int)
    {
        _checkClientConfig {
            _tapLeaderboard.fetchLeaderboardUserAroundRankings(leaderboardName, count)
        }
    }

    @UsedByGodot
    fun submitGameSave(
        name : String,
        summary : String,
        playedTime : Long,
        progressValue : Int,
        coverPath : String,
        gameFilePath : String,
        modifiedAt : Long,
    )
    {
        _checkClientConfig {
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
    }

    @UsedByGodot
    fun fetchGameSaves()
    {
        _checkClientConfig {
            _tapGameSave.fetchGameSaves()
        }
    }

    @UsedByGodot
    fun deleteGameSave(gameSaveId : String)
    {
        _checkClientConfig {
            _tapGameSave.deleteGameSave(gameSaveId)
        }
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

    @UsedByGodot
    fun loadSplashAd(spaceId : Int)
    {
        _checkClientConfig {
            _splashAd.load(spaceId)
        }
    }

    @UsedByGodot
    fun showSplashAd()
    {
        _checkClientConfig {
            _splashAd.show()
        }
    }

    @UsedByGodot
    fun disposeSplashAd()
    {
        _checkClientConfig {
            _splashAd.dispose()
        }
    }

    @UsedByGodot
    fun loadRewardVideoAd(
        spaceId : Int,
        rewardName : String,
        rewardAmount : Int,
        extraInfo : String,
        gameUserId : String,
    )
    {
        _checkClientConfig {
            _rewardVideoAd.load(spaceId, rewardName, rewardAmount, extraInfo, gameUserId)
        }
    }

    @UsedByGodot
    fun showRewardVideoAd()
    {
        _checkClientConfig {
            _rewardVideoAd.show()
        }
    }

    @UsedByGodot
    fun loadBannerAd(spaceId : Int)
    {
        _checkClientConfig {
            _bannerAd.load(spaceId)
        }
    }

    @UsedByGodot
    fun showBannerAd(gravity : Int, height : Int)
    {
        _checkClientConfig {
            _bannerAd.show(gravity, height)
        }
    }

    @UsedByGodot
    fun disposeBannerAd()
    {
        _checkClientConfig {
            _bannerAd.dispose()
        }
    }

    @UsedByGodot
    fun loadInterstitialAd(spaceId : Int)
    {
        _checkClientConfig {
            _interstitialAd.load(spaceId)
        }
    }

    @UsedByGodot
    fun showInterstitialAd()
    {
        _checkClientConfig {
            _interstitialAd.show()
        }
    }

    @UsedByGodot
    fun loadFeedAd(spaceId : Int, query : String)
    {
        _checkClientConfig {
            _feedAd.load(spaceId, query)
        }
    }

    @UsedByGodot
    fun showFeedAd(gravity : Int, height : Int)
    {
        _checkClientConfig {
            _feedAd.show(gravity, height)
        }
    }

    @UsedByGodot
    fun showToast(msg : String)
    {
        activity!!.runOnUiThread {
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        }
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

    // Useful for emit signal
    fun emitPluginSignal(signal : String, code : Int, msg : String)
    {
        emitSignal(signal, code, msg)
    }

    fun getTapAdNative() : TapAdNative
    {
        return if (_tapAdNative == null)
        {
            TapAdManager.get().createAdNative(activity)
        }
        else
        {
            _tapAdNative!!
        }
    }

    fun _checkClientConfig(block : () -> Unit)
    {
        if (clientConfigValid)
        {
            block()
        }
        else
        {
            Log.e("GodotTdsPlugin", "Invalid client config!")
        }
    }

    private fun _initAdSdk(mediaId : Long, mediaName : String, mediaKey : String, clientId : String)
    {
        TapAdManager.get().requestPermissionIfNecessary(activity)

        _initTapAdnCallback()

        val config = TapAdConfig.Builder()
            .withMediaId(mediaId)
            .withMediaName(mediaName)
            .withMediaKey(mediaKey)
            .withMediaVersion("1")
            .withGameChannel("taptap2")
            .withTapClientId(clientId)
            .shakeEnabled(false)
            .enableDebug(true)
            .withCustomController(_tapAdnCallback)
            .build()

        TapAdSdk.init(activity, config)
    }

    private fun _initTapAdnCallback()
    {
        _tapAdnCallback = object : TapAdCustomController()
        {
            // https://developer.taptap.cn/docs/sdk/tap-adn/tds-tapad/#%E5%88%9D%E5%A7%8B%E5%8C%96
        }
    }
}