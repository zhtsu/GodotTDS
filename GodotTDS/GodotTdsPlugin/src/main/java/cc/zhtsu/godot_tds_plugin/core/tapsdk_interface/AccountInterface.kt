package cc.zhtsu.godot_tds_plugin.core.tapsdk_interface

interface AccountInterface
{
    fun login(
        publicProfileEnabled : Boolean,
        userFriendsEnabled : Boolean
    )
    fun logout()
    fun isLoggedIn() : Boolean
    fun getAccountOpenId() : String
    fun getCurrentAccountAsString() : String
}