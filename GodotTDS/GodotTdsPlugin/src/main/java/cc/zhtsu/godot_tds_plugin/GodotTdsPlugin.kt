package cc.zhtsu.godot_tds_plugin

import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot

class GodotTdsPlugin(godot : Godot) : GodotPlugin(godot) {

    override fun getPluginName() = "GodotTdsPlugin"

    override fun getPluginSignals() : MutableSet<SignalInfo>
    {
        return mutableSetOf(
            SignalInfo("onLoginReturn", Integer::class.java, String::class.java),
            SignalInfo("onAntiAddictionReturn", Integer::class.java, String::class.java)
        )
    }

    private var tapSDK : TapSDK = TapSDK()
    private var showPopupTips : Boolean = true

    @UsedByGodot
    fun init(clientId : String, clientToken : String, serverUrl : String)
    {
        activity?.let { tapSDK.init(it, clientId, clientToken, serverUrl, this) }
    }

    @UsedByGodot
    fun login()
    {
        tapSDK.login()
    }

    @UsedByGodot
    fun getUserProfile() : String
    {
        return tapSDK.getCurrentProfile()
    }

    @UsedByGodot
    fun isLoggedIn() : Boolean
    {
        return tapSDK.isLoggedIn()
    }

    @UsedByGodot
    fun antiAddiction()
    {
        tapSDK.antiAddiction()
    }

    @UsedByGodot
    fun setShowPopupTips(enabled : Boolean)
    {
        showPopupTips = enabled
    }

    fun getShowPopupTips() : Boolean { return showPopupTips }

    // Useful for emit signal
    fun emitPluginSignal(signal : String, code : Int, msg : String)
    {
        emitSignal(signal, code, msg)
    }
}