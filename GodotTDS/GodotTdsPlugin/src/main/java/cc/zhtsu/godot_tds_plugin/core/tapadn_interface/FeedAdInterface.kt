package cc.zhtsu.godot_tds_plugin.core.tapadn_interface

interface FeedAdInterface
{
    fun initialize()
    fun load(spaceId : Int, query : String)
    fun show(gravity : Int, height : Int)
}