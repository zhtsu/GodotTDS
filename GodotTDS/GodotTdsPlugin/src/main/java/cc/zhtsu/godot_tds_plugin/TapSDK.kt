package cc.zhtsu.godot_tds_plugin

import android.widget.Toast
import com.tapsdk.bootstrap.Callback
import com.tapsdk.bootstrap.TapBootstrap
import com.tapsdk.bootstrap.account.TDSUser
import com.tapsdk.bootstrap.exceptions.TapError
import com.taptap.sdk.TapLoginHelper
import com.tds.common.entities.TapConfig
import com.tds.common.models.TapRegionType

class TapSDK {

    private lateinit var activity : android.app.Activity
    private lateinit var godotTdsPluginSingleton : GodotTdsPlugin
    private lateinit var loggedInUser : TDSUser

    fun init(mainActivity : android.app.Activity,
             clientId : String,
             clientToken : String,
             serverUrl : String,
             godotTdsPlugin : GodotTdsPlugin)
    {
        activity = mainActivity
        godotTdsPluginSingleton = godotTdsPlugin

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
        }
    }

    fun login()
    {
        TDSUser.loginWithTapTap(activity, object : Callback<TDSUser> {
            override fun onSuccess(user : TDSUser?) {
                if (godotTdsPluginSingleton.getShowPopupTips())
                {
                    Toast.makeText(activity, "Login successful", Toast.LENGTH_SHORT).show()
                }

                user?.let {
                    loggedInUser = it
                    godotTdsPluginSingleton.emitPluginSignal("onLoginSuccess", it.toJSONInfo(), 0)
                }
            }

            override fun onFail(error : TapError?) {
                if (godotTdsPluginSingleton.getShowPopupTips())
                {
                    Toast.makeText(activity, "Login failed", Toast.LENGTH_SHORT).show()
                }

                error?.let {
                    godotTdsPluginSingleton.emitPluginSignal("onLoginFail", error.message.toString(), error.code)
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
}