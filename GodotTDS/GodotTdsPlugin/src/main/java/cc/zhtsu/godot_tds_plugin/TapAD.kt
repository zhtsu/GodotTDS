package cc.zhtsu.godot_tds_plugin

import android.app.Activity
import com.tapsdk.tapad.TapAdNative

interface TapAD
{
    var _activity : Activity
    var _godotTdsPlugin : GodotTdsPlugin
    val _tapAdNative : TapAdNative

    fun load(
        spaceId : Int,
        // For FeedAd
        query : String = "",
        // For VideoAd
        rewardName : String = "", rewardAmount : Int = 1,
        extraInfo : String = "", gameUserId : String = ""
    )

    fun show()

    fun dispose()
    {

    }
}