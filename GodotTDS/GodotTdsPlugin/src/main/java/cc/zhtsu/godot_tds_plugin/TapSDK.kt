package cc.zhtsu.godot_tds_plugin

import android.widget.Toast
import com.tapsdk.antiaddiction.Config
import com.tapsdk.antiaddictionui.AntiAddictionUICallback
import com.tapsdk.antiaddictionui.AntiAddictionUIKit
import com.tapsdk.bootstrap.Callback
import com.tapsdk.bootstrap.TapBootstrap
import com.tapsdk.bootstrap.account.TDSUser
import com.tapsdk.bootstrap.exceptions.TapError
import com.tapsdk.moment.TapMoment
import com.tapsdk.moment.TapMoment.TapMomentCallback
import com.tapsdk.tapconnect.TapConnect
import com.taptap.sdk.TapLoginHelper
import com.tds.achievement.AchievementCallback
import com.tds.achievement.AchievementException
import com.tds.achievement.TapAchievement
import com.tds.achievement.TapAchievementBean
import com.tds.common.entities.TapConfig
import com.tds.common.models.TapRegionType


class TapSDK {

    private lateinit var _activity : android.app.Activity
    private lateinit var _clientId : String
    private lateinit var _clientToken : String
    private lateinit var _godotTdsPlugin : GodotTdsPlugin
    private lateinit var _antiAddictionUICallback : AntiAddictionUICallback
    private lateinit var _tapMomentCallback : TapMomentCallback
    private lateinit var _achievementCallback : AchievementCallback
    private var _networkAllAchievementList : List<TapAchievementBean> = listOf()

    fun init(
        activity: android.app.Activity,
        clientId: String,
        clientToken: String,
        serverUrl: String,
        godotTdsPlugin: GodotTdsPlugin,
    )
    {
        _activity = activity
        _clientId = clientId
        _clientToken = clientToken
        _godotTdsPlugin = godotTdsPlugin

        _antiAddictionUICallback = AntiAddictionUICallback { code, _ ->
            godotTdsPlugin.emitPluginSignal("onAntiAddictionReturn", code, Code.EMPTY_MSG)
        }

        _tapMomentCallback = TapMomentCallback { code, msg ->
            godotTdsPlugin.emitPluginSignal("onTapMomentReturn", code, msg)
        }

        _achievementCallback = object : AchievementCallback {
            override fun onAchievementSDKInitSuccess() {
                godotTdsPlugin.emitPluginSignal("OnAchievementReturn", Code.ACHIEVEMENT_INIT_SUCCESS, Code.EMPTY_MSG)
            }

            override fun onAchievementSDKInitFail(exception: AchievementException) {
                godotTdsPlugin.emitPluginSignal("OnAchievementReturn", Code.ACHIEVEMENT_INIT_ERROR, exception.message.toString())
            }

            override fun onAchievementStatusUpdate(
                item: TapAchievementBean?,
                exception: AchievementException?,
            ) {
                if (exception != null) {
                    godotTdsPlugin.emitPluginSignal("OnAchievementReturn", Code.ACHIEVEMENT_UPDATE_ERROR, exception.message.toString())
                }

                if (item != null) {
                    godotTdsPlugin.emitPluginSignal("OnAchievementReturn", Code.ACHIEVEMENT_UPDATE_SUCCESS, item.toJson().toString())
                }
            }
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

            // Initialize Achievement
            TapAchievement.registerCallback(_achievementCallback)
        }
    }

    fun logIn()
    {
        TDSUser.loginWithTapTap(_activity, object : Callback<TDSUser> {
            override fun onSuccess(user : TDSUser?) {
                _showToast("Log in successful")
                user?.let {
                    _godotTdsPlugin.emitPluginSignal("onLogInReturn", Code.LOG_IN_SUCCESS, it.toJSONInfo())
                }

                // Reinit achievement data
                TapAchievement.initData()
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
            Code.EMPTY_MSG
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

    fun fetchAllAchievementList()
    {
        TapAchievement.fetchAllAchievementList { achievementList, exception ->
            if (exception != null)
            {
                _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", exception.errorCode, exception.message.toString())
            }
            else
            {
                _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", Code.ACHIEVEMENT_LIST_FETCH_SUCCESS, Code.EMPTY_MSG)
                _networkAllAchievementList = achievementList
            }
        }
    }

    fun getLocalAllAchievementList() : List<TapAchievementBean>
    {
        return TapAchievement.getLocalAllAchievementList()
    }

    fun getNetworkAllAchievementList() : List<TapAchievementBean>
    {
        return _networkAllAchievementList
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