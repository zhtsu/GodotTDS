package cc.zhtsu.godot_tds_plugin

import android.widget.Toast
import com.tapsdk.antiaddiction.Config
import com.tapsdk.antiaddictionui.AntiAddictionUICallback
import com.tapsdk.antiaddictionui.AntiAddictionUIKit
import com.tapsdk.moment.TapMoment.TapMomentCallback
import com.tapsdk.bootstrap.Callback
import com.tapsdk.bootstrap.TapBootstrap
import com.tapsdk.bootstrap.account.TDSUser
import com.tapsdk.bootstrap.exceptions.TapError
import com.tapsdk.moment.TapMoment
import com.tapsdk.tapconnect.TapConnect
import com.taptap.sdk.TapLoginHelper
import com.tds.common.entities.TapConfig
import com.tds.common.models.TapRegionType


class TapSDK {

    private val SUCCESS_CODE : Int = 200
    private val EMPTY_MSG : String = "{}"

    private lateinit var _activity : android.app.Activity
    private lateinit var _clientId : String
    private lateinit var _clientToken : String
    private lateinit var _godotTdsPlugin : GodotTdsPlugin
    private lateinit var _antiAddictionUICallback : AntiAddictionUICallback
    private lateinit var _tapMomentCallback : TapMomentCallback

    fun init(activity : android.app.Activity,
             clientId : String,
             clientToken : String,
             serverUrl : String,
             godotTdsPlugin : GodotTdsPlugin)
    {
        _activity = activity
        _clientId = clientId
        _clientToken = clientToken
        _godotTdsPlugin = godotTdsPlugin

        _antiAddictionUICallback = AntiAddictionUICallback { code, _ ->
            godotTdsPlugin.emitPluginSignal("onAntiAddictionReturn", code, EMPTY_MSG)
        }

        _tapMomentCallback = TapMomentCallback { code, msg ->
            godotTdsPlugin.emitPluginSignal("onTapMomentReturn", code, msg)
        }

        activity.let {
            // Initialize Login
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

            // Initialize AntiAddiction
            val config = Config.Builder()
                .withClientId(clientId)
                .showSwitchAccount(false)
                .useAgeRange(true)
                .build()

            AntiAddictionUIKit.init(activity, config)
            AntiAddictionUIKit.setAntiAddictionCallback(_antiAddictionUICallback)

            // Initialize TapMoment
            TapMoment.setCallback(_tapMomentCallback)
        }
    }

    fun logIn()
    {
        TDSUser.loginWithTapTap(_activity, object : Callback<TDSUser> {
            override fun onSuccess(user : TDSUser?) {
                _showToast("Log in successful")
                user?.let {
                    _godotTdsPlugin.emitPluginSignal("onLogInReturn", SUCCESS_CODE, it.toJSONInfo())
                }
            }

            override fun onFail(error : TapError?) {
                _showToast("Log in failed")
                error?.let {
                    _godotTdsPlugin.emitPluginSignal("onLogInReturn", error.code, error.message.toString())
                }
            }
        })
    }

    fun logOut()
    {
        if (TDSUser.currentUser() != null)
        {
            TDSUser.logOut()
            AntiAddictionUIKit.exit()
            _showToast("Log out successful")
        }
        else
        {
            _showToast("Not log in")
        }
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
            EMPTY_MSG
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
            _showToast("Not log in")
        }
    }

    fun getAgeRange() : Int
    {
        return AntiAddictionUIKit.getAgeRange()
    }

    fun tapMoment(orientation : Int)
    {
        when (orientation) {
            0 -> {
                TapMoment.open(TapMoment.ORIENTATION_DEFAULT)
            }
            1 -> {
                TapMoment.open(TapMoment.ORIENTATION_LANDSCAPE)
            }
            2 -> {
                TapMoment.open(TapMoment.ORIENTATION_PORTRAIT)
            }
            3 -> {
                TapMoment.open(TapMoment.ORIENTATION_SENSOR)
            }
            else -> {
                TapMoment.open(TapMoment.ORIENTATION_DEFAULT)
            }
        }
    }

    fun setEntryVisible(visible : Boolean)
    {
        _activity.runOnUiThread {
            TapConnect.setEntryVisible(visible)
        }
    }

    fun _showToast(msg : String)
    {
        _activity.runOnUiThread {
            if (_godotTdsPlugin.getToastEnabled())
            {
                Toast.makeText(_activity, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}