package cc.zhtsu.godot_tds_plugin

import android.app.Activity
import android.widget.Toast

interface TapTDS
{
    var _activity : Activity
    var _godotTdsPlugin : GodotTdsPlugin

    fun _initCallbacks() {}

    fun _showToast(msg : String)
    {
        if (_godotTdsPlugin.getShowTipsToast())
        {
            _activity.runOnUiThread {
                Toast.makeText(_activity, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}