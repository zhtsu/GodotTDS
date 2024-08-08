package cc.zhtsu.godot_tds_plugin

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import com.tapsdk.bootstrap.gamesave.TapGameSave
import com.tapsdk.lc.types.LCNull
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import java.util.Date

class GameSave(activity : Activity, godotTdsPlugin: GodotTdsPlugin) : TapTDS
{
    override var _activity : Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    private var _gameSaves : MutableMap<String, TapGameSave> = mutableMapOf()

    private lateinit var _gameSaveCreateCallback : Observer<TapGameSave>
    private lateinit var _gameSaveAccessCallback : Observer<List<TapGameSave>>
    private lateinit var _gameSaveDeleteCallback : Observer<LCNull>

    fun init()
    {
        _initCallbacks()
    }

    fun submitGameSave(name : String, summary : String, playedTime : Long, progressValue : Int, coverPath : String, gameFilePath : String, modifiedAt : Long)
    {
        val snapshot = TapGameSave()
        snapshot.name = name
        snapshot.summary = summary
        snapshot.playedTime = playedTime.toDouble()
        snapshot.progressValue = progressValue
        try
        {
            snapshot.setCover(coverPath)
            snapshot.setGameFile(gameFilePath)
        }
        catch (_ : java.lang.IllegalArgumentException) {}
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
            _godotTdsPlugin.emitPluginSignal("OnGameSaveReturn", StateCode.GAME_SAVE_DELETE_FAIL, msg)
        }
    }

    override fun _initCallbacks()
    {
        _gameSaveCreateCallback = object : Observer<TapGameSave>
        {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(gameSave : TapGameSave)
            {
                _showToast("Submit successful")
                _gameSaves[gameSave.objectId] = gameSave
                _godotTdsPlugin.emitPluginSignal("OnGameSaveReturn", StateCode.GAME_SAVE_CREATE_SUCCESS, gameSave.objectId)
            }

            override fun onError(throwable : Throwable)
            {
                _showToast("Submit failed")
                _godotTdsPlugin.emitPluginSignal("OnGameSaveReturn", StateCode.GAME_SAVE_CREATE_FAIL, throwable.message.toString())
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
                _godotTdsPlugin.emitPluginSignal("OnGameSaveReturn", StateCode.GAME_SAVE_ACCESS_SUCCESS, jsonObject.toString())
            }

            override fun onError(throwable : Throwable)
            {
                _godotTdsPlugin.emitPluginSignal("OnGameSaveReturn", StateCode.GAME_SAVE_ACCESS_FAIL, throwable.message.toString())
            }

            override fun onComplete() {}
        }

        _gameSaveDeleteCallback = object : Observer<LCNull>
        {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(response : LCNull)
            {
                _showToast("Delete successful")
                _godotTdsPlugin.emitPluginSignal("OnGameSaveReturn", StateCode.GAME_SAVE_DELETE_SUCCESS, "Game save delete successful")
            }

            override fun onError(throwable : Throwable)
            {
                _showToast("Delete failed")
                _godotTdsPlugin.emitPluginSignal("OnGameSaveReturn", StateCode.GAME_SAVE_DELETE_FAIL, throwable.message.toString())
            }

            override fun onComplete() {}
        }
    }
}