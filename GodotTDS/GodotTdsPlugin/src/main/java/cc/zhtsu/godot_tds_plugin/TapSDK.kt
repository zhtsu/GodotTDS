package cc.zhtsu.godot_tds_plugin

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.tapsdk.antiaddiction.Config
import com.tapsdk.antiaddictionui.AntiAddictionUICallback
import com.tapsdk.antiaddictionui.AntiAddictionUIKit
import com.tapsdk.bootstrap.Callback
import com.tapsdk.bootstrap.TapBootstrap
import com.tapsdk.bootstrap.account.TDSUser
import com.tapsdk.bootstrap.exceptions.TapError
import com.tapsdk.bootstrap.gamesave.TapGameSave
import com.tapsdk.lc.LCLeaderboard
import com.tapsdk.lc.LCLeaderboardResult
import com.tapsdk.lc.LCRanking
import com.tapsdk.lc.LCStatisticResult
import com.tapsdk.lc.LCUser
import com.tapsdk.lc.types.LCNull
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Date


class TapSDK {

    private lateinit var _activity : android.app.Activity
    private lateinit var _clientId : String
    private lateinit var _clientToken : String
    private lateinit var _godotTdsPlugin : GodotTdsPlugin

    private lateinit var _logInCallback : Callback<TDSUser>
    private lateinit var _antiAddictionUICallback : AntiAddictionUICallback
    private lateinit var _tapMomentCallback : TapMomentCallback
    private lateinit var _achievementCallback : AchievementCallback
    private lateinit var _giftCallback : okhttp3.Callback
    private lateinit var _leaderboardSubmitObserver : Observer<LCStatisticResult>
    private lateinit var _leaderboardSectionRankingsObserver : Observer<LCLeaderboardResult>
    private lateinit var _leaderboardUserAroundRankingsObserver : Observer<LCLeaderboardResult>
    private lateinit var _gameSaveCreateCallback : Observer<TapGameSave>
    private lateinit var _gameSaveAccessCallback : Observer<List<TapGameSave>>
    private lateinit var _gameSaveDeleteCallback : Observer<LCNull>

    private var _networkAllAchievementList : List<TapAchievementBean> = listOf()
    private var _objectId : String = ""
    private var _gameSaves : MutableMap<String, TapGameSave> = mutableMapOf()

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
                _objectId = TDSUser.currentUser().objectId
                TapAchievement.initData()
                TapGameSave.getCurrentUserGameSaves().subscribe(_gameSaveAccessCallback)
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

    fun logIn()
    {
        TDSUser.loginWithTapTap(_activity, _logInCallback)
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
        return if (TDSUser.currentUser() != null)
        {
            TapLoginHelper.getCurrentProfile().toJsonString()
        }
        else
        {
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
        when (orientation)
        {
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
        val nonceStr : String = _generateNonceStr()

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
        okHttpClient.newCall(request).enqueue(_giftCallback)
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
        val leaderboard = LCLeaderboard.createWithoutData(leaderboardName)
        val selectKeys : List<String> = listOf("nickname")
        leaderboard.getAroundResults(_objectId, 0, count, selectKeys, null).subscribe(_leaderboardUserAroundRankingsObserver)
    }

    fun submitGameSave(name : String, summary : String, playedTime : Long, progressValue : Int, coverPath : String, gameFilePath : String, modifiedAt : Long)
    {
        val snapshot = TapGameSave()
        snapshot.name = name
        snapshot.summary = summary
        snapshot.playedTime = playedTime.toDouble()
        snapshot.progressValue = progressValue
        val a = _copyAssetGetFilePath(coverPath)
        a.let {
            snapshot.setCover(a)
        }
        Log.v("Cover path: ", a)
        val b = _copyAssetGetFilePath(gameFilePath)
        b.let {
            snapshot.setGameFile(b)
        }
        Log.v("Game file path: ", b)
        snapshot.modifiedAt = Date(modifiedAt)
        snapshot.saveInBackground().subscribe(_gameSaveCreateCallback)
    }

    fun accessGameSaves()
    {
        TapGameSave.getCurrentUserGameSaves().subscribe(_gameSaveAccessCallback)
    }

    fun deleteGameSave(gameSaveId : String)
    {
        if (_gameSaves.containsKey(gameSaveId))
        {
            _gameSaves[gameSaveId]?.deleteInBackground()?.subscribe(_gameSaveDeleteCallback)
            _gameSaves.remove(gameSaveId)
        }
        else
        {
            val msg = "Try to delete a nonexistent game save!"
            _godotTdsPlugin.emitPluginSignal("OnGameSaveReturn", Code.GAME_SAVE_DELETE_FAIL, msg)
        }
    }

    private fun _showToast(msg : String)
    {
        if (_godotTdsPlugin.getShowTipsToast())
        {
            _activity.runOnUiThread {
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

    private fun _generateNonceStr() : String
    {
        val random = SecureRandom()
        val nonce = ByteArray(5)
        random.nextBytes(nonce)
        return String(nonce, Charsets.UTF_8)
    }

    private fun _getSign(timestamp : String, nonceStr : String) : String
    {
        try
        {
            val signTxt: String = _shaEncode("${timestamp}${nonceStr}${_clientId}")
            return signTxt
        }
        catch (e: java.lang.Exception)
        {
            throw RuntimeException(e)
        }
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

    private fun _copyAssetGetFilePath(filePath : String) : String
    {
        try
        {
            val cacheDir = _activity.baseContext.cacheDir
            if (!cacheDir.exists())
            {
                cacheDir.mkdirs()
            }

            val file = File(filePath)
            val outFile = File(cacheDir, "${_shaEncode(file.name)}.${file.extension}")
            if (outFile.exists())
            {
                Log.v("SSSSSSSSSSSSSSSSSSSSSSSS", "")
                return outFile.path
            }
            else
            {
                val res = outFile.createNewFile()
                if (!res)
                {
                    Log.v("RRRRRRRRRRRRRRRRRRRRRRRRRR", "")
                    return ""
                }
            }

            val input : InputStream = _activity.assets.open(filePath)
            val output = FileOutputStream(outFile)
            val buffer = ByteArray(1024)
            var byteCount : Int = input.read(buffer)
            while (byteCount != -1)
            {
                output.write(buffer, 0, byteCount)
                byteCount = input.read(buffer)
            }
            output.flush()
            input.close()
            output.close()

            Log.v("TTTTTTTTTTTTTTTTTTTTTTTTTTT", "${byteCount}")
            return outFile.path
        }
        catch (e : IOException)
        {
            e.printStackTrace()
        }

        Log.v("MMMMMMMMMMMMMMMMMMMMMMMMMMMMM", "")
        return ""
    }

    private fun _initAllCallback()
    {
        _logInCallback = object : Callback<TDSUser>
        {
            override fun onSuccess(user : TDSUser)
            {
                _showToast("Log in successful")
                _objectId = user.objectId
                _godotTdsPlugin.emitPluginSignal("onLogInReturn", Code.LOG_IN_SUCCESS, user.toJSONInfo())

                // Reinit achievement data
                TapAchievement.initData()
            }

            override fun onFail(error : TapError)
            {
                _showToast("Log in failed")
                _godotTdsPlugin.emitPluginSignal("onLogInReturn", error.code, error.message.toString())
            }
        }

        _antiAddictionUICallback = AntiAddictionUICallback { code, _ ->
            _godotTdsPlugin.emitPluginSignal("onAntiAddictionReturn", code, Code.EMPTY_MSG)
        }

        _tapMomentCallback = TapMomentCallback { code, msg ->
            _godotTdsPlugin.emitPluginSignal("onTapMomentReturn", code, msg)
        }

        _achievementCallback = object : AchievementCallback
        {
            override fun onAchievementSDKInitSuccess()
            {
                _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", Code.ACHIEVEMENT_INIT_SUCCESS, Code.EMPTY_MSG)
            }

            override fun onAchievementSDKInitFail(exception: AchievementException)
            {
                _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", Code.ACHIEVEMENT_INIT_FAIL, exception.message.toString())
            }

            override fun onAchievementStatusUpdate(item: TapAchievementBean?, exception: AchievementException?)
            {
                if (exception != null)
                {
                    _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", Code.ACHIEVEMENT_UPDATE_FAIL, exception.message.toString())
                }

                if (item != null)
                {
                    _godotTdsPlugin.emitPluginSignal("OnAchievementReturn", Code.ACHIEVEMENT_UPDATE_SUCCESS, item.toJson().toString())
                }
            }
        }

        _giftCallback = object : okhttp3.Callback
        {
            override fun onFailure(call : Call, e : IOException)
            {
                _godotTdsPlugin.emitPluginSignal("OnGiftReturn", Code.GIFT_CODE_SUBMIT_FAIL, e.message.toString())
            }

            override fun onResponse(call : Call, response : Response)
            {
                var emptyBody = true
                response.body?.let {
                    emptyBody = false
                    _godotTdsPlugin.emitPluginSignal("OnGiftReturn", Code.GIFT_CODE_SUBMIT_SUCCESS, it.string())
                }
                if (emptyBody)
                {
                    _godotTdsPlugin.emitPluginSignal("OnGiftReturn", Code.GIFT_CODE_SUBMIT_FAIL, "Empty body")
                }
            }
        }

        _leaderboardSubmitObserver = object : Observer<LCStatisticResult>
        {
            override fun onSubscribe(disposable : Disposable) {}

            override fun onNext(result : LCStatisticResult)
            {
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", Code.LEADERBOARD_SUBMIT_SUCCESS, Code.EMPTY_MSG)
            }

            override fun onError(throwable : Throwable)
            {
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", Code.LEADERBOARD_SUBMIT_FAIL, throwable.message.toString())
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
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", Code.LEADERBOARD_ACCESS_SECTION_RANKINGS_SUCCESS, msg)
            }

            override fun onError(throwable : Throwable)
            {
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", Code.LEADERBOARD_ACCESS_SECTION_RANKINGS_FAIL, throwable.message.toString())
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
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", Code.LEADERBOARD_ACCESS_USER_RANKING_SUCCESS, msg)
            }

            override fun onError(throwable : Throwable)
            {
                _godotTdsPlugin.emitPluginSignal("OnLeaderboardReturn", Code.LEADERBOARD_ACCESS_USER_RANKING_FAIL, throwable.message.toString())
            }

            override fun onComplete() {}
        }

        _gameSaveCreateCallback = object : Observer<TapGameSave>
        {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(gameSave : TapGameSave)
            {
                _showToast("Submit successful")
                _gameSaves[gameSave.objectId] = gameSave
                _godotTdsPlugin.emitPluginSignal("OnGameSaveReturn", Code.GAME_SAVE_CREATE_SUCCESS, gameSave.objectId)
            }

            override fun onError(throwable : Throwable)
            {
                _showToast("Submit failed")
                _godotTdsPlugin.emitPluginSignal("OnGameSaveReturn", Code.GAME_SAVE_CREATE_FAIL, throwable.message.toString())
            }

            override fun onComplete() {}
        }

        _gameSaveAccessCallback = object : Observer<List<TapGameSave>>
        {
            override fun onSubscribe(d: Disposable) {}

            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onNext(gameSaves : List<TapGameSave>)
            {
                val jsonObject = JSONObject()
                _gameSaves.clear()
                for (gameSave in gameSaves)
                {
                    _gameSaves[gameSave.objectId] = gameSave
                    val tempJsonObject = JSONObject()
                    tempJsonObject.put("id", gameSave.objectId)
                    tempJsonObject.put("name", gameSave.name)
                    tempJsonObject.put("summary", gameSave.summary)
                    tempJsonObject.put("modifiedAt", gameSave.modifiedAt.time)
                    tempJsonObject.put("playedTime", gameSave.playedTime.toLong())
                    tempJsonObject.put("progressValue", gameSave.progressValue)
                    if (gameSave.cover == null)
                        tempJsonObject.put("cover", "null")
                    else
                        tempJsonObject.put("cover", gameSave.cover.url)
                    if (gameSave.gameFile == null)
                        tempJsonObject.put("gameFile", "null")
                    else
                        tempJsonObject.put("gameFile", gameSave.gameFile.url)
                    jsonObject.append("list", tempJsonObject)
                }
                _godotTdsPlugin.emitPluginSignal("OnGameSaveReturn", Code.GAME_SAVE_ACCESS_SUCCESS, jsonObject.toString())
            }

            override fun onError(throwable : Throwable)
            {
                _godotTdsPlugin.emitPluginSignal("OnGameSaveReturn", Code.GAME_SAVE_ACCESS_FAIL, throwable.message.toString())
            }

            override fun onComplete() {}
        }

        _gameSaveDeleteCallback = object : Observer<LCNull>
        {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(response : LCNull)
            {
                _showToast("Delete successful")
                _godotTdsPlugin.emitPluginSignal("OnGameSaveReturn", Code.GAME_SAVE_DELETE_SUCCESS, "Game save delete successful")
            }

            override fun onError(throwable : Throwable)
            {
                _showToast("Delete failed")
                _godotTdsPlugin.emitPluginSignal("OnGameSaveReturn", Code.GAME_SAVE_DELETE_FAIL, throwable.message.toString())
            }

            override fun onComplete() {}
        }
    }
}