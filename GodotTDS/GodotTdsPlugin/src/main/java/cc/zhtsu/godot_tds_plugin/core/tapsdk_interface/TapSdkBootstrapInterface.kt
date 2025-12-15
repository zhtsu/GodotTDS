package cc.zhtsu.godot_tds_plugin.core.tapsdk_interface

import com.taptap.sdk.core.TapTapLanguage

interface TapSdkBootstrapInterface
{
    fun initialize(
        clientId: String,
        clientToken: String,
        region: Int,
        preferredLanguage: TapTapLanguage,
        enableLog: Boolean
    )
}