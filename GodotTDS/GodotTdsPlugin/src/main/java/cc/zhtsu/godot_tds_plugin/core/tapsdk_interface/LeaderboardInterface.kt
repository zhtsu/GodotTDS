package cc.zhtsu.godot_tds_plugin.core.tapsdk_interface

interface LeaderboardInterface
{
    fun initialize()
    fun destroy()
    fun openLeaderboard(leaderboardId: String, collection: String)
    fun showTapUserProfile(openId: String)
    fun submitScore(leaderboardId: String, score: Long)
    fun loadLeaderboardScores(leaderboardId: String, leaderboardCollection: Int, nextPage: String)
    fun loadCurrentPlayerLeaderboardScore(leaderboardId: String, leaderboardCollection: Int)
    fun loadPlayerCenteredScores(leaderboardId: String, leaderboardCollection: Int, periodToken: String, maxCount: Int)
}