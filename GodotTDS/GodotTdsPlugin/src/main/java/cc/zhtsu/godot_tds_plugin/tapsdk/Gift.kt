package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.core.StateCode
import cc.zhtsu.godot_tds_plugin.core.GodotTdsPluginModule
import cc.zhtsu.godot_tds_plugin.core.tapsdk_interface.GiftInterface
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
import java.security.SecureRandom

class Gift(activity : Activity, godotTdsPlugin: GodotTdsPlugin):
    GodotTdsPluginModule(activity, godotTdsPlugin),
    GiftInterface
{
    override fun submitGiftCode(giftCode : String)
    {
        val okHttpClient = OkHttpClient()
        val jsonObject = JSONObject()
        val timestamp : String = (System.currentTimeMillis() / 1000).toString()
        val nonceStr : String = _generateNonceStr()
        val objectId : String = _godotTdsPlugin.getAccountOpenId()

        try
        {
            jsonObject.put("client_id", _godotTdsPlugin.getClientId())
            jsonObject.put("gift_code", giftCode)
            jsonObject.put("character_id", objectId)
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

    private var _giftCallback : okhttp3.Callback = object : okhttp3.Callback
    {
        override fun onFailure(call : Call, e : IOException)
        {
            _godotTdsPlugin.emitPluginSignal("onGiftReturn", StateCode.GIFT_CODE_SUBMIT_FAIL, e.message.toString())
        }

        override fun onResponse(call : Call, response : Response)
        {
            var emptyBody = true
            response.body?.let {
                emptyBody = false
                _godotTdsPlugin.emitPluginSignal("onGiftReturn", StateCode.GIFT_CODE_SUBMIT_SUCCESS, it.string())
            }
            if (emptyBody)
            {
                _godotTdsPlugin.emitPluginSignal("onGiftReturn", StateCode.GIFT_CODE_SUBMIT_FAIL, "Empty body")
            }
        }
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
            val signTxt: String = _shaEncode("${timestamp}${nonceStr}${_godotTdsPlugin.getClientId()}")
            return signTxt
        }
        catch (e: java.lang.Exception)
        {
            throw RuntimeException(e)
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
}