package org.dronix.ktelegramboot.api

import okhttp3.MediaType
import okhttp3.RequestBody
import org.dronix.ktelegramboot.model.Message
import retrofit2.Call
import retrofit2.http.*
import java.nio.file.Files
import com.google.gson.annotations.SerializedName as Name
import java.io.File as IoFile

private val PLAIN_TEXT_MIME = MediaType.parse("text/plain")
private val APPLICATION_JSON_MIME = MediaType.parse("application/json")

data class Response<T>(
        val result: T?,
        val ok: Boolean,
        @Name("error_code") val errorCode: Int?,
        @Name("description") val errorDescription: String?)

private fun inputFile(file: IoFile, mimeType: String? = null): RequestBody {
    return RequestBody.create(MediaType.parse(mimeType ?: Files.probeContentType(file.toPath())), file)
}

//private val GSON = Gson()

private fun requestString(text: String) = RequestBody.create(PLAIN_TEXT_MIME, text)

private fun requestJson(text: String) = RequestBody.create(APPLICATION_JSON_MIME, text)

interface TelegramBotService {

    @POST("sendMessage") @FormUrlEncoded
    fun sendMessage(
            @Field("chat_id") chatId: String,
            @Field("text") text: String,
            @Field("parse_mode") parseMode: String? = null,
            @Field("disable_web_page_preview") disableWebPagePreview: Boolean? = null,
            @Field("disable_notification") disableNotification: Boolean? = null,
            @Field("reply_to_message_id") replyToMessageId: Int? = null
    ): Call<Response<Message>>

    @POST("sendMessage") @FormUrlEncoded
    fun sendMessage(
            @Field("chat_id") chatId: Long,
            @Field("text") text: String,
            @Field("parse_mode") parseMode: String? = null,
            @Field("disable_web_page_preview") disableWebPagePreview: Boolean? = null,
            @Field("disable_notification") disableNotification: Boolean? = null,
            @Field("reply_to_message_id") replyToMessageId: Int? = null
    ): Call<Response<Message>>
}