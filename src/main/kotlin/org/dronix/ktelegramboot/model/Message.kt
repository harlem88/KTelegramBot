package org.dronix.ktelegramboot.model
import com.google.gson.annotations.SerializedName as Name

data class Message(
        @Name("message_id") val messageId: Long,
        val from: User?,
        val date: Int,
        val chat: Chat,
        @Name("edit_date") val editDate: Int?,
        val text: String?)