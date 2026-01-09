package cc.zhtsu.godot_tds_plugin

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Toast
import cc.zhtsu.godot_tds_plugin.tapadn.BannerAd
import cc.zhtsu.godot_tds_plugin.tapadn.FeedAd
import cc.zhtsu.godot_tds_plugin.tapadn.InterstitialAd
import cc.zhtsu.godot_tds_plugin.tapadn.RewardVideoAd
import cc.zhtsu.godot_tds_plugin.tapadn.SplashAd
import cc.zhtsu.godot_tds_plugin.tapadn.TapAdnBootstrap
import cc.zhtsu.godot_tds_plugin.tapsdk.Account
import cc.zhtsu.godot_tds_plugin.tapsdk.Achievement
import cc.zhtsu.godot_tds_plugin.tapsdk.CloudSave
import cc.zhtsu.godot_tds_plugin.tapsdk.Compliance
import cc.zhtsu.godot_tds_plugin.tapsdk.Gift
import cc.zhtsu.godot_tds_plugin.tapsdk.Leaderboard
import cc.zhtsu.godot_tds_plugin.tapsdk.Moment
import cc.zhtsu.godot_tds_plugin.tapsdk.TapSdkBootstrap
import com.taptap.sdk.core.TapTapLanguage
import com.taptap.sdk.core.TapTapRegion
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot


class GodotTdsPlugin(godot: Godot): GodotPlugin(godot)
{
    override fun getPluginName() = "GodotTdsPlugin"

    override fun getPluginSignals(): MutableSet<SignalInfo>
    {
        return mutableSetOf(
            SignalInfo("onLoginReturn", Integer::class.java, String::class.java),
            SignalInfo("onComplianceReturn", Integer::class.java, String::class.java),
            SignalInfo("onTapMomentReturn", Integer::class.java, String::class.java),
            SignalInfo("onAchievementReturn", Integer::class.java, String::class.java),
            SignalInfo("onGiftReturn", Integer::class.java, String::class.java),
            SignalInfo("onLeaderboardReturn", Integer::class.java, String::class.java),
            SignalInfo("onCloudSaveReturn", Integer::class.java, String::class.java),
            SignalInfo("onSplashAdReturn", Integer::class.java, String::class.java),
            SignalInfo("onRewardVideoAdReturn", Integer::class.java, String::class.java),
            SignalInfo("onBannerAdReturn", Integer::class.java, String::class.java),
            SignalInfo("onInterstitialAdReturn", Integer::class.java, String::class.java),
            SignalInfo("onFeedAdReturn", Integer::class.java, String::class.java)
        )
    }

    private var _clientId: String = "Invalid ClientId"

    private var _isTapSDKConfigValid: Boolean = true
    private var _isTapADNConfigValid: Boolean = true

    private var _tapSdkBootstrap = TapSdkBootstrap(activity!!, this)
    private var _tapAccount = Account(activity!!, this)
    private var _tapCompliance = Compliance(activity!!, this)
    private var _tapMoment = Moment(activity!!, this)
    private var _tapAchievement = Achievement(activity!!, this)
    private var _tapGift = Gift(activity!!, this)
    private var _tapLeaderboard = Leaderboard(activity!!, this)
    private var _tapCloudSave = CloudSave(activity!!, this)

    private var _tapAdnBootstrap = TapAdnBootstrap(activity!!, this)
    private var _bannerAd = BannerAd(activity!!, this)
    private var _feedAd = FeedAd(activity!!, this)
    private var _interstitialAd = InterstitialAd(activity!!, this)
    private var _rewardVideoAd = RewardVideoAd(activity!!, this)
    private var _splashAd = SplashAd(activity!!, this)

    @UsedByGodot
    private fun initTapSdk(
        clientId: String,
        clientToken: String,
        logEnabled: Boolean,
        showSwitchAccountEnabled: Boolean,
        useAgeRangeEnabled: Boolean,
        achievementToastEnabled: Boolean,
        screenOrientation: Int
    )
    {
        if (clientId == "" || clientToken == "")
        {
            _isTapSDKConfigValid = false
        }

        _clientId = clientId

        _checkTapSdkConfig {
            _tapSdkBootstrap.initialize(
                clientId = clientId,
                clientToken = clientToken,
                region = TapTapRegion.CN,
                preferredLanguage = TapTapLanguage.ZH_HANS,
                enableLog = logEnabled,
                showSwitchAccountEnabled = showSwitchAccountEnabled,
                useAgeRangeEnabled = useAgeRangeEnabled,
                achievementToastEnabled = achievementToastEnabled,
                screenOrientation = screenOrientation
            )

            _tapAchievement.initialize()
            _tapCompliance.initialize()
            _tapLeaderboard.initialize()
            _tapMoment.initialize()
            _tapCloudSave.initialize()
        }
    }

    @UsedByGodot
    private fun initTapAdn(
        mediaId: Long,
        mediaName: String,
        mediaKey: String,
        clientId: String,
        logEnabled: Boolean,
        requestPermissionIfNecessaryEnabled: Boolean
    )
    {
        if (mediaId == 0L || mediaName == "" || mediaKey == "" || clientId == "")
        {
            _isTapADNConfigValid = false
        }

        _checkTapAdnConfig {
            _tapAdnBootstrap.initialize(
                mediaId,
                mediaName,
                mediaKey,
                clientId,
                logEnabled,
                requestPermissionIfNecessaryEnabled
            )

            _bannerAd.initialize()
            _feedAd.initialize()
        }
    }

    @UsedByGodot
    fun login(publicProfileEnabled: Boolean, userFriendsEnabled: Boolean)
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
    fun getCurrentTapAccountAsString(): String
    {
        var userProfile = "Invalid Account"

        _checkTapSdkConfig {
            userProfile = _tapAccount.getCurrentAccountAsString()
        }

        return userProfile
    }

    @UsedByGodot
    fun isLoggedIn(): Boolean
    {
        var loggedIn = false

        _checkTapSdkConfig {
            loggedIn = _tapAccount.isLoggedIn()
        }

        return loggedIn
    }

    @UsedByGodot
    fun startupCompliance()
    {
        _checkTapSdkConfig {
            _tapCompliance.startup()
        }
    }

    @UsedByGodot
    fun openMomentPage()
    {
        _checkTapSdkConfig {
            _tapMoment.openPage()
        }
    }

    @UsedByGodot
    fun showAchievements()
    {
        _checkTapSdkConfig {
            _tapAchievement.showAchievements()
        }
    }

    @UsedByGodot
    fun unlockAchievement(achievementId: String)
    {
        _checkTapSdkConfig {
            _tapAchievement.unlock(achievementId)
        }
    }

    @UsedByGodot
    fun incrementAchievement(displayId: String, steps: Int)
    {
        _checkTapSdkConfig {
            _tapAchievement.increment(displayId, steps)
        }
    }

    @UsedByGodot
    fun setAchievementToastEnable(enable: Boolean)
    {
        _checkTapSdkConfig {
            _tapAchievement.setToastEnable(enable)
        }
    }

    @UsedByGodot
    fun submitGiftCode(giftCode: String)
    {
        _checkTapSdkConfig {
            _tapGift.submitGiftCode(giftCode)
        }
    }

    @UsedByGodot
    fun submitLeaderboardScore(leaderboardId: String, score: Long)
    {
        _checkTapSdkConfig {
            _tapLeaderboard.submitScore(leaderboardId, score)
        }
    }

    @UsedByGodot
    fun loadLeaderboardScores(leaderboardName: String, leaderboardCollection: Int, nextPage: String)
    {
        _checkTapSdkConfig {
            _tapLeaderboard.loadLeaderboardScores(leaderboardName, leaderboardCollection, nextPage)
        }
    }

    @UsedByGodot
    fun loadCurrentPlayerLeaderboardScore(leaderboardId: String, leaderboardCollection: Int)
    {
        _checkTapSdkConfig {
            _tapLeaderboard.loadCurrentPlayerLeaderboardScore(leaderboardId, leaderboardCollection)
        }
    }

    @UsedByGodot
    fun loadPlayerCenteredLeaderboardScores(leaderboardId: String, leaderboardCollection: Int, periodToken: String, maxCount: Int)
    {
        _checkTapSdkConfig {
            _tapLeaderboard.loadPlayerCenteredScores(leaderboardId, leaderboardCollection, periodToken, maxCount)
        }
    }

    @UsedByGodot
    fun openLeaderboard(leaderboardId: String, leaderboardCollection: Int)
    {
        _checkTapSdkConfig {
            _tapLeaderboard.openLeaderboard(leaderboardId, leaderboardCollection)
        }
    }

    @UsedByGodot
    fun showTapUserProfile(openId: String)
    {
        _checkTapSdkConfig {
            _tapLeaderboard.showTapUserProfile(openId)
        }
    }

    @UsedByGodot
    fun createCloudSaveArchive(
        name: String,
        summary: String,
        extra: String,
        playtime: Int,
        archiveFilePath: String,
        archiveCoverPath: String
    )
    {
        _checkTapSdkConfig {
            _tapCloudSave.createArchive(name, summary, extra, playtime, archiveFilePath, archiveCoverPath)
        }
    }

    @UsedByGodot
    fun getCloudSaveArchiveList()
    {
        _checkTapSdkConfig {
            _tapCloudSave.getArchiveList()
        }
    }

    @UsedByGodot
    fun getCloudSaveArchiveData(archiveUuid: String, archiveFileId: String)
    {
        _checkTapSdkConfig {
            _tapCloudSave.getArchiveData(archiveUuid, archiveFileId)
        }
    }

    @UsedByGodot
    fun updateCloudSaveArchive(
        archiveUuid: String,
        name: String,
        summary: String,
        extra: String,
        playtime: Int,
        archiveFilePath: String,
        archiveCoverPath: String
    )
    {
        _checkTapSdkConfig {
            _tapCloudSave.updateArchive(archiveUuid, name, summary, extra, playtime, archiveFilePath, archiveCoverPath)
        }
    }

    @UsedByGodot
    fun deleteCloudSaveArchive(archiveUuid: String)
    {
        _checkTapSdkConfig {
            _tapCloudSave.deleteArchive(archiveUuid)
        }
    }

    @UsedByGodot
    fun getCloudSaveArchiveCover(archiveUuid: String, archiveFileId: String)
    {
        _checkTapSdkConfig {
            _tapCloudSave.getArchiveCover(archiveUuid, archiveFileId)
        }
    }

    @UsedByGodot
    fun pushLog(msg: String, error: Boolean)
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
    fun getCacheDirPath(): String
    {
        return activity!!.baseContext.cacheDir.absolutePath
    }

    @UsedByGodot
    fun loadSplashAd(spaceId: Int)
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
        spaceId: Int,
        rewardName: String,
        rewardAmount: Int,
        extraInfo: String,
        gameUserId: String,
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
    fun loadBannerAd(spaceId: Int)
    {
        _checkTapAdnConfig {
            _bannerAd.load(spaceId)
        }
    }

    @UsedByGodot
    fun showBannerAd(gravity: Int, height: Int)
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
    fun loadInterstitialAd(spaceId: Int)
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
    fun loadFeedAd(spaceId: Int, query: String)
    {
        _checkTapAdnConfig {
            _feedAd.load(spaceId, query)
        }
    }

    @UsedByGodot
    fun showFeedAd(gravity: Int, height: Int)
    {
        _checkTapAdnConfig {
            _feedAd.show(gravity, height)
        }
    }

    @UsedByGodot
    fun showToast(msg: String)
    {
        activity!!.runOnUiThread {
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun getClientId(): String
    {
        return _clientId
    }

    fun getAccountOpenId(): String
    {
        return _tapAccount.getAccountOpenId()
    }

    fun getTapAdnBootstrap(): TapAdnBootstrap
    {
        return _tapAdnBootstrap
    }

    fun exitCompliance()
    {
        _tapCompliance.exit()
    }

    // Useful for emit signal
    fun emitPluginSignal(signal: String, code: Int, msg: String)
    {
        emitSignal(signal, code, msg)
    }

    private fun _checkTapSdkConfig(block: () -> Unit)
    {
        if (_isTapSDKConfigValid)
        {
            block()
        }
        else
        {
            val msg: String = "Invalid SDK config!"

            showToast(msg);

            Log.e("GodotTdsPlugin", msg)
        }
    }

    private fun _checkTapAdnConfig(block: () -> Unit)
    {
        if (_isTapADNConfigValid)
        {
            block()
        }
        else
        {
            val msg: String = "Invalid ADN config!"

            showToast(msg);

            Log.e("GodotTdsPlugin", msg)
        }
    }

    override fun onMainDestroy()
    {
        _tapAchievement.destroy()
        _tapCompliance.destroy()
        _tapLeaderboard.destroy()
        _tapCloudSave.destroy()
    }
}