package cc.zhtsu.godot_tds_plugin.core.tapadn_interface

interface RewardVideoAdInterface
{
    fun load(
        spaceId: Int,
        rewardName: String,
        rewardAmount: Int,
        extraInfo: String,
        gameUserId: String,
    )
    fun show()
}