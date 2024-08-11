package cc.zhtsu.godot_tds_plugin.tapadn

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.StateCode
import cc.zhtsu.godot_tds_plugin.TapAD
import com.tapsdk.tapad.AdRequest
import com.tapsdk.tapad.TapAdNative
import com.tapsdk.tapad.TapRewardVideoAd

class RewardVideoAD(activity : Activity, godotTdsPlugin : GodotTdsPlugin) : TapAD
{
    override var _activity: Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    private lateinit var _rewardVideoAdListener : TapAdNative.RewardVideoAdListener
    private lateinit var _rewardVideoAdInteractionListener : TapRewardVideoAd.RewardAdInteractionListener

    private var _rewardVideoAd : TapRewardVideoAd? = null

    init
    {
        _initCallbacks()
    }

    fun load(
        spaceId: Int,
        rewardName: String,
        rewardAmount: Int,
        extraInfo: String,
        gameUserId: String,
    )
    {
        val adRequest = AdRequest.Builder()
            .withSpaceId(spaceId)
            .withRewordName(rewardName)
            .withRewordAmount(rewardAmount)
            .withExtra1(extraInfo)
            .withUserId(gameUserId)
            .build()

        _godotTdsPlugin.getTapAdNative().loadRewardVideoAd(adRequest, _rewardVideoAdListener)
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

    private fun _initCallbacks()
    {
        _rewardVideoAdListener = object : TapAdNative.RewardVideoAdListener
        {
            override fun onError(code : Int, msg : String)
            {
                _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", code, msg)
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

        _rewardVideoAdInteractionListener = object : TapRewardVideoAd.RewardAdInteractionListener
        {
            override fun onAdShow()
            {
                _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", StateCode.AD_REWARD_VIDEO_SHOWED, "")
            }

            override fun onAdClose()
            {
                _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", StateCode.AD_REWARD_VIDEO_CLOSED, "")
            }

            override fun onVideoComplete()
            {
                _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", StateCode.AD_REWARD_VIDEO_COMPLETED, "")
            }

            override fun onVideoError()
            {
                _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", StateCode.AD_REWARD_VIDEO_ERROR, "")
            }

            override fun onRewardVerify(rewardVerify : Boolean, rewardAmount : Int, rewardName : String, code : Int, msg: String)
            {
                _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", StateCode.AD_REWARD_VIDEO_VERIFIED, "")
            }

            override fun onSkippedVideo()
            {
                _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", StateCode.AD_REWARD_VIDEO_SKIPPED, "")
            }

            override fun onAdClick()
            {
                _godotTdsPlugin.emitPluginSignal("onRewardVideoAdReturn", StateCode.AD_REWARD_VIDEO_CLICKED, "")
            }
        }
    }
}