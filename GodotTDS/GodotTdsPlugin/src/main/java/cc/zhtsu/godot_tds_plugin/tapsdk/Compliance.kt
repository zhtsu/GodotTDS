package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.core.GodotTdsPluginModule
import cc.zhtsu.godot_tds_plugin.core.tapsdk_interface.ComplianceInterface
import com.taptap.sdk.compliance.TapTapCompliance
import com.taptap.sdk.compliance.TapTapComplianceCallback

class Compliance(activity : Activity, godotTdsPlugin: GodotTdsPlugin):
    GodotTdsPluginModule(activity, godotTdsPlugin),
    ComplianceInterface
{
    private var _complianceCallback : TapTapComplianceCallback = object : TapTapComplianceCallback
    {
        override fun onComplianceResult(code: Int, extra: Map<String, Any>?)
        {
            _godotTdsPlugin.emitPluginSignal("onComplianceReturn", code, extra.toString())
        }
    }

    override fun initialize()
    {
        TapTapCompliance.registerComplianceCallback(_complianceCallback)
    }

    override fun destroy()
    {
        TapTapCompliance.unregisterComplianceCallback(_complianceCallback)
    }

    override fun startup()
    {
        if (_godotTdsPlugin.isLoggedIn())
        {
            val userIdentifier = _godotTdsPlugin.getAccountOpenId()
            TapTapCompliance.startup(activity = _activity, userId = userIdentifier)
        }
    }

    override fun exit()
    {
        TapTapCompliance.exit()
    }

    override fun getAgeRange(): Int
    {
        return TapTapCompliance.getAgeRange()
    }

    override fun getRemainingTime() : Int
    {
        return TapTapCompliance.getRemainingTime()
    }
}