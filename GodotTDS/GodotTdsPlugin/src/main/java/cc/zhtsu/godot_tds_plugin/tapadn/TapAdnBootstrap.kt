package cc.zhtsu.godot_tds_plugin.tapadn

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.core.GodotTdsPluginModule
import cc.zhtsu.godot_tds_plugin.core.tapadn_interface.TapAdnBootstrapInterface
import com.tapsdk.tapad.TapAdConfig
import com.tapsdk.tapad.TapAdManager
import com.tapsdk.tapad.TapAdNative
import com.tapsdk.tapad.TapAdSdk

class TapAdnBootstrap(activity: Activity, godotTdsPlugin: GodotTdsPlugin) :
    GodotTdsPluginModule(activity, godotTdsPlugin),
    TapAdnBootstrapInterface
{
    private var _tapAdNative: TapAdNative? = null

    override fun initialize(
        mediaId: Long,
        mediaName: String,
        mediaKey: String,
        clientId: String,
        requestPermissionIfNecessaryEnabled: Boolean
    )
    {
        _tapAdNative = TapAdManager.get().createAdNative(_activity)

        // https://github.com/zhtsu/GodotTDS/issues/4
        if (requestPermissionIfNecessaryEnabled)
            TapAdManager.get().requestPermissionIfNecessary(_activity)

        val config = TapAdConfig.Builder()
            .withMediaId(mediaId)
            .withMediaName(mediaName)
            .withMediaKey(mediaKey)
            .withMediaVersion("1")
            .withGameChannel("taptap2")
            .withTapClientId(clientId)
            .shakeEnabled(false)
            .enableDebug(true)
            .build()

        TapAdSdk.init(_activity, config)
    }

    override fun getTapAdNative(): TapAdNative?
    {
        return _tapAdNative
    }
}