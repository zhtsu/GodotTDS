package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.StateCode
import cc.zhtsu.godot_tds_plugin.TapTDS
import com.tapsdk.antiaddiction.Config
import com.tapsdk.antiaddictionui.AntiAddictionUICallback
import com.tapsdk.antiaddictionui.AntiAddictionUIKit
import com.tapsdk.bootstrap.account.TDSUser

class AntiAddiction(activity : Activity, godotTdsPlugin: GodotTdsPlugin) : TapTDS
{
    override var _activity : Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    private lateinit var _antiAddictionUICallback : AntiAddictionUICallback

    fun init(clientId : String)
    {
        val config = Config.Builder()
            .withClientId(clientId)
            .showSwitchAccount(false)
            .useAgeRange(false)
            .build()

        AntiAddictionUIKit.init(_activity, config)

        _initCallbacks()
        AntiAddictionUIKit.setAntiAddictionCallback(_antiAddictionUICallback)
    }

    fun startUpWithTapTap()
    {
        if (TDSUser.currentUser() != null)
        {
            val userIdentifier = TDSUser.currentUser().uuid
            AntiAddictionUIKit.startupWithTapTap(_activity, userIdentifier)
        }
        else
        {
            _showToast("Not log in")
        }
    }

    fun _initCallbacks()
    {
        _antiAddictionUICallback = AntiAddictionUICallback { code, _ ->
            _godotTdsPlugin.emitPluginSignal("onAntiAddictionReturn", code, "Anti addiction authentication failure")
        }
    }
}