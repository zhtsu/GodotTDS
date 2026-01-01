package cc.zhtsu.godot_tds_plugin.tapadn

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.StateCode
import cc.zhtsu.godot_tds_plugin.GodotTdsPluginModule
import com.tapsdk.tapad.AdRequest
import com.tapsdk.tapad.TapAdNative
import com.tapsdk.tapad.TapRewardVideoAd

class RewardVideoAd(activity : Activity, godotTdsPlugin : GodotTdsPlugin) :
    GodotTdsPluginModule(activity, godotTdsPlugin)
{
    private var _rewardVideoAd : TapRewardVideoAd? = null

    fun load(
        spaceId: Int,
        rewardName: String,
        rewardAmount: Int,
        extraInfo: String,
        gameUserId: String
    )
    {
        val adRequest = AdRequest.Builder()
            .withSpaceId(spaceId.toLong())
            .withRewardName(rewardName)
            .withRewardAmount(rewardAmount)
            .withExtra1(extraInfo)
            .withUserId(gameUserId)
            .build()

        _godotTdsPlugin.getTapAdnBootstrap().getTapAdNative()?.loadRewardVideoAd(adRequest, _rewardVideoAdListener)
    }

    fun show()
    {
        if (_rewardVideoAd != null)
        {
            _rewardVideoAd!!.setRewardAdInteractionListener(_rewardVideoAdInteractionListener)

            _activity.runOnUiThread {
                _rewardVideoAd!!.showRewardVideoAd(_activity)
            }
        }
        else
        {
            _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", StateCode.AD_REWARD_VIDEO_LOAD_FAIL, "Reward video AD is not loaded")
        }
    }

    private var _rewardVideoAdListener : TapAdNative.RewardVideoAdListener = object : TapAdNative.RewardVideoAdListener
    {
        override fun onError(code : Int, msg : String)
        {
            _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", code, "RewardVideoAD error: $msg")
        }

        override fun onRewardVideoAdLoad(rewardVideoAd : TapRewardVideoAd)
        {
            _rewardVideoAd = rewardVideoAd
            _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", StateCode.AD_REWARD_VIDEO_LOAD_SUCCESS, rewardVideoAd.toString())
        }

        override fun onRewardVideoCached(rewardVideoAd : TapRewardVideoAd)
        {
            _rewardVideoAd = rewardVideoAd
            _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", StateCode.AD_REWARD_VIDEO_CACHE_SUCCESS, rewardVideoAd.toString())
        }
    }

    private var _rewardVideoAdInteractionListener : TapRewardVideoAd.RewardAdInteractionListener = object : TapRewardVideoAd.RewardAdInteractionListener
    {
        override fun onAdShow(p0: TapRewardVideoAd?)
        {
            _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", StateCode.AD_REWARD_VIDEO_SHOWN, "")
        }

        override fun onAdClose(p0: TapRewardVideoAd?)
        {
            _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", StateCode.AD_REWARD_VIDEO_CLOSED, "")
        }

        override fun onVideoComplete(p0: TapRewardVideoAd?)
        {
            _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", StateCode.AD_REWARD_VIDEO_COMPLETED, "")
        }

        override fun onVideoError(p0: TapRewardVideoAd?)
        {
            _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", StateCode.AD_REWARD_VIDEO_ERROR, "")
        }

        override fun onRewardVerify(p0: TapRewardVideoAd?, rewardVerify : Boolean, rewardAmount : Int, rewardName : String, code : Int, msg: String)
        {
            _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", StateCode.AD_REWARD_VIDEO_VERIFIED, "")
        }

        override fun onSkippedVideo(p0: TapRewardVideoAd?)
        {
            _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", StateCode.AD_REWARD_VIDEO_SKIPPED, "")
        }

        override fun onAdClick(p0: TapRewardVideoAd?)
        {
            _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", StateCode.AD_REWARD_VIDEO_CLICKED, "")
        }

        override fun onAdValidShow(p0: TapRewardVideoAd?)
        {
        }
    }
}