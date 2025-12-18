package cc.zhtsu.godot_tds_plugin.core.tapsdk_interface

interface CloudSaveInterface
{
    fun initialize()
    fun destroy()
    fun createArchive(
        name: String,
        summary: String,
        extra: String,
        playtime: Int,
        archiveFilePath: String,
        archiveCoverPath: String
    )
    fun getArchiveList()
    fun getArchiveData(archiveUuid: String, archiveFileId: String)
    fun updateArchive(
        archiveUuid: String,
        name: String,
        summary: String,
        extra: String,
        playtime: Int,
        archiveFilePath: String,
        archiveCoverPath: String
    )
    fun deleteArchive(archiveUuid: String)
    fun getArchiveCover(archiveUuid: String, archiveFileId: String)
}