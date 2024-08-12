package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.TapTDS
import com.tapsdk.moment.TapMoment
import com.tapsdk.moment.TapMoment.TapMomentCallback

class Moment(activity : Activity, godotTdsPlugin: GodotTdsPlugin) : TapTDS
{
    override var _activity : Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    private lateinit var _tapMomentCallback : TapMomentCallback

    fun init()
    {
        _initCallbacks()
        TapMoment.setCallback(_tapMomentCallback)
    }

    fun showPage(orientation : Int)
    {
        when (orientation)
        {
            0 -> {
                TapMoment.open(TapMoment.ORIENTATION_DEFAULT)
            }
            1 -> {
                TapMoment.open(TapMoment.ORIENTATION_LANDSCAPE)
            }
            2 -> {
                TapMoment.open(TapMoment.ORIENTATION_PORTRAIT)
            }
            3 -> {
                TapMoment.open(TapMoment.ORIENTATION_SENSOR)
            }
        }
    }

    fun _initCallbacks()
    {
        _tapMomentCallback = TapMomentCallback { code, msg ->
            _godotTdsPlugin.emitPluginSignal("onTapMomentReturn", code, "TapMoment: $msg")
        }
    }
}