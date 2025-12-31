package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.GodotTdsPluginModule
import com.taptap.sdk.achievement.options.TapTapAchievementOptions
import com.taptap.sdk.compliance.option.TapTapComplianceOptions
import com.taptap.sdk.core.TapTapLanguage
import com.taptap.sdk.core.TapTapSdk
import com.taptap.sdk.core.TapTapSdkOptions
import com.taptap.sdk.initializer.api.model.ScreenOrientation

class TapSdkBootstrap(activity: Activity, godotTdsPlugin: GodotTdsPlugin):
    GodotTdsPluginModule(activity, godotTdsPlugin)
{
    fun initialize(
        clientId: String,
        clientToken: String,
        region: Int,
        preferredLanguage: TapTapLanguage,
        enableLog: Boolean,
        showSwitchAccountEnabled: Boolean,
        useAgeRangeEnabled: Boolean,
        achievementToastEnabled: Boolean,
        screenOrientation: Int
    )
    {
        val orientation: Int = if (screenOrientation == 0) ScreenOrientation.LANDSCAPE else ScreenOrientation.PORTRAIT

        val sdkOptions = TapTapSdkOptions(
            clientId = clientId,
            clientToken = clientToken,
            region = region,
            screenOrientation = orientation,
            preferredLanguage = preferredLanguage,
            enableLog = enableLog
        )

        val complianceOptions: TapTapComplianceOptions = TapTapComplianceOptions(
            showSwitchAccount = showSwitchAccountEnabled,
            useAgeRange = useAgeRangeEnabled
        )

        val achievementOptionsOptions = TapTapAchievementOptions(
            enableToast = achievementToastEnabled
        )

        TapTapSdk.init(
            context = _activity,
            sdkOptions = sdkOptions,
            options = arrayOf(
                complianceOptions,
                achievementOptionsOptions
            )
        )
    }
}