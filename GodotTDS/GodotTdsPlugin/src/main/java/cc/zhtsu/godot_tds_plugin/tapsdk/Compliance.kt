package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.GodotTdsPluginModule
import com.taptap.sdk.compliance.TapTapCompliance
import com.taptap.sdk.compliance.TapTapComplianceCallback

class Compliance(activity : Activity, godotTdsPlugin: GodotTdsPlugin):
    GodotTdsPluginModule(activity, godotTdsPlugin)
{
    private var _complianceCallback : TapTapComplianceCallback = object : TapTapComplianceCallback
    {
        override fun onComplianceResult(code: Int, extra: Map<String, Any>?)
        {
            _godotTdsPlugin.emitPluginSignal("onComplianceReturn", code, extra.toString())
        }
    }

    fun initialize()
    {
        TapTapCompliance.registerComplianceCallback(_complianceCallback)
    }

    fun destroy()
    {
        TapTapCompliance.unregisterComplianceCallback(_complianceCallback)
    }

    fun startup()
    {
        if (_godotTdsPlugin.isLoggedIn())
        {
            val userIdentifier = _godotTdsPlugin.getAccountOpenId()
            TapTapCompliance.startup(activity = _activity, userId = userIdentifier)
        }
    }

    fun exit()
    {
        TapTapCompliance.exit()
    }

    fun getAgeRange(): Int
    {
        return TapTapCompliance.getAgeRange()
    }

    fun getRemainingTime() : Int
    {
        return TapTapCompliance.getRemainingTime()
    }
}