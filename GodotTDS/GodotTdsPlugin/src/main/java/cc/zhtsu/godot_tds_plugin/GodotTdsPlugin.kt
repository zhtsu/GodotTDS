package cc.zhtsu.godot_tds_plugin

import com.tapsdk.tapconnect.TapConnect
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot

class GodotTdsPlugin(godot : Godot) : GodotPlugin(godot) {

    override fun getPluginName() = "GodotTdsPlugin"

    override fun getPluginSignals() : MutableSet<SignalInfo>
    {
        return mutableSetOf(
            SignalInfo("onLogInReturn", Integer::class.java, String::class.java),
            SignalInfo("onAntiAddictionReturn", Integer::class.java, String::class.java),
            SignalInfo("onTapMomentReturn", Integer::class.java, String::class.java)
        )
    }

    private var _tapSDK : TapSDK = TapSDK()
    private var _toastEnabled : Boolean = true

    @UsedByGodot
    fun init(clientId : String, clientToken : String, serverUrl : String)
    {
        activity?.let { _tapSDK.init(it, clientId, clientToken, serverUrl, this) }
    }

    @UsedByGodot
    fun logIn()
    {
        _tapSDK.logIn()
    }

    @UsedByGodot
    fun logOut()
    {
        _tapSDK.logOut()
    }

    @UsedByGodot
    fun getUserProfile() : String
    {
        return _tapSDK.getCurrentProfile()
    }

    @UsedByGodot
    fun isLoggedIn() : Boolean
    {
        return _tapSDK.isLoggedIn()
    }

    @UsedByGodot
    fun antiAddiction()
    {
        _tapSDK.antiAddiction()
    }

    @UsedByGodot
    fun getAgeRange() : Int
    {
        return _tapSDK.getAgeRange()
    }

    @UsedByGodot
    fun setToastEnabled(enabled : Boolean)
    {
        _toastEnabled = enabled
    }

    @UsedByGodot
    fun tapMoment(orientation : Int)
    {
        _tapSDK.tapMoment(orientation)
    }

    @UsedByGodot
    fun setEntryVisible(visible : Boolean)
    {
        _tapSDK.setEntryVisible(visible)
    }

    fun getToastEnabled() : Boolean { return _toastEnabled }

    // Useful for emit signal
    fun emitPluginSignal(signal : String, code : Int, msg : String)
    {
        emitSignal(signal, code, msg)
    }
}