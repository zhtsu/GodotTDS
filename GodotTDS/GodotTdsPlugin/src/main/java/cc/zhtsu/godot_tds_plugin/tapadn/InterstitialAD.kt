package cc.zhtsu.godot_tds_plugin.tapadn

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.TapAD

class InterstitialAD(activity : Activity, godotTdsPlugin : GodotTdsPlugin) : TapAD
{
    override var _activity: Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    init
    {
        _initCallbacks()
    }

    fun load(
        spaceId : Int,
        query : String,
        rewardName : String,
        rewardAmount : Int,
        extraInfo : String,
        gameUserId : String
    )
    {

    }

    fun show()
    {

    }

    private fun _initCallbacks()
    {

    }
}