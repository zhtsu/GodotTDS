package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.core.StateCode
import cc.zhtsu.godot_tds_plugin.core.tapsdk_interface.AccountInterface
import cc.zhtsu.godot_tds_plugin.core.GodotTdsPluginModule
import com.taptap.sdk.kit.internal.callback.TapTapCallback
import com.taptap.sdk.kit.internal.exception.TapTapException
import com.taptap.sdk.kit.internal.extensions.toJson
import com.taptap.sdk.login.Scopes.SCOPE_BASIC_INFO
import com.taptap.sdk.login.Scopes.SCOPE_PUBLIC_PROFILE
import com.taptap.sdk.login.Scopes.SCOPE_USER_FRIENDS
import com.taptap.sdk.login.TapTapAccount
import com.taptap.sdk.login.TapTapLogin
import com.taptap.sdk.login.TapTapLogin.loginWithScopes

class Account(activity : Activity, godotTdsPlugin: GodotTdsPlugin):
    GodotTdsPluginModule(activity, godotTdsPlugin),
    AccountInterface
{
    override fun login(
        publicProfileEnabled : Boolean,
        userFriendsEnabled : Boolean
    )
    {
        val scopes = mutableSetOf<String>()
        scopes.add(SCOPE_BASIC_INFO)
        if (publicProfileEnabled)
            scopes.add(SCOPE_PUBLIC_PROFILE)
        if (userFriendsEnabled)
            scopes.add(SCOPE_USER_FRIENDS)

        loginWithScopes(_activity, scopes.toTypedArray(), _loginCallback);
    }

    override fun logout()
    {
        TapTapLogin.logout()
        _godotTdsPlugin.exitCompliance()
    }

    override fun isLoggedIn() : Boolean
    {
        return TapTapLogin.getCurrentTapAccount() != null;
    }

    override fun getCurrentAccountAsString() : String
    {
        return TapTapLogin.getCurrentTapAccount().toJson()
    }

    override fun getAccountOpenId(): String
    {
        val tapAccount = TapTapLogin.getCurrentTapAccount()

        if (tapAccount != null)
            return tapAccount.openId

        return "Invalid Account"
    }

    private var _loginCallback : TapTapCallback<TapTapAccount> = object : TapTapCallback<TapTapAccount>
    {
        override fun onSuccess(result: TapTapAccount)
        {
            _godotTdsPlugin.emitPluginSignal("onLoginReturn", StateCode.LOGIN_SUCCESS, result.name!!)
        }

        override fun onFail(exception: TapTapException)
        {
            _godotTdsPlugin.emitPluginSignal("onLoginReturn", StateCode.LOGIN_FAIL, exception.message.toString())
        }

        override fun onCancel()
        {
            _godotTdsPlugin.emitPluginSignal("onLoginReturn", StateCode.LOGIN_CANCEL, "onCancel")
        }
    }
}