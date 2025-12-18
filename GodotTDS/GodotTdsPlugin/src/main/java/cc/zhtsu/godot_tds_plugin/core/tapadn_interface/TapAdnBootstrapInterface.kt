package cc.zhtsu.godot_tds_plugin.core.tapadn_interface

import com.tapsdk.tapad.TapAdNative

interface TapAdnBootstrapInterface
{
    fun initialize(
        mediaId: Long,
        mediaName: String,
        mediaKey: String,
        clientId: String,
        requestPermissionIfNecessaryEnabled: Boolean
    )
    fun getTapAdNative(): TapAdNative?
}