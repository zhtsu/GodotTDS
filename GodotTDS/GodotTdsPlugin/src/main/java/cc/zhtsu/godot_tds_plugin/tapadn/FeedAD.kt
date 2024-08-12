package cc.zhtsu.godot_tds_plugin.tapadn

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.TapAD
import com.tapsdk.tapad.TapAdNative
import com.tapsdk.tapad.TapFeedAd.ExpressRenderListener

class FeedAD(activity : Activity, godotTdsPlugin : GodotTdsPlugin) : TapAD
{
    override var _activity: Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    private lateinit var _feedAdListener : TapAdNative.FeedAdListener
    private lateinit var _renderListener : ExpressRenderListener

    init
    {
        _initCallbacks()
    }

    fun load(spaceId : Int, query : String)
    {

    }

    fun show()
    {

    }

    private fun _initCallbacks()
    {

    }
}