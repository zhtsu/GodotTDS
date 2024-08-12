package cc.zhtsu.godot_tds_plugin.tapadn

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.StateCode
import cc.zhtsu.godot_tds_plugin.TapAD
import com.tapsdk.tapad.AdRequest
import com.tapsdk.tapad.TapAdNative
import com.tapsdk.tapad.TapSplashAd

class SplashAD(activity : Activity, godotTdsPlugin : GodotTdsPlugin) : TapAD
{
    override var _activity: Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    private lateinit var _loadListener : TapAdNative.SplashAdListener
    private lateinit var _interactionListener : TapSplashAd.AdInteractionListener

    private var _splashAd : TapSplashAd? = null

    init
    {
        _initCallbacks()
    }

    fun load(spaceId : Int)
    {
        val adRequest = AdRequest.Builder()
            .withSpaceId(spaceId)
            .build()

        _godotTdsPlugin.getTapAdNative().loadSplashAd(adRequest, _loadListener)
    }

    fun show()
    {
        if (_splashAd != null)
        {
            _splashAd!!.setSplashInteractionListener(_interactionListener)

            _activity.runOnUiThread {
                _splashAd!!.show(_activity)
            }
        }
        else
        {
            _godotTdsPlugin.emitPluginSignal("onSplashAdReturn", StateCode.AD_SPLASH_LOAD_FAIL, "Splash AD is not loaded")
        }
    }

    fun dispose()
    {
        if (_splashAd != null)
        {
            _splashAd!!.dispose()

            _activity.runOnUiThread {
                _splashAd!!.destroyView()
            }
        }
    }

    private fun _initCallbacks()
    {
        _loadListener = object : TapAdNative.SplashAdListener
        {
            override fun onError(code : Int, msg : String)
            {
                _godotTdsPlugin.emitPluginSignal("onSplashAdReturn", code, "SplashAD error: $msg")
            }

            override fun onSplashAdLoad(taplashAd : TapSplashAd)
            {
                _splashAd = taplashAd
                _godotTdsPlugin.emitPluginSignal("onSplashAdReturn", StateCode.AD_SPLASH_LOAD_SUCCESS, _splashAd.toString())
            }
        }

        _interactionListener = object : TapSplashAd.AdInteractionListener
        {
            override fun onAdClick()
            {
                _godotTdsPlugin.emitPluginSignal("onSplashAdReturn", StateCode.AD_SPLASH_CLICKED, _splashAd.toString())
            }

            override fun onAdSkip()
            {
                _godotTdsPlugin.emitPluginSignal("onSplashAdReturn", StateCode.AD_SPLASH_SKIPPED, _splashAd.toString())
            }

            override fun onAdTimeOver()
            {
                _godotTdsPlugin.emitPluginSignal("onSplashAdReturn", StateCode.AD_SPLASH_TIME_OVER, _splashAd.toString())
            }
        }
    }
}