package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.core.StateCode
import cc.zhtsu.godot_tds_plugin.core.GodotTdsPluginModule
import cc.zhtsu.godot_tds_plugin.core.tapsdk_interface.LeaderboardInterface
import com.tapsdk.lc.LCLeaderboard
import com.tapsdk.lc.LCLeaderboardResult
import com.tapsdk.lc.LCRanking
import com.tapsdk.lc.LCStatisticResult
import com.tapsdk.lc.LCUser
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.json.JSONObject

class Leaderboard(activity : Activity, godotTdsPlugin: GodotTdsPlugin) :
    GodotTdsPluginModule(activity, godotTdsPlugin),
    LeaderboardInterface
{
    fun submitLeaderboardScore(leaderboardName : String, score : Long)
    {
        val statistic = HashMap<String, Double>()
        statistic[leaderboardName] = score.toDouble()
        LCLeaderboard.updateStatistic(LCUser.currentUser(), statistic, true).subscribe(_leaderboardSubmitObserver)
    }

    fun fetchLeaderboardSectionRankings(leaderboardName : String, start : Int, end : Int)
    {
        val leaderboard = LCLeaderboard.createWithoutData(leaderboardName)
        val selectKeys : List<String> = listOf("nickname")
        leaderboard.getResults(start, end, selectKeys, null).subscribe(_leaderboardSectionRankingsObserver)
    }

    fun fetchLeaderboardUserAroundRankings(leaderboardName : String, count : Int)
    {
        val objectId : String = _godotTdsPlugin.getAccountOpenId()
        val leaderboard = LCLeaderboard.createWithoutData(leaderboardName)
        val selectKeys : List<String> = listOf("nickname")
        leaderboard.getAroundResults(objectId, 0, count, selectKeys, null).subscribe(_leaderboardUserAroundRankingsObserver)
    }

    private var _leaderboardSubmitObserver : Observer<LCStatisticResult> = object : Observer<LCStatisticResult>
    {
        override fun onSubscribe(disposable : Disposable) {}

        override fun onNext(result : LCStatisticResult)
        {
            _godotTdsPlugin.emitPluginSignal("onLeaderboardReturn",
                StateCode.LEADERBOARD_SUBMIT_SUCCESS,
                result.toString()
            )
        }

        override fun onError(throwable : Throwable)
        {
            _godotTdsPlugin.emitPluginSignal("onLeaderboardReturn",
                StateCode.LEADERBOARD_SUBMIT_FAIL, throwable.message.toString())
        }

        override fun onComplete() {}
    }

    private var _leaderboardSectionRankingsObserver : Observer<LCLeaderboardResult> = object : Observer<LCLeaderboardResult>
    {
        override fun onSubscribe(disposable : Disposable) {}

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onNext(leaderboardResult : LCLeaderboardResult)
        {
            val msg : String = _rankingListToJsonObj(leaderboardResult.results).toString()
            _godotTdsPlugin.emitPluginSignal("onLeaderboardReturn",
                StateCode.LEADERBOARD_FETCH_SECTION_RANKINGS_SUCCESS, msg)
        }

        override fun onError(throwable : Throwable)
        {
            _godotTdsPlugin.emitPluginSignal("onLeaderboardReturn",
                StateCode.LEADERBOARD_FETCH_SECTION_RANKINGS_FAIL, throwable.message.toString())
        }

        override fun onComplete() {}
    }

    private var _leaderboardUserAroundRankingsObserver : Observer<LCLeaderboardResult> = object : Observer<LCLeaderboardResult>
    {
        override fun onSubscribe(disposable : Disposable) {}

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onNext(leaderboardResult : LCLeaderboardResult)
        {
            val msg : String = _rankingListToJsonObj(leaderboardResult.results).toString()
            _godotTdsPlugin.emitPluginSignal("onLeaderboardReturn",
                StateCode.LEADERBOARD_FETCH_USER_RANKING_SUCCESS, msg)
        }

        override fun onError(throwable : Throwable)
        {
            _godotTdsPlugin.emitPluginSignal("onLeaderboardReturn",
                StateCode.LEADERBOARD_FETCH_USER_RANKING_FAIL, throwable.message.toString())
        }

        override fun onComplete() {}
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun _rankingListToJsonObj(rankingList : List<LCRanking>) : JSONObject
    {
        val jsonObject = JSONObject()
        for (ranking in rankingList)
        {
            val tempJsonObject = JSONObject()
            tempJsonObject.put("rank", ranking.rank)
            tempJsonObject.put("nickname", ranking.user.toJSONObject()["nickname"])
            tempJsonObject.put("statisticValue", ranking.statisticValue)
            jsonObject.append("list", tempJsonObject)
        }
        return jsonObject
    }
}