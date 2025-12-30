package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.GodotTdsPluginModule
import cc.zhtsu.godot_tds_plugin.StateCode
import com.taptap.sdk.cloudsave.ArchiveData
import com.taptap.sdk.cloudsave.ArchiveMetadata
import com.taptap.sdk.cloudsave.TapTapCloudSave
import com.taptap.sdk.cloudsave.internal.TapCloudSaveCallback
import com.taptap.sdk.cloudsave.internal.TapCloudSaveRequestCallback
import org.json.JSONObject

class CloudSave(activity: Activity, godotTdsPlugin: GodotTdsPlugin):
    GodotTdsPluginModule(activity, godotTdsPlugin)
{
    fun initialize()
    {
        TapTapCloudSave.registerCloudSaveCallback(_cloudSaveCallback)
    }

    fun destroy()
    {
        TapTapCloudSave.unregisterCloudSaveCallback(_cloudSaveCallback)
    }

    fun createArchive(
        name: String,
        summary: String,
        extra: String,
        playtime: Int,
        archiveFilePath: String,
        archiveCoverPath: String
    )
    {
        val metadata = ArchiveMetadata.Builder()
            .setName(name)
            .setSummary(summary)
            .setExtra(extra)
            .setPlaytime(playtime)
            .build()

        TapTapCloudSave.createArchive(metadata, archiveFilePath, archiveCoverPath, _createArchiveCallback)
    }

    fun getArchiveList()
    {
        TapTapCloudSave.getArchiveList(_getArchiveListCallback)
    }

    fun getArchiveData(archiveUuid: String, archiveFileId: String)
    {
        TapTapCloudSave.getArchiveData(archiveUuid, archiveFileId, _getArchiveDataCallback)
    }

    fun updateArchive(
        archiveUuid: String,
        name: String,
        summary: String,
        extra: String,
        playtime: Int,
        archiveFilePath: String,
        archiveCoverPath: String
    )
    {
        val metadata = ArchiveMetadata.Builder()
            .setName(name)
            .setSummary(summary)
            .setExtra(extra)
            .setPlaytime(playtime)
            .build()

        TapTapCloudSave.updateArchive(archiveUuid, metadata, archiveFilePath, archiveCoverPath, _updateArchiveCallback)
    }

    fun deleteArchive(archiveUuid: String)
    {
        TapTapCloudSave.deleteArchive(archiveUuid, _deleteArchiveCallback)
    }

    fun getArchiveCover(archiveUuid: String, archiveFileId: String)
    {
        TapTapCloudSave.getArchiveCover(archiveUuid, archiveFileId, _getArchiveCoverCallback)
    }

    private val _cloudSaveCallback = object: TapCloudSaveCallback
    {
        override fun onResult(resultCode: Int)
        {
            _godotTdsPlugin.emitPluginSignal("onCloudSaveReturn", resultCode, "cloudSaveCallback")
        }
    }

    private val _createArchiveCallback = object: TapCloudSaveRequestCallback
    {
        override fun onRequestError(errorCode: Int, errorMessage: String)
        {
            _godotTdsPlugin.emitPluginSignal("onCloudSaveReturn", StateCode.CLOUD_SAVE_CREATE_ARCHIVE_FAIL, errorMessage)
        }

        override fun onArchiveCreated(archive: ArchiveData)
        {
            _godotTdsPlugin.emitPluginSignal("onCloudSaveReturn", StateCode.CLOUD_SAVE_CREATE_ARCHIVE_SUCCESS, archive.uuid)
        }
    }

    private val _getArchiveListCallback = object : TapCloudSaveRequestCallback
    {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onArchiveListResult(archiveList: List<ArchiveData>)
        {
            val jsonObject = JSONObject()
            for (archive in archiveList)
            {
                val tempJsonObject = JSONObject()
                tempJsonObject.put("uuid", archive.uuid)
                tempJsonObject.put("name", archive.name)
                tempJsonObject.put("summary", archive.summary)
                tempJsonObject.put("extra", archive.extra)
                tempJsonObject.put("playtime", archive.playtime)
                tempJsonObject.put("coverSize", archive.coverSize)
                tempJsonObject.put("createdTime", archive.createdTime)
                tempJsonObject.put("fileId", archive.fileId)
                tempJsonObject.put("modifiedTime", archive.modifiedTime)
                tempJsonObject.put("saveSize", archive.saveSize)
                jsonObject.append("list", tempJsonObject)
            }

            _godotTdsPlugin.emitPluginSignal("onCloudSaveReturn", StateCode.CLOUD_SAVE_GET_ARCHIVE_LIST_SUCCESS, jsonObject.toString())
        }

        override fun onRequestError(errorCode: Int, errorMessage: String)
        {
            _godotTdsPlugin.emitPluginSignal("onCloudSaveReturn", StateCode.CLOUD_SAVE_GET_ARCHIVE_LIST_FAIL, errorMessage)
        }
    }

    private val _deleteArchiveCallback = object : TapCloudSaveRequestCallback
    {
        override fun onArchiveDeleted(archive: ArchiveData)
        {
            _godotTdsPlugin.emitPluginSignal("onCloudSaveReturn", StateCode.CLOUD_SAVE_DELETE_ARCHIVE_SUCCESS, "Delete Successful")
        }

        override fun onRequestError(errorCode: Int, errorMessage: String)
        {
            _godotTdsPlugin.emitPluginSignal("onCloudSaveReturn", StateCode.CLOUD_SAVE_DELETE_ARCHIVE_FAIL, errorMessage)
        }
    }

    private val _getArchiveDataCallback = object : TapCloudSaveRequestCallback
    {
        override fun onArchiveDataResult(archiveData: ByteArray)
        {
            val base64String = android.util.Base64.encodeToString(archiveData, android.util.Base64.NO_WRAP)
            _godotTdsPlugin.emitPluginSignal("onCloudSaveReturn", StateCode.CLOUD_SAVE_GET_ARCHIVE_DATA_SUCCESS, base64String)
        }

        override fun onRequestError(errorCode: Int, errorMessage: String)
        {
            _godotTdsPlugin.emitPluginSignal("onCloudSaveReturn", StateCode.CLOUD_SAVE_GET_ARCHIVE_DATA_FAIL, errorMessage)
        }
    }

    private val _updateArchiveCallback = object : TapCloudSaveRequestCallback
    {
        override fun onArchiveUpdated(archive: ArchiveData)
        {
            _godotTdsPlugin.emitPluginSignal("onCloudSaveReturn", StateCode.CLOUD_SAVE_UPDATE_ARCHIVE_SUCCESS, archive.uuid)
        }

        override fun onRequestError(errorCode: Int, errorMessage: String)
        {
            _godotTdsPlugin.emitPluginSignal("onCloudSaveReturn", StateCode.CLOUD_SAVE_UPDATE_ARCHIVE_FAIL, errorMessage)
        }
    }

    private val _getArchiveCoverCallback = object : TapCloudSaveRequestCallback
    {
        override fun onArchiveCoverResult(coverData: ByteArray)
        {
            val base64String = android.util.Base64.encodeToString(coverData, android.util.Base64.NO_WRAP)
            _godotTdsPlugin.emitPluginSignal("onCloudSaveReturn", StateCode.CLOUD_SAVE_GET_ARCHIVE_COVER_SUCCESS, base64String)
        }

        override fun onRequestError(errorCode: Int, errorMessage: String)
        {
            _godotTdsPlugin.emitPluginSignal("onCloudSaveReturn", StateCode.CLOUD_SAVE_GET_ARCHIVE_COVER_FAIL, errorMessage)
        }
    }
}