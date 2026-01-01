package cc.zhtsu.godot_tds_plugin.tapadn

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.GodotTdsPluginModule
import com.tapsdk.tapad.TapAdConfig
import com.tapsdk.tapad.TapAdManager
import com.tapsdk.tapad.TapAdNative
import com.tapsdk.tapad.TapAdSdk

class TapAdnBootstrap(activity: Activity, godotTdsPlugin: GodotTdsPlugin) :
    GodotTdsPluginModule(activity, godotTdsPlugin)
{
    private var _tapAdNative: TapAdNative? = null

    fun initialize(
        mediaId: Long,
        mediaName: String,
        mediaKey: String,
        clientId: String,
        logEnabled: Boolean,
        requestPermissionIfNecessaryEnabled: Boolean
    )
    {
        val config = TapAdConfig.Builder()
            .withMediaId(mediaId)
            .withMediaName(mediaName)
            .withMediaKey(mediaKey)
            .withMediaVersion("1")
            .withGameChannel("taptap2")
            .withTapClientId(clientId)
            .shakeEnabled(false)
            .enableDebug(logEnabled)
            .build()

        TapAdSdk.init(_activity, config)

        // https://github.com/zhtsu/GodotTDS/issues/4
        if (requestPermissionIfNecessaryEnabled)
            TapAdManager.get().requestPermissionIfNecessary(_activity, true)

        _tapAdNative = TapAdManager.get().createAdNative(_activity)
    }

    fun getTapAdNative(): TapAdNative?
    {
        return _tapAdNative
    }
}