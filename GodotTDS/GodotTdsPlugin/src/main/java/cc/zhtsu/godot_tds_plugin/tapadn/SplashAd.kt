package cc.zhtsu.godot_tds_plugin.tapadn

import android.app.Activity
import android.util.DisplayMetrics
import android.util.Log
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.GodotTdsPluginModule
import cc.zhtsu.godot_tds_plugin.StateCode
import com.tapsdk.tapad.AdRequest
import com.tapsdk.tapad.TapAdManager
import com.tapsdk.tapad.TapAdNative
import com.tapsdk.tapad.TapSplashAd


class SplashAd(activity : Activity, godotTdsPlugin : GodotTdsPlugin) :
    GodotTdsPluginModule(activity, godotTdsPlugin)
{
    private var _splashAd : TapSplashAd? = null

    fun load(spaceId : Int)
    {
        val displayMetrics: DisplayMetrics = _activity.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val adRequest = AdRequest.Builder()
            .withSpaceId(spaceId.toLong())
            .withExpressViewAcceptedSize(screenWidth, screenHeight)
            .build()

        _godotTdsPlugin.getTapAdnBootstrap().getTapAdNative()?.loadSplashAd(adRequest, _loadListener)
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

    private var _loadListener : TapAdNative.SplashAdListener = object : TapAdNative.SplashAdListener
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

    private var _interactionListener : TapSplashAd.AdInteractionListener = object : TapSplashAd.AdInteractionListener
    {
        override fun onAdClick()
        {
            _godotTdsPlugin.emitPluginSignal("onSplashAdReturn", StateCode.AD_SPLASH_CLICKED, _splashAd.toString())
        }

        override fun onAdShow()
        {
        }

        override fun onAdValidShow()
        {
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