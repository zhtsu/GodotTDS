package cc.zhtsu.godot_tds_plugin.core.tapsdk_interface

interface AchievementInterface
{
    fun initialize()
    fun destroy()
    fun unlock(achievementId: String)
    fun increment(achievementId: String, steps: Int)
    fun setToastEnable(enable: Boolean)
    fun showAchievements()
}