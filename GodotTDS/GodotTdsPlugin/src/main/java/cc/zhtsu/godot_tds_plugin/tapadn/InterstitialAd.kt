package cc.zhtsu.godot_tds_plugin.tapadn

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.StateCode
import cc.zhtsu.godot_tds_plugin.GodotTdsPluginModule
import com.tapsdk.tapad.AdRequest
import com.tapsdk.tapad.TapAdNative
import com.tapsdk.tapad.TapInterstitialAd

class InterstitialAd(activity : Activity, godotTdsPlugin : GodotTdsPlugin) :
    GodotTdsPluginModule(activity, godotTdsPlugin)
{
    private var _interstitialAd : TapInterstitialAd? = null

    fun load(spaceId : Int)
    {
        val adRequest = AdRequest.Builder()
            .withSpaceId(spaceId.toLong())
            .build()

        _godotTdsPlugin.getTapAdnBootstrap().getTapAdNative()?.loadInterstitialAd(adRequest, _interstitialAdListener)
    }

    fun show()
    {
        if (_interstitialAd != null)
        {
            _interstitialAd!!.setInteractionListener(_interstitialAdInteractionListener)

            _activity.runOnUiThread {
                _interstitialAd!!.show(_activity)
            }
        }
        else
        {
            _godotTdsPlugin.emitPluginSignal("onInterstitialAdReturn", StateCode.AD_INTERSTITIAL_LOAD_FAIL, "Interstitial AD is not loaded")
        }
    }

    private var _interstitialAdListener : TapAdNative.InterstitialAdListener = object : TapAdNative.InterstitialAdListener
    {
        override fun onInterstitialAdLoad(tapInterstitialAd: TapInterstitialAd)
        {
            _interstitialAd = tapInterstitialAd
            _godotTdsPlugin.emitPluginSignal("onInterstitialAdReturn", StateCode.AD_INTERSTITIAL_LOAD_SUCCESS, tapInterstitialAd.toString())
        }

        override fun onError(code : Int, msg : String)
        {
            _godotTdsPlugin.emitPluginSignal("onInterstitialAdReturn", code, "InterstitialAD error: $msg")
        }
    }

    private var _interstitialAdInteractionListener : TapInterstitialAd.InterstitialAdInteractionListener = object : TapInterstitialAd.InterstitialAdInteractionListener
    {
        override fun onAdShow()
        {
            _godotTdsPlugin.emitPluginSignal("onInterstitialAdReturn", StateCode.AD_INTERSTITIAL_SHOWN, "")
        }

        override fun onAdClose()
        {
            _godotTdsPlugin.emitPluginSignal("onInterstitialAdReturn", StateCode.AD_INTERSTITIAL_CLOSED, "")
        }

        override fun onAdError()
        {
            _godotTdsPlugin.emitPluginSignal("onInterstitialAdReturn", StateCode.AD_INTERSTITIAL_ERROR, "")
        }

        override fun onAdValidShow()
        {
        }

        override fun onAdClick()
        {
        }
    }
}