package cc.zhtsu.godot_tds_plugin.tapadn

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.StateCode
import cc.zhtsu.godot_tds_plugin.TapAD
import com.tapsdk.tapad.AdRequest
import com.tapsdk.tapad.TapAdNative
import com.tapsdk.tapad.TapInterstitialAd

class InterstitialAD(activity : Activity, godotTdsPlugin : GodotTdsPlugin) : TapAD
{
    override var _activity: Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    private lateinit var _interstitialAdListener : TapAdNative.InterstitialAdListener
    private lateinit var _interstitialAdInteractionListener : TapInterstitialAd.InterstitialAdInteractionListener

    private var _interstitialAd : TapInterstitialAd? = null

    init
    {
        _initCallbacks()
    }

    fun load(spaceId : Int)
    {
        val adRequest = AdRequest.Builder()
            .withSpaceId(spaceId)
            .build()

        _godotTdsPlugin.getTapAdNative().loadInterstitialAd(adRequest, _interstitialAdListener)
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

    private fun _initCallbacks()
    {
        _interstitialAdListener = object : TapAdNative.InterstitialAdListener
        {
            override fun onInterstitialAdLoad(tapInterstitialAd: TapInterstitialAd)
            {
                _interstitialAd = tapInterstitialAd
                _godotTdsPlugin.emitPluginSignal("onInterstitialAdReturn", StateCode.AD_INTERSTITIAL_LOAD_SUCCESS, tapInterstitialAd.toString())
            }

            override fun onError(code : Int, msg : String)
            {
                _godotTdsPlugin.emitPluginSignal("onInterstitialAdReturn", code, msg)
            }
        }

        _interstitialAdInteractionListener = object : TapInterstitialAd.InterstitialAdInteractionListener
        {
            override fun onAdShow()
            {
                _godotTdsPlugin.emitPluginSignal("onInterstitialAdReturn", StateCode.AD_INTERSTITIAL_SHOWED, "")
            }

            override fun onAdClose()
            {
                _godotTdsPlugin.emitPluginSignal("onInterstitialAdReturn", StateCode.AD_INTERSTITIAL_CLOSED, "")
            }

            override fun onAdError()
            {
                _godotTdsPlugin.emitPluginSignal("onInterstitialAdReturn", StateCode.AD_INTERSTITIAL_ERROR, "")
            }
        }
    }
}