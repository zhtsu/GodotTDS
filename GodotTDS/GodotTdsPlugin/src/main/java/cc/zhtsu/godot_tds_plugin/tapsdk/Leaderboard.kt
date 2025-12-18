package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.core.StateCode
import cc.zhtsu.godot_tds_plugin.core.GodotTdsPluginModule
import cc.zhtsu.godot_tds_plugin.core.tapsdk_interface.LeaderboardInterface
import com.taptap.sdk.leaderboard.androidx.TapTapLeaderboard
import com.taptap.sdk.leaderboard.callback.TapTapLeaderboardCallback
import com.taptap.sdk.leaderboard.callback.TapTapLeaderboardResponseCallback
import com.taptap.sdk.leaderboard.data.request.LeaderboardCollection
import com.taptap.sdk.leaderboard.data.request.SubmitScoresRequest
import com.taptap.sdk.leaderboard.data.response.LeaderboardScoresResponse
import com.taptap.sdk.leaderboard.data.response.SubmitScoresResponse
import com.taptap.sdk.leaderboard.data.response.UserScoreResponse
import com.taptap.sdk.leaderboard.data.response.common.Score
import org.json.JSONObject

class Leaderboard(activity : Activity, godotTdsPlugin: GodotTdsPlugin):
    GodotTdsPluginModule(activity, godotTdsPlugin),
    LeaderboardInterface
{
    override fun initialize()
    {
        TapTapLeaderboard.registerLeaderboardCallback(_leaderboardCallback)
    }

    override fun destroy()
    {
        TapTapLeaderboard.unregisterLeaderboardCallback(_leaderboardCallback)
    }

    override fun openLeaderboard(leaderboardId: String, collection: String)
    {
        TapTapLeaderboard.openLeaderboard(_activity, leaderboardId, collection)
    }

    override fun showTapUserProfile(openId: String)
    {
        TapTapLeaderboard.showTapUserProfile(_activity, openId)
    }

    override fun submitScore(leaderboardId: String, score: Long)
    {
        val scores = listOf(SubmitScoresRequest.ScoreItem("leaderboardId", score))
        TapTapLeaderboard.submitScores(scores, _leaderboardSubmitCallback)
    }

    override fun loadLeaderboardScores(leaderboardId: String, leaderboardCollection: Int, nextPage: String)
    {
        val collection = if (leaderboardCollection == 0) LeaderboardCollection.PUBLIC else LeaderboardCollection.FRIENDS
        val nextPageParam: String? = if (nextPage == "") null else nextPage

        TapTapLeaderboard.loadLeaderboardScores(
            leaderboardId = leaderboardId,
            leaderboardCollection = collection,
            nextPage = nextPageParam,
            callback = _loadLeaderboardScoresCallback
        )
    }

    override fun loadCurrentPlayerLeaderboardScore(leaderboardId: String, leaderboardCollection: Int)
    {
        val collection = if (leaderboardCollection == 0) LeaderboardCollection.PUBLIC else LeaderboardCollection.FRIENDS

        TapTapLeaderboard.loadCurrentPlayerLeaderboardScore(
            leaderboardId = leaderboardId,
            leaderboardCollection = collection,
            periodToken = null,
            callback = _loadCurrentPlayerLeaderboardScoreCallback
        )
    }

    override fun loadPlayerCenteredScores(leaderboardId: String, leaderboardCollection: Int, periodToken: String, maxCount: Int)
    {
        val collection = if (leaderboardCollection == 0) LeaderboardCollection.PUBLIC else LeaderboardCollection.FRIENDS

        TapTapLeaderboard.loadPlayerCenteredScores(
            leaderboardId = leaderboardId,
            leaderboardCollection = collection,
            periodToken = periodToken,
            maxCount = maxCount,
            callback = _loadPlayerCenteredScoresCallback
        )
    }

    private var _leaderboardCallback: TapTapLeaderboardCallback = object : TapTapLeaderboardCallback
    {
        override fun onLeaderboardResult(code: Int, message: String)
        {
            _godotTdsPlugin.emitPluginSignal("onLeaderboardReturn", code, message)
        }
    }

    private var _leaderboardSubmitCallback: TapTapLeaderboardResponseCallback<SubmitScoresResponse> = object:
        TapTapLeaderboardResponseCallback<SubmitScoresResponse>()
    {
        override fun onSuccess(data: SubmitScoresResponse)
        {
            _godotTdsPlugin.emitPluginSignal("onLeaderboardReturn",
                StateCode.LEADERBOARD_SUBMIT_SUCCESS,
                SubmitScoresResponse.toString()
            )
        }

        override fun onFailure(code: Int, message: String)
        {
            _godotTdsPlugin.emitPluginSignal("onLeaderboardReturn",
                StateCode.LEADERBOARD_SUBMIT_FAIL, message)
        }
    }

    private var _loadLeaderboardScoresCallback: TapTapLeaderboardResponseCallback<LeaderboardScoresResponse> = object:
        TapTapLeaderboardResponseCallback<LeaderboardScoresResponse>()
    {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onSuccess(data: LeaderboardScoresResponse)
        {
            val scores = data.scores
            val nextPage = data.nextPage

            val retJsonObj: JSONObject = _rankingListToJsonObj(scores)
            retJsonObj.put("nextPage", nextPage)
            val msg : String = retJsonObj.toString()

            _godotTdsPlugin.emitPluginSignal("onLeaderboardReturn",
                StateCode.LEADERBOARD_FETCH_SCORES_SUCCESS, msg)
        }

        override fun onFailure(code: Int, message: String)
        {
            _godotTdsPlugin.emitPluginSignal("onLeaderboardReturn",
                StateCode.LEADERBOARD_FETCH_SCORES_FAIL, message)
        }
    }

    private var _loadCurrentPlayerLeaderboardScoreCallback: TapTapLeaderboardResponseCallback<UserScoreResponse> = object:
        TapTapLeaderboardResponseCallback<UserScoreResponse>()
    {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onSuccess(data: UserScoreResponse)
        {
            val scores: List<Score> = if (data.currentUserScore == null) emptyList() else listOf(data.currentUserScore!!)

            val retJsonObj: JSONObject = _rankingListToJsonObj(scores)
            val msg : String = retJsonObj.toString()

            _godotTdsPlugin.emitPluginSignal("onLeaderboardReturn",
                StateCode.LEADERBOARD_FETCH_CURRENT_PLAYER_SCORE_SUCCESS, msg)
        }

        override fun onFailure(code: Int, message: String)
        {
            _godotTdsPlugin.emitPluginSignal("onLeaderboardReturn",
                StateCode.LEADERBOARD_FETCH_CURRENT_PLAYER_SCORE_FAIL, message)
        }
    }

    private var _loadPlayerCenteredScoresCallback: TapTapLeaderboardResponseCallback<LeaderboardScoresResponse> = object:
        TapTapLeaderboardResponseCallback<LeaderboardScoresResponse>()
    {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onSuccess(data: LeaderboardScoresResponse)
        {
            val scores = data.scores

            val retJsonObj: JSONObject = _rankingListToJsonObj(scores)
            val msg : String = retJsonObj.toString()

            _godotTdsPlugin.emitPluginSignal("onLeaderboardReturn",
                StateCode.LEADERBOARD_FETCH_CURRENT_PLAYER_CENTERED_SCORE_SUCCESS, msg)
        }

        override fun onFailure(code: Int, message: String)
        {
            _godotTdsPlugin.emitPluginSignal("onLeaderboardReturn",
                StateCode.LEADERBOARD_FETCH_CURRENT_PLAYER_CENTERED_SCORE_FAIL, message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun _rankingListToJsonObj(rankingList : List<Score>) : JSONObject
    {
        val jsonObject = JSONObject()
        for (ranking in rankingList)
        {
            val tempJsonObject = JSONObject()
            tempJsonObject.put("rank", ranking.rank)
            tempJsonObject.put("username", ranking.user?.name ?: "Invalid Username")
            tempJsonObject.put("score", ranking.score)
            jsonObject.append("scores", tempJsonObject)
        }
        return jsonObject
    }
}