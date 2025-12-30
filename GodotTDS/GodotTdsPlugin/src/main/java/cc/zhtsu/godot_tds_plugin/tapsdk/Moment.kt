package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.GodotTdsPluginModule
import com.taptap.sdk.moment.TapTapMoment

class Moment(activity : Activity, godotTdsPlugin: GodotTdsPlugin):
    GodotTdsPluginModule(activity, godotTdsPlugin)
{
    fun initialize()
    {
        TapTapMoment.setCallback(_tapMomentCallback)
    }

    fun openPage()
    {
        TapTapMoment.open()
    }

    private var _tapMomentCallback : TapTapMoment.TapTapMomentCallback = object : TapTapMoment.TapTapMomentCallback
    {
        override fun onCallback(code: Int, msg: String?)
        {
            _godotTdsPlugin.emitPluginSignal("onTapMomentReturn", code, "TapMoment: $msg")
        }
    }
}