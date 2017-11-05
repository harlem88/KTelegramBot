package org.dronix.ktelegramboot.model
import com.google.gson.annotations.SerializedName as Name

data class Chat(
        val id: Long,
        val type: String,
        val title: String?,
        @Name("username") val userName: String?,
        @Name("first_name") val firstName: String?,
        @Name("last_name") val lastName: String?)