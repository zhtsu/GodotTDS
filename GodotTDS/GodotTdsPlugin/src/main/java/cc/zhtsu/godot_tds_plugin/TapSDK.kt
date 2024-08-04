package cc.zhtsu.godot_tds_plugin

import android.widget.Toast
import com.tapsdk.antiaddiction.Config
import com.tapsdk.antiaddictionui.AntiAddictionUICallback
import com.tapsdk.antiaddictionui.AntiAddictionUIKit
import com.tapsdk.bootstrap.Callback
import com.tapsdk.bootstrap.TapBootstrap
import com.tapsdk.bootstrap.account.TDSUser
import com.tapsdk.bootstrap.exceptions.TapError
import com.taptap.sdk.TapLoginHelper
import com.tds.common.entities.TapConfig
import com.tds.common.models.TapRegionType


class TapSDK {

    private lateinit var _activity : android.app.Activity
    private lateinit var _clientId : String
    private lateinit var _godotTdsPlugin : GodotTdsPlugin
    private lateinit var _antiAddictionUICallback : AntiAddictionUICallback

    fun init(activity : android.app.Activity,
             clientId : String,
             clientToken : String,
             serverUrl : String,
             godotTdsPlugin : GodotTdsPlugin)
    {
        _activity = activity
        _clientId = clientId
        _godotTdsPlugin = godotTdsPlugin

        activity.let {
            activity.runOnUiThread {
                val tdsConfig = TapConfig.Builder()
                    .withAppContext(activity)
                    .withClientId(clientId)
                    .withClientToken(clientToken)
                    .withServerUrl(serverUrl)
                    .withRegionType(TapRegionType.CN)
                    .build()

                TapBootstrap.init(activity, tdsConfig)
            }

            val config = Config.Builder()
                .withClientId(clientId)
                .showSwitchAccount(false)
                .useAgeRange(true)
                .build()

            AntiAddictionUIKit.init(activity, config)
            _antiAddictionUICallback = AntiAddictionUICallback() { code, _ ->
                godotTdsPlugin.emitPluginSignal("onAntiAddictionReturn", code, "null")
            }
            AntiAddictionUIKit.setAntiAddictionCallback(_antiAddictionUICallback)
        }
    }

    fun login()
    {
        TDSUser.loginWithTapTap(_activity, object : Callback<TDSUser> {
            override fun onSuccess(user : TDSUser?) {
                if (_godotTdsPlugin.getShowPopupTips())
                {
                    Toast.makeText(_activity, "Login successful", Toast.LENGTH_SHORT).show()
                }

                user?.let {
                    _godotTdsPlugin.emitPluginSignal("onLoginReturn", 200, it.toJSONInfo())
                }
            }

            override fun onFail(error : TapError?) {
                if (_godotTdsPlugin.getShowPopupTips())
                {
                    Toast.makeText(_activity, "Login failed", Toast.LENGTH_SHORT).show()
                }

                error?.let {
                    _godotTdsPlugin.emitPluginSignal("onLoginReturn", error.code, error.message.toString())
                }
            }
        })
    }

    fun isLoggedIn() : Boolean
    {
        return TDSUser.currentUser() != null
    }

    fun getCurrentProfile() : String
    {
        return if (TDSUser.currentUser() != null) {
            TapLoginHelper.getCurrentProfile().toJsonString()
        } else {
            "{}"
        }
    }

    fun antiAddiction()
    {
        if (TDSUser.currentUser() != null)
        {
            val userIdentifier = TDSUser.currentUser().uuid
            AntiAddictionUIKit.startupWithTapTap(_activity, userIdentifier)
        }
        else
        {
            if (_godotTdsPlugin.getShowPopupTips())
            {
                Toast.makeText(_activity, "Not logged in", Toast.LENGTH_SHORT).show()
            }
        }
    }
}