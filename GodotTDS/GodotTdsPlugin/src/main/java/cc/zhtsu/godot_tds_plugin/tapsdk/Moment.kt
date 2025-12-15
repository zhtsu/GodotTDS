package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.core.GodotTdsPluginModule
import cc.zhtsu.godot_tds_plugin.core.tapsdk_interface.MomentInterface
import com.taptap.sdk.moment.TapTapMoment

class Moment(activity : Activity, godotTdsPlugin: GodotTdsPlugin) :
    GodotTdsPluginModule(activity, godotTdsPlugin),
    MomentInterface
{
    fun init()
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