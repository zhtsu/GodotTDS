package cc.zhtsu.godot_tds_plugin

import org.godotengine.godot.BuildConfig
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot

class GodotTdsPlugin(godot : Godot) : GodotPlugin(godot) {

    override fun getPluginName() = BuildConfig.LIBRARY_PACKAGE_NAME

    @UsedByGodot
    fun helloGodotPlugin() : String
    {
        return "Hello! Godot plugin!"
    }

}