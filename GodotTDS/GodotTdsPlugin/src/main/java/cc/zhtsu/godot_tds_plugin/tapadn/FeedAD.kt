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
import cc.zhtsu.godot_tds_plugin.TapTdsInterface
import com.tapsdk.tapad.AdRequest
import com.tapsdk.tapad.TapAdNative
import com.tapsdk.tapad.TapFeedAd
import com.tapsdk.tapad.TapFeedAd.ExpressRenderListener
import com.tapsdk.tapad.feed.FeedOption
import com.tapsdk.tapad.feed.TapFeedAdView
import com.tapsdk.tapad.feed.VideoOption


@SuppressLint("ResourceType", "InflateParams")
class FeedAD(activity : Activity, godotTdsPlugin : GodotTdsPlugin) : TapTdsInterface
{
    override var _activity: Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    private lateinit var _feedAdListener : TapAdNative.FeedAdListener
    private lateinit var _renderListener : ExpressRenderListener

    private val _feedOption = FeedOption.Builder()
        .expressWidth(ViewGroup.LayoutParams.MATCH_PARENT)
        .expressHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        .videoOption(VideoOption.Builder().autoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS).build())
        .build()

    private var _feedAd : TapFeedAd? = null
    private var _gravity : Int = 0
    private var _height : Int = -1

    init
    {
        _initCallbacks()

        _activity.runOnUiThread {
            val rootView = activity.findViewById<ViewGroup>(android.R.id.content)

            val inflater = LayoutInflater.from(activity)
            val bannerLayout = inflater.inflate(R.layout.feed_container, null) as FrameLayout

            bannerLayout.id = R.layout.feed_container
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )

            rootView.addView(bannerLayout, params)
        }
    }

    fun load(spaceId : Int, query : String)
    {
        val adRequest = AdRequest.Builder()
            .withQuery(query)
            .withSpaceId(spaceId)
            .build()

        _godotTdsPlugin.getTapAdNative().loadFeedAd(adRequest, _feedAdListener)
    }

    @SuppressLint("ResourceType")
    fun show(gravity : Int, height : Int)
    {
        if (_feedAd != null)
        {
            _gravity = gravity
            _height = height

            _feedAd!!.setExpressRenderListener(_renderListener)

            _activity.runOnUiThread {
                _feedAd!!.render(_activity, _feedOption)
            }
        }
        else
        {
            _godotTdsPlugin.emitPluginSignal("onFeedAdReturn", StateCode.AD_FEED_LOAD_FAIL, "Feed AD is not loaded")
        }
    }

    private fun _initCallbacks()
    {
        _feedAdListener = object : TapAdNative.FeedAdListener
        {
            override fun onError(code : Int, msg : String)
            {
                _godotTdsPlugin.emitPluginSignal("onFeedAdReturn", code, "FeedAD error: $msg")
            }

            override fun onFeedAdLoad(tapFeeds : MutableList<TapFeedAd>?)
            {
                if (tapFeeds == null || tapFeeds.size == 0)
                {
                    _godotTdsPlugin.emitPluginSignal("onFeedAdReturn", StateCode.AD_FEED_LOAD_FAIL, "Feed AD is not loaded")
                }
                else
                {
                    _feedAd = tapFeeds[0]
                    _godotTdsPlugin.emitPluginSignal("onFeedAdReturn", StateCode.AD_FEED_LOAD_SUCCESS, tapFeeds[0].toString())
                }
            }
        }

        _renderListener = object : ExpressRenderListener
        {
            @SuppressLint("ResourceType")
            override fun onRenderSuccess(tapFeedAdView : TapFeedAdView)
            {
                val frameLayout = _activity.findViewById<FrameLayout>(R.layout.feed_container)

                val layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )

                layoutParams.gravity = when (_gravity) {
                    0 -> Gravity.BOTTOM
                    1 -> Gravity.TOP
                    else -> Gravity.BOTTOM
                }

                if (_height != -1)
                {
                    layoutParams.height = _height
                }

                _activity.runOnUiThread {
                    frameLayout.removeAllViews()
                    frameLayout.layoutParams = layoutParams
                    frameLayout.addView(tapFeedAdView)
                }

                _godotTdsPlugin.emitPluginSignal("onFeedAdReturn", StateCode.AD_FEED_RENDER_SUCCESS, tapFeedAdView.toString())
            }

            override fun onRenderFail(tapFeedAdView : TapFeedAdView, tapFeedAd : TapFeedAd, code : Int, msg : String)
            {
                _godotTdsPlugin.emitPluginSignal("onFeedAdReturn", code, "FeedAD render fail: $msg")
            }

            override fun onAdShow(tapFeedAdView : TapFeedAdView)
            {
                _godotTdsPlugin.emitPluginSignal("onFeedAdReturn", StateCode.AD_FEED_SHOWN, tapFeedAdView.toString())
            }

            override fun onAdClicked(tapFeedAdView : TapFeedAdView)
            {
                _godotTdsPlugin.emitPluginSignal("onFeedAdReturn", StateCode.AD_FEED_CLICKED, tapFeedAdView.toString())
            }

            override fun onAdClosed(tapFeedAdView : TapFeedAdView)
            {
                _godotTdsPlugin.emitPluginSignal("onFeedAdReturn", StateCode.AD_FEED_CLOSED, tapFeedAdView.toString())
            }
        }
    }
}