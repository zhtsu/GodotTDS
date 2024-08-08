package cc.zhtsu.godot_tds_plugin

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import com.tapsdk.bootstrap.account.TDSUser
import com.tapsdk.lc.LCLeaderboard
import com.tapsdk.lc.LCLeaderboardResult
import com.tapsdk.lc.LCRanking
import com.tapsdk.lc.LCStatisticResult
import com.tapsdk.lc.LCUser
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.json.JSONObject

class Leaderboard(activity : Activity, godotTdsPlugin: GodotTdsPlugin) : TapTDS
{
    override var _activity : Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    private lateinit var _leaderboardSubmitObserver : Observer<LCStatisticResult>
    private lateinit var _leaderboardSectionRankingsObserver : Observer<LCLeaderboardResult>
    private lateinit var _leaderboardUserAroundRankingsObserver : Observer<LCLeaderboardResult>

    fun init()
    {
        _initCallbacks()
    }

    fun submitLeaderboardScore(leaderboardName : String, score : Long)
    {
        val statistic = HashMap<String, Double>()
        statistic[leaderboardName] = score.toDouble()
        LCLeaderboard.updateStatistic(LCUser.currentUser(), statistic, true).subscribe(_leaderboardSubmitObserver)
    }

    fun accessLeaderboardSectionRankings(leaderboardName : String, start : Int, end : Int)
    {
        val leaderboard = LCLeaderboard.createWithoutData(leaderboardName)
        val selectKeys : List<String> = listOf("nickname")
        leaderboard.getResults(start, end, selectKeys, null).subscribe(_leaderboardSectionRankingsObserver)
    }

    fun accessLeaderboardUserAroundRankings(leaderboardName : String, count : Int)
    {
        val objectId : String = if (TDSUser.currentUser() != null) TDSUser.currentUser().objectId else ""
        val leaderboard = LCLeaderboard.createWithoutData(leaderboardName)
        val selectKeys : List<String> = listOf("nickname")
        leaderboard.getAroundResults(objectId, 0, count, selectKeys, null).subscribe(_leaderboardUserAroundRankingsObserver)
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

    override fun _initCallbacks()
    {
        _leaderboardSubmitObserver = object : Observer<LCStatisticResult>
        {
            override fun onSubscribe(disposable : Disposable) {}

            override fun onNext(result : LCStatisticResult)
            {
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", StateCode.LEADERBOARD_SUBMIT_SUCCESS, StateCode.EMPTY_MSG)
            }

            override fun onError(throwable : Throwable)
            {
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", StateCode.LEADERBOARD_SUBMIT_FAIL, throwable.message.toString())
            }

            override fun onComplete() {}
        }

        _leaderboardSectionRankingsObserver = object : Observer<LCLeaderboardResult>
        {
            override fun onSubscribe(disposable : Disposable) {}

            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onNext(leaderboardResult : LCLeaderboardResult)
            {
                val msg : String = _rankingListToJsonObj(leaderboardResult.results).toString()
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", StateCode.LEADERBOARD_ACCESS_SECTION_RANKINGS_SUCCESS, msg)
            }

            override fun onError(throwable : Throwable)
            {
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", StateCode.LEADERBOARD_ACCESS_SECTION_RANKINGS_FAIL, throwable.message.toString())
            }

            override fun onComplete() {}
        }

        _leaderboardUserAroundRankingsObserver = object : Observer<LCLeaderboardResult>
        {
            override fun onSubscribe(disposable : Disposable) {}

            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onNext(leaderboardResult : LCLeaderboardResult)
            {
                val msg : String = _rankingListToJsonObj(leaderboardResult.results).toString()
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", StateCode.LEADERBOARD_ACCESS_USER_RANKING_SUCCESS, msg)
            }

            override fun onError(throwable : Throwable)
            {
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", StateCode.LEADERBOARD_ACCESS_USER_RANKING_FAIL, throwable.message.toString())
            }

            override fun onComplete() {}
        }
    }
}