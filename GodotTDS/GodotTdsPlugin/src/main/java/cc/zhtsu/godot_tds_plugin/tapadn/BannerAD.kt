package cc.zhtsu.godot_tds_plugin.tapadn

import android.annotation.SuppressLint
import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.R
import cc.zhtsu.godot_tds_plugin.StateCode
import cc.zhtsu.godot_tds_plugin.TapAD
import com.tapsdk.tapad.AdRequest
import com.tapsdk.tapad.TapAdNative
import com.tapsdk.tapad.TapBannerAd

@SuppressLint("InflateParams", "ResourceType")
class BannerAD(activity : Activity, godotTdsPlugin : GodotTdsPlugin) : TapAD
{
    override var _activity: Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    private lateinit var _bannerAdListener : TapAdNative.BannerAdListener
    private lateinit var _bannerInteractionListener : TapBannerAd.BannerInteractionListener

    private var _bannerAd : TapBannerAd? = null

    init
    {
        _initCallbacks()

        _activity.runOnUiThread {
            val rootView = activity.findViewById<ViewGroup>(android.R.id.content)

            val inflater = LayoutInflater.from(activity)
            val bannerLayout = inflater.inflate(R.layout.banner_container, null) as FrameLayout

            bannerLayout.id = R.layout.banner_container
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )

            rootView.addView(bannerLayout, params)
        }
    }

    fun load(spaceId : Int)
    {
        val adRequest = AdRequest.Builder()
            .withSpaceId(spaceId)
            .build()

        _godotTdsPlugin.getTapAdNative().loadBannerAd(adRequest, _bannerAdListener)
    }

    fun show(gravity : Int, height : Int)
    {
        if (_bannerAd != null)
        {
            _bannerAd!!.setBannerInteractionListener(_bannerInteractionListener)

            val frameLayout = _activity.findViewById<FrameLayout>(R.layout.banner_container)

            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )

            layoutParams.gravity = when (gravity) {
                0 -> Gravity.BOTTOM
                1 -> Gravity.TOP
                else -> Gravity.BOTTOM
            }

            if (height != -1)
            {
                layoutParams.height = height
            }

            _activity.runOnUiThread {
                frameLayout.removeAllViews()
                frameLayout.layoutParams = layoutParams
                frameLayout.addView(_bannerAd!!.bannerView)
            }
        }
        else
        {
            _godotTdsPlugin.emitPluginSignal("onBannerAdReturn", StateCode.AD_BANNER_LOAD_FAIL, "Banner AD is not loaded")
        }
    }

    private fun _initCallbacks()
    {
        _bannerAdListener = object : TapAdNative.BannerAdListener
        {
            override fun onError(code : Int, msg : String)
            {
                _godotTdsPlugin.emitPluginSignal("onBannerAdReturn", code, "BannerAD error: $msg")
            }

            override fun onBannerAdLoad(tapBannerAd : TapBannerAd)
            {
                _bannerAd = tapBannerAd
                _godotTdsPlugin.emitPluginSignal("onBannerAdReturn", StateCode.AD_BANNER_LOAD_SUCCESS, tapBannerAd.toString())
            }
        }

        _bannerInteractionListener = object : TapBannerAd.BannerInteractionListener
        {
            override fun onAdShow()
            {
                _godotTdsPlugin.emitPluginSignal("onBannerAdReturn", StateCode.AD_BANNER_SHOWN, "")
            }

            override fun onAdClose()
            {
                _godotTdsPlugin.emitPluginSignal("onBannerAdReturn", StateCode.AD_BANNER_CLOSED, "")
            }

            override fun onAdClick()
            {
                _godotTdsPlugin.emitPluginSignal("onBannerAdReturn", StateCode.AD_BANNER_CLICKED, "")
            }

            override fun onDownloadClick()
            {
                _godotTdsPlugin.emitPluginSignal("onBannerAdReturn", StateCode.AD_BANNER_DOWNLOAD_CLICKED, "")
            }
        }
    }
}