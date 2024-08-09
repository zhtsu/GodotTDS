package cc.zhtsu.godot_tds_plugin.tapadn

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.TapAD
import com.tapsdk.tapad.TapAdNative

class FeedAD(activity : Activity, godotTdsPlugin : GodotTdsPlugin, tapAdNative : TapAdNative) : TapAD
{
    override var _activity: Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin
    override val _tapAdNative = tapAdNative

    init
    {
        _initCallbacks()
    }

    override fun load(
        spaceId : Int,
        query : String,
        rewardName : String,
        rewardAmount : Int,
        extraInfo : String,
        gameUserId : String
    )
    {

    }

    override fun show()
    {

    }

    private fun _initCallbacks()
    {

    }
}