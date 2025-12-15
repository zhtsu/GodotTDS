package cc.zhtsu.godot_tds_plugin

import android.util.Log
import android.widget.Toast
import cc.zhtsu.godot_tds_plugin.core.tapadn_interface.BannerAdInterface
import cc.zhtsu.godot_tds_plugin.core.tapadn_interface.FeedAdInterface
import cc.zhtsu.godot_tds_plugin.core.tapadn_interface.InterstitialAdInterface
import cc.zhtsu.godot_tds_plugin.core.tapadn_interface.RewardVideoAdInterface
import cc.zhtsu.godot_tds_plugin.core.tapadn_interface.SplashAdInterface
import cc.zhtsu.godot_tds_plugin.core.tapadn_interface.TapAdnBootstrapInterface
import cc.zhtsu.godot_tds_plugin.core.tapsdk_interface.AccountInterface
import cc.zhtsu.godot_tds_plugin.core.tapsdk_interface.AchievementInterface
import cc.zhtsu.godot_tds_plugin.core.tapsdk_interface.ComplianceInterface
import cc.zhtsu.godot_tds_plugin.core.tapsdk_interface.GiftInterface
import cc.zhtsu.godot_tds_plugin.core.tapsdk_interface.LeaderboardInterface
import cc.zhtsu.godot_tds_plugin.core.tapsdk_interface.MomentInterface
import cc.zhtsu.godot_tds_plugin.core.tapsdk_interface.TapSdkBootstrapInterface
import cc.zhtsu.godot_tds_plugin.tapadn.BannerAd
import cc.zhtsu.godot_tds_plugin.tapadn.FeedAd
import cc.zhtsu.godot_tds_plugin.tapadn.InterstitialAd
import cc.zhtsu.godot_tds_plugin.tapadn.RewardVideoAd
import cc.zhtsu.godot_tds_plugin.tapadn.SplashAd
import cc.zhtsu.godot_tds_plugin.tapadn.TapAdnBootstrap
import cc.zhtsu.godot_tds_plugin.tapsdk.Account
import cc.zhtsu.godot_tds_plugin.tapsdk.Achievement
import cc.zhtsu.godot_tds_plugin.tapsdk.Compliance
import cc.zhtsu.godot_tds_plugin.tapsdk.Gift
import cc.zhtsu.godot_tds_plugin.tapsdk.Leaderboard
import cc.zhtsu.godot_tds_plugin.tapsdk.Moment
import cc.zhtsu.godot_tds_plugin.tapsdk.TapSdkBootstrap
import com.tapsdk.tapad.TapAdConfig
import com.tapsdk.tapad.TapAdManager
import com.tapsdk.tapad.TapAdNative
import com.tapsdk.tapad.TapAdSdk
import com.taptap.sdk.core.TapTapLanguage
import com.taptap.sdk.core.TapTapRegion
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot


class GodotTdsPlugin(godot : Godot) : GodotPlugin(godot)
{
    override fun getPluginName() = "GodotTdsPlugin"

    override fun getPluginSignals(): MutableSet<SignalInfo>
    {
        return mutableSetOf(
            SignalInfo("onLogInReturn", Integer::class.java, String::class.java),
            SignalInfo("onComplianceReturn", Integer::class.java, String::class.java),
            SignalInfo("onTapMomentReturn", Integer::class.java, String::class.java),
            SignalInfo("onAchievementReturn", Integer::class.java, String::class.java),
            SignalInfo("onGiftReturn", Integer::class.java, String::class.java),
            SignalInfo("onLeaderboardReturn", Integer::class.java, String::class.java),
            SignalInfo("onSplashAdReturn", Integer::class.java, String::class.java),
            SignalInfo("onRewardVideoAdReturn", Integer::class.java, String::class.java),
            SignalInfo("onBannerAdReturn", Integer::class.java, String::class.java),
            SignalInfo("onInterstitialAdReturn", Integer::class.java, String::class.java),
            SignalInfo("onFeedAdReturn", Integer::class.java, String::class.java)
        )
    }

    private var _clientId: String = "Invalid ClientId"
    private var _requestPermissionIfNecessaryEnabled: Boolean = false

    private var _isTapSDKConfigValid: Boolean = true
    private var _isTapADNConfigValid: Boolean = true

    private val _tapSdkBootstrap: TapSdkBootstrapInterface = TapSdkBootstrap(activity!!, this)
    private val _tapAccount: AccountInterface = Account(activity!!, this)
    private val _tapCompliance: ComplianceInterface = Compliance(activity!!, this)
    private val _tapMoment: MomentInterface = Moment(activity!!, this)
    private val _tapAchievement: AchievementInterface = Achievement(activity!!, this)
    private val _tapGift: GiftInterface = Gift(activity!!, this)
    private val _tapLeaderboard: LeaderboardInterface = Leaderboard(activity!!, this)

    private var _tapAdNative : TapAdNative? = null

    private val _tapAdnBootstrap: TapAdnBootstrapInterface = TapAdnBootstrap(activity!!, this)
    private val _bannerAd: BannerAdInterface = BannerAd(activity!!, this)
    private val _feedAd: FeedAdInterface = FeedAd(activity!!, this)
    private val _interstitialAd: InterstitialAdInterface = InterstitialAd(activity!!, this)
    private val _rewardVideoAd: RewardVideoAdInterface = RewardVideoAd(activity!!, this)
    private val _splashAd: SplashAdInterface = SplashAd(activity!!, this)

    @UsedByGodot
    fun initTapSdk(
        clientId: String,
        clientToken: String,
        logEnabled: Boolean,
        useAgeRangeEnabled: Boolean,
        requestPermissionIfNecessaryEnabled: Boolean
    )
    {
        if (clientId == "" || clientToken == "")
        {
            _isTapSDKConfigValid = false
        }

        _clientId = clientId
        _requestPermissionIfNecessaryEnabled = requestPermissionIfNecessaryEnabled

        _checkTapSdkConfig {
            _tapSdkBootstrap.initialize(
                clientId = clientId,
                clientToken = clientToken,
                region = TapTapRegion.CN,
                preferredLanguage = TapTapLanguage.ZH_HANS,
                enableLog = logEnabled
            )
        }
    }

    @UsedByGodot
    fun initTapAdn(mediaId: Long, mediaName: String, mediaKey: String, clientId: String)
    {
        if (mediaId == -1L || mediaName == "" || mediaKey == "")
        {
            _isTapADNConfigValid = false
        }

        _checkTapSdkConfig {
            _initAdSdk(mediaId, mediaName, mediaKey, clientId)
        }
    }

    @UsedByGodot
    fun login(
        publicProfileEnabled: Boolean,
        userFriendsEnabled: Boolean
    )
    {
        _checkTapSdkConfig {
            _tapAccount.login(publicProfileEnabled, userFriendsEnabled)
        }
    }

    @UsedByGodot
    fun logout()
    {
        _checkTapSdkConfig {
            _tapAccount.logout()
        }
    }

    @UsedByGodot
    fun getCurrentTapAccountAsString() : String
    {
        var userProfile = "Invalid Account"

        _checkTapSdkConfig {
            userProfile = _tapAccount.getCurrentAccountAsString()
        }

        return userProfile
    }

    @UsedByGodot
    fun isLoggedIn() : Boolean
    {
        var loggedIn = false

        _checkTapSdkConfig {
            loggedIn = _tapAccount.isLoggedIn()
        }

        return loggedIn
    }

    @UsedByGodot
    fun startUpCompliance()
    {
        _checkTapSdkConfig {
            _tapCompliance.startUp()
        }
    }

    @UsedByGodot
    fun tapMoment()
    {
        _checkTapSdkConfig {
            _tapMoment.openPage()
        }
    }

    @UsedByGodot
    fun showAchievementPage()
    {
        _checkTapSdkConfig {
            _tapAchievement.showAchievementPage()
        }
    }

    @UsedByGodot
    fun unlockAchievement(achievementId : String)
    {
        _checkTapSdkConfig {
            _tapAchievement.unlockAchievement(achievementId)
        }
    }

    @UsedByGodot
    fun growAchievementSteps(displayId : String, steps : Int)
    {
        _checkTapSdkConfig {
            _tapAchievement.growAchievementSteps(displayId, steps)
        }
    }

    @UsedByGodot
    fun setShowAchievementToast(show : Boolean)
    {
        _checkTapSdkConfig {
            _tapAchievement.setShowAchievementToast(show)
        }
    }

    @UsedByGodot
    fun submitGiftCode(giftCode : String)
    {
        _checkTapSdkConfig {
            _tapGift.submitGiftCode(giftCode)
        }
    }

    @UsedByGodot
    fun submitLeaderboardScore(leaderboardName : String, score : Long)
    {
        _checkTapSdkConfig {
            _tapLeaderboard.submitLeaderboardScore(leaderboardName, score)
        }
    }

    @UsedByGodot
    fun fetchLeaderboardSectionRankings(leaderboardName : String, start : Int, end : Int)
    {
        _checkTapSdkConfig {
            _tapLeaderboard.fetchLeaderboardSectionRankings(leaderboardName, start, end)
        }
    }

    @UsedByGodot
    fun fetchLeaderboardUserAroundRankings(leaderboardName : String, count : Int)
    {
        _checkTapSdkConfig {
            _tapLeaderboard.fetchLeaderboardUserAroundRankings(leaderboardName, count)
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
        _checkTapAdnConfig {
            _splashAd.load(spaceId)
        }
    }

    @UsedByGodot
    fun showSplashAd()
    {
        _checkTapAdnConfig {
            _splashAd.show()
        }
    }

    @UsedByGodot
    fun disposeSplashAd()
    {
        _checkTapAdnConfig {
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
        _checkTapAdnConfig {
            _rewardVideoAd.load(spaceId, rewardName, rewardAmount, extraInfo, gameUserId)
        }
    }

    @UsedByGodot
    fun showRewardVideoAd()
    {
        _checkTapAdnConfig {
            _rewardVideoAd.show()
        }
    }

    @UsedByGodot
    fun loadBannerAd(spaceId : Int)
    {
        _checkTapAdnConfig {
            _bannerAd.load(spaceId)
        }
    }

    @UsedByGodot
    fun showBannerAd(gravity : Int, height : Int)
    {
        _checkTapAdnConfig {
            _bannerAd.show(gravity, height)
        }
    }

    @UsedByGodot
    fun disposeBannerAd()
    {
        _checkTapAdnConfig {
            _bannerAd.dispose()
        }
    }

    @UsedByGodot
    fun loadInterstitialAd(spaceId : Int)
    {
        _checkTapAdnConfig {
            _interstitialAd.load(spaceId)
        }
    }

    @UsedByGodot
    fun showInterstitialAd()
    {
        _checkTapAdnConfig {
            _interstitialAd.show()
        }
    }

    @UsedByGodot
    fun loadFeedAd(spaceId : Int, query : String)
    {
        _checkTapAdnConfig {
            _feedAd.load(spaceId, query)
        }
    }

    @UsedByGodot
    fun showFeedAd(gravity : Int, height : Int)
    {
        _checkTapAdnConfig {
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

    fun getClientId() : String
    {
        return _clientId
    }

    fun getAccountOpenId() : String
    {
        return _tapAccount.getAccountOpenId()
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

    fun _checkTapSdkConfig(block : () -> Unit)
    {
        if (_isTapSDKConfigValid)
        {
            block()
        }
        else
        {
            val msg : String = "Invalid SDK config!"

            showToast(msg);

            Log.e("GodotTdsPlugin", msg)
        }
    }

    fun _checkTapAdnConfig(block : () -> Unit)
    {
        if (_isTapADNConfigValid)
        {
            block()
        }
        else
        {
            val msg : String = "Invalid ADN config!"

            showToast(msg);

            Log.e("GodotTdsPlugin", msg)
        }
    }

    private fun _initAdSdk(mediaId : Long, mediaName : String, mediaKey : String, clientId : String)
    {
        // https://github.com/zhtsu/GodotTDS/issues/4
        if (_requestPermissionIfNecessaryEnabled)
            TapAdManager.get().requestPermissionIfNecessary(activity)

        val config = TapAdConfig.Builder()
            .withMediaId(mediaId)
            .withMediaName(mediaName)
            .withMediaKey(mediaKey)
            .withMediaVersion("1")
            .withGameChannel("taptap2")
            .withTapClientId(clientId)
            .shakeEnabled(false)
            .enableDebug(true)
            .build()

        TapAdSdk.init(activity, config)
    }
}