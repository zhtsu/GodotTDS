package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.StateCode
import cc.zhtsu.godot_tds_plugin.TapTDS
import com.tapsdk.antiaddictionui.AntiAddictionUIKit
import com.tapsdk.bootstrap.Callback
import com.tapsdk.bootstrap.TapBootstrap
import com.tapsdk.bootstrap.account.TDSUser
import com.tapsdk.bootstrap.exceptions.TapError
import com.tapsdk.tapconnect.TapConnect
import com.taptap.sdk.TapLoginHelper
import com.tds.achievement.TapAchievement
import com.tds.common.entities.TapConfig
import com.tds.common.models.TapRegionType

class Account(activity : Activity, godotTdsPlugin: GodotTdsPlugin) : TapTDS
{
    override var _activity : Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    private lateinit var _logInCallback : Callback<TDSUser>

    fun init(clientId : String, clientToken : String, serverUrl : String)
    {
        _activity.runOnUiThread {
            val tdsConfig = TapConfig.Builder()
                .withAppContext(_activity)
                .withClientId(clientId)
                .withClientToken(clientToken)
                .withServerUrl(serverUrl)
                .withRegionType(TapRegionType.CN)
                .build()

            TapBootstrap.init(_activity, tdsConfig)
        }

        _initCallbacks()
    }

    fun logIn()
    {
        TDSUser.loginWithTapTap(_activity, _logInCallback)
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

    fun getUserProfile() : String
    {
        return if (TDSUser.currentUser() != null)
        {
            TapLoginHelper.getCurrentProfile().toJsonString()
        }
        else
        {
            "{}"
        }
    }

    fun getUserObjectId() : String
    {
        return if (TDSUser.currentUser() != null)
            TDSUser.currentUser().objectId
        else
            "null"
    }

    fun setEntryVisible(visible : Boolean)
    {
        _activity.runOnUiThread {
            TapConnect.setEntryVisible(visible)
        }
    }

    fun _initCallbacks()
    {
        _logInCallback = object : Callback<TDSUser>
        {
            override fun onSuccess(user : TDSUser)
            {
                _showToast("Log in successful")
                _godotTdsPlugin.emitPluginSignal("onLogInReturn", StateCode.LOG_IN_SUCCESS, user.toJSONInfo())

                // Reinit achievement data
                TapAchievement.initData()
            }

            override fun onFail(error : TapError)
            {
                _showToast("Log in failed")
                _godotTdsPlugin.emitPluginSignal("onLogInReturn", error.code, error.message.toString())
            }
        }
    }
}