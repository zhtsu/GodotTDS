package cc.zhtsu.godot_tds_plugin

import android.app.Activity

open class GodotTdsPluginModule(activity: Activity, godotTdsPlugin: GodotTdsPlugin)
{
    protected var _activity : Activity = activity
    protected var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin
}