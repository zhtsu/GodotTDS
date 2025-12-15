package cc.zhtsu.godot_tds_plugin.core

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin

open class GodotTdsPluginModule(activity: Activity, godotTdsPlugin: GodotTdsPlugin)
{
    protected var _activity : Activity = activity
    protected var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin
}