package cc.zhtsu.godot_tds_plugin

import android.util.Log
import android.widget.Toast
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot

class GodotTdsPlugin(godot : Godot) : GodotPlugin(godot) {

    override fun getPluginName() = "GodotTdsPlugin"

    @UsedByGodot
    private fun helloGodotPlugin() : String
    {
        runOnUiThread {
            Toast.makeText(activity, "Hello World", Toast.LENGTH_LONG).show()
            Log.v(pluginName, "Hello World")
        }

        return "Hello! Godot plugin!"
    }

}