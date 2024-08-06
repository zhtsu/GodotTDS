package cc.zhtsu.godot_tds_plugin

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.tapsdk.antiaddiction.Config
import com.tapsdk.antiaddictionui.AntiAddictionUICallback
import com.tapsdk.antiaddictionui.AntiAddictionUIKit
import com.tapsdk.bootstrap.Callback
import com.tapsdk.bootstrap.TapBootstrap
import com.tapsdk.bootstrap.account.TDSUser
import com.tapsdk.bootstrap.exceptions.TapError
import com.tapsdk.lc.LCLeaderboard
import com.tapsdk.lc.LCLeaderboardResult
import com.tapsdk.lc.LCRanking
import com.tapsdk.lc.LCStatisticResult
import com.tapsdk.lc.LCUser
import com.tapsdk.moment.TapMoment
import com.tapsdk.moment.TapMoment.TapMomentCallback
import com.tapsdk.tapconnect.TapConnect
import com.taptap.sdk.TapLoginHelper
import com.tds.achievement.AchievementCallback
import com.tds.achievement.AchievementException
import com.tds.achievement.TapAchievement
import com.tds.achievement.TapAchievementBean
import com.tds.common.entities.TapConfig
import com.tds.common.models.TapRegionType
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.security.MessageDigest


class TapSDK {

    private lateinit var _activity : android.app.Activity
    private lateinit var _clientId : String
    private lateinit var _clientToken : String
    private lateinit var _godotTdsPlugin : GodotTdsPlugin

    private lateinit var _antiAddictionUICallback : AntiAddictionUICallback
    private lateinit var _tapMomentCallback : TapMomentCallback
    private lateinit var _achievementCallback : AchievementCallback
    private lateinit var _okHttpCallback : okhttp3.Callback
    private lateinit var _leaderboardSubmitObserver : Observer<LCStatisticResult>
    private lateinit var _leaderboardSectionRankingsObserver : Observer<LCLeaderboardResult>
    private lateinit var _leaderboardUserRankingObserver : Observer<LCLeaderboardResult>

    private var _networkAllAchievementList : List<TapAchievementBean> = listOf()
    private var _objectId : String = ""

    fun init(
        activity: android.app.Activity,
        clientId: String,
        clientToken: String,
        serverUrl: String,
        godotTdsPlugin: GodotTdsPlugin,
    )
    {
        _activity = activity
        _clientId = clientId
        _clientToken = clientToken
        _godotTdsPlugin = godotTdsPlugin

        _initAllCallback()

        activity.let {
            // Initialize Login
            activity.runOnUiThread {
                val tdsConfig = TapConfig.Builder()
                    .withAppContext(activity)
                    .withClientId(clientId)
                    .withClientToken(clientToken)
                    .withServerUrl(serverUrl)
                    .withRegionType(TapRegionType.CN)
                    .build()

                TapBootstrap.init(activity, tdsConfig)

                // Initialize Achievement if user is valid
                if (TDSUser.currentUser() != null)
                {
                    TapAchievement.initData()
                    _objectId = TDSUser.currentUser().objectId
                }
            }

            // Initialize AntiAddiction
            val config = Config.Builder()
                .withClientId(clientId)
                .showSwitchAccount(false)
                .useAgeRange(false)
                .build()
            AntiAddictionUIKit.init(activity, config)

            // Register callbacks
            AntiAddictionUIKit.setAntiAddictionCallback(_antiAddictionUICallback)
            TapMoment.setCallback(_tapMomentCallback)
            TapAchievement.registerCallback(_achievementCallback)
        }
    }

    fun logIn()
    {
        TDSUser.loginWithTapTap(_activity, object : Callback<TDSUser> {
            override fun onSuccess(user : TDSUser?) {
                _showToast("Log in successful")
                user?.let {
                    _objectId = user.objectId
                    _godotTdsPlugin.emitPluginSignal("onLogInReturn", Code.LOG_IN_SUCCESS, it.toJSONInfo())
                }

                // Reinit achievement data
                TapAchievement.initData()
            }

            override fun onFail(error : TapError?) {
                _showToast("Log in failed")
                error?.let {
                    _godotTdsPlugin.emitPluginSignal("onLogInReturn", error.code, error.message.toString())
                }
            }
        })
    }

    fun logOut()
    {
        if (TDSUser.currentUser() != null)
        {
            TDSUser.logOut()
            AntiAddictionUIKit.exit()
            _objectId = ""
            _showToast("Log out successful")
        }
        else
        {
            _showToast("Not log in")
        }
    }

    fun isLoggedIn() : Boolean
    {
        return TDSUser.currentUser() != null
    }

    fun getUserProfile() : String
    {
        return if (TDSUser.currentUser() != null) {
            TapLoginHelper.getCurrentProfile().toJsonString()
        } else {
            Code.EMPTY_MSG
        }
    }

    fun getUserObjectId() : String
    {
        return _objectId
    }

    fun antiAddiction()
    {
        if (TDSUser.currentUser() != null)
        {
            val userIdentifier = TDSUser.currentUser().uuid
            AntiAddictionUIKit.startupWithTapTap(_activity, userIdentifier)
        }
        else
        {
            _showToast("Not log in")
        }
    }

    fun tapMoment(orientation : Int)
    {
        when (orientation) {
            0 -> {
                TapMoment.open(TapMoment.ORIENTATION_DEFAULT)
            }
            1 -> {
                TapMoment.open(TapMoment.ORIENTATION_LANDSCAPE)
            }
            2 -> {
                TapMoment.open(TapMoment.ORIENTATION_PORTRAIT)
            }
            3 -> {
                TapMoment.open(TapMoment.ORIENTATION_SENSOR)
            }
            else -> {
                TapMoment.open(TapMoment.ORIENTATION_DEFAULT)
            }
        }
    }

    fun setEntryVisible(visible : Boolean)
    {
        _activity.runOnUiThread {
            TapConnect.setEntryVisible(visible)
        }
    }

    fun fetchAllAchievementList()
    {
        TapAchievement.fetchAllAchievementList { achievementList, exception ->
            if (exception != null)
            {
                _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", exception.errorCode, exception.message.toString())
            }
            else
            {
                _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", Code.ACHIEVEMENT_LIST_FETCH_SUCCESS, Code.EMPTY_MSG)
                _networkAllAchievementList = achievementList
            }
        }
    }

    fun getLocalAllAchievementList() : List<TapAchievementBean>
    {
        return TapAchievement.getLocalAllAchievementList()
    }

    fun getNetworkAllAchievementList() : List<TapAchievementBean>
    {
        return _networkAllAchievementList
    }

    fun showAchievementPage()
    {
        if (TDSUser.currentUser() != null)
        {
            TapAchievement.showAchievementPage()
        }
        else
        {
            _showToast("Not log in")
        }
    }

    fun reachAchievement(displayId : String)
    {
        TapAchievement.reach(displayId)
    }

    fun growAchievementSteps(displayId : String, steps : Int)
    {
        TapAchievement.growSteps(displayId, steps)
    }

    fun makeAchievementSteps(displayId : String, steps : Int)
    {
        TapAchievement.makeSteps(displayId, steps)
    }

    fun setShowAchievementToast(show : Boolean)
    {
        TapAchievement.setShowToast(show)
    }

    fun submitGiftCode(giftCode : String)
    {
        val okHttpClient = OkHttpClient()
        val jsonObject = JSONObject()
        val timestamp : String = (System.currentTimeMillis() / 1000).toString()
        val nonceStr : String = "GODOT"

        try
        {
            jsonObject.put("client_id", _clientId)
            jsonObject.put("gift_code", giftCode)
            jsonObject.put("character_id", _objectId)
            jsonObject.put("nonce_str", nonceStr)
            jsonObject.put("sign", _getSign(timestamp, nonceStr))
            jsonObject.put("timestamp", timestamp.toInt())
            jsonObject.put("server_code", "121212")
        }
        catch (e: JSONException)
        {
            e.printStackTrace()
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body : RequestBody = jsonObject.toString().toRequestBody(mediaType)
        val request : Request = Request.Builder()
            .url("https://poster-api.xd.cn/api/v1.0/cdk/game/submit-simple")
            .post(body)
            .build()
        okHttpClient.newCall(request).enqueue(_okHttpCallback)
    }

    fun submitLeaderboardScore(leaderboardName : String, score : Double)
    {
        val statistic = HashMap<String, Double>()
        statistic[leaderboardName] = score
        LCLeaderboard.updateStatistic(LCUser.currentUser(), statistic).subscribe(_leaderboardSubmitObserver)
    }

    fun accessLeaderboardSectionRankings(leaderboardName : String, start : Int, end : Int)
    {
        val leaderboard = LCLeaderboard.createWithoutData(leaderboardName)
        leaderboard.getResults(start, end, null, null).subscribe(_leaderboardSectionRankingsObserver)
    }

    fun accessLeaderboardUserRanking(leaderboardName : String)
    {
        val leaderboard = LCLeaderboard.createWithoutData(leaderboardName)
        val selectKeys : List<String> = listOf("username")
        leaderboard.getAroundResults(_objectId, 0, 1, selectKeys, null).subscribe(_leaderboardUserRankingObserver)
    }

    private fun _showToast(msg : String)
    {
        _activity.runOnUiThread {
            if (_godotTdsPlugin.getShowTipsToast())
            {
                Toast.makeText(_activity, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(Exception::class)
    private fun _shaEncode(inStr : String) : String
    {
        val sha : MessageDigest?
        try
        {
            sha = MessageDigest.getInstance("SHA")
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return ""
        }

        val byteArray = inStr.toByteArray(charset("UTF-8"))
        val md5Bytes = sha.digest(byteArray)
        val hexValue = StringBuffer()
        for (i in md5Bytes.indices)
        {
            val value = (md5Bytes[i].toInt()) and 0xff
            if (value < 16)
            {
                hexValue.append("0")
            }
            hexValue.append(Integer.toHexString(value))
        }

        return hexValue.toString()
    }

    private fun _getSign(timestamp : String, nonceStr : String) : String
    {
        try
        {
            val signTxt: String = _shaEncode(timestamp + nonceStr + _clientId)
            return signTxt
        }
        catch (e: java.lang.Exception)
        {
            throw RuntimeException(e)
        }
    }

    private fun _initAllCallback()
    {
        _antiAddictionUICallback = AntiAddictionUICallback { code, _ ->
            _godotTdsPlugin.emitPluginSignal("onAntiAddictionReturn", code, Code.EMPTY_MSG)
        }

        _tapMomentCallback = TapMomentCallback { code, msg ->
            _godotTdsPlugin.emitPluginSignal("onTapMomentReturn", code, msg)
        }

        _achievementCallback = object : AchievementCallback {
            override fun onAchievementSDKInitSuccess() {
                _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", Code.ACHIEVEMENT_INIT_SUCCESS, Code.EMPTY_MSG)
            }

            override fun onAchievementSDKInitFail(exception: AchievementException) {
                _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", Code.ACHIEVEMENT_INIT_ERROR, exception.message.toString())
            }

            override fun onAchievementStatusUpdate(
                item: TapAchievementBean?,
                exception: AchievementException?,
            ) {
                if (exception != null) {
                    _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", Code.ACHIEVEMENT_UPDATE_ERROR, exception.message.toString())
                }

                if (item != null) {
                    _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", Code.ACHIEVEMENT_UPDATE_SUCCESS, item.toJson().toString())
                }
            }
        }

        _okHttpCallback = object : okhttp3.Callback {
            override fun onFailure(call : Call, e : IOException) {
                _godotTdsPlugin.emitPluginSignal("OnGiftReturn", Code.GIFT_CODE_SUBMIT_ERROR, e.message.toString())
            }

            override fun onResponse(call : Call, response : Response) {
                var emptyBody : Boolean = true
                response.body?.let {
                    emptyBody = false
                    _godotTdsPlugin.emitPluginSignal("OnGiftReturn", Code.GIFT_CODE_SUBMIT_SUCCESS, it.string())
                }
                if (emptyBody)
                {
                    _godotTdsPlugin.emitPluginSignal("OnGiftReturn", Code.GIFT_CODE_SUBMIT_ERROR, "Empty body")
                }
            }
        }

        _leaderboardSubmitObserver = object : Observer<LCStatisticResult> {
            override fun onSubscribe(disposable : Disposable) {}

            override fun onNext(jsonObject : LCStatisticResult) {
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", Code.LEADERBOARD_SUBMIT_SUCCESS, jsonObject.toString())
            }

            override fun onError(throwable : Throwable) {
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", Code.LEADERBOARD_SUBMIT_ERROR, throwable.message.toString())
            }

            override fun onComplete() {}
        }

        _leaderboardSectionRankingsObserver = object : Observer<LCLeaderboardResult> {
            override fun onSubscribe(disposable : Disposable) {}

            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onNext(leaderboardResult : LCLeaderboardResult) {
                val rankingList : List<LCRanking> = leaderboardResult.results
                val jsonObject = JSONObject()
                for (ranking in rankingList)
                {
                    jsonObject.append("list", ranking.toString())
                }
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", Code.LEADERBOARD_ACCESS_SECTION_RANKINGS_SUCCESS, jsonObject.toString())
            }

            override fun onError(throwable : Throwable) {
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", Code.LEADERBOARD_ACCESS_SECTION_RANKINGS_ERROR, throwable.message.toString())
            }

            override fun onComplete() {}
        }

        _leaderboardUserRankingObserver = object : Observer<LCLeaderboardResult> {
            override fun onSubscribe(disposable : Disposable) {}

            override fun onNext(leaderboardResult : LCLeaderboardResult) {
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", Code.LEADERBOARD_ACCESS_USER_RANKING_SUCCESS, leaderboardResult.toString())
            }

            override fun onError(throwable : Throwable) {
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", Code.LEADERBOARD_ACCESS_USER_RANKING_ERROR, throwable.message.toString())
            }

            override fun onComplete() {}
        }
    }
}