package cc.zhtsu.godot_tds_plugin.core.tapadn_interface

interface BannerAdInterface
{
    fun initialize()
    fun load(spaceId: Int)
    fun show(gravity: Int, height: Int)
    fun dispose()
}