package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.core.GodotTdsPluginModule
import cc.zhtsu.godot_tds_plugin.core.tapsdk_interface.TapSdkBootstrapInterface
import com.taptap.sdk.core.TapTapLanguage
import com.taptap.sdk.core.TapTapSdk
import com.taptap.sdk.core.TapTapSdkOptions

class TapSdkBootstrap(activity: Activity, godotTdsPlugin: GodotTdsPlugin) :
    GodotTdsPluginModule(activity, godotTdsPlugin),
    TapSdkBootstrapInterface
{
    override fun initialize(
        clientId: String,
        clientToken: String,
        region: Int,
        preferredLanguage: TapTapLanguage,
        enableLog: Boolean
    )
    {
        val sdkOptions = TapTapSdkOptions(
            clientId = clientId,
            clientToken = clientToken,
            region = region,
            preferredLanguage = preferredLanguage,
            enableLog = enableLog
        )

        _activity.runOnUiThread {
            TapTapSdk.init(_activity, sdkOptions)
        }
    }
}