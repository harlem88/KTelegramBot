package org.dronix.ktelegramboot.model
import com.google.gson.annotations.SerializedName as Name

data class User(
        val id: Long,
        @Name("first_name") val firstName: String,
        @Name("last_name") val lastName: String?,
        @Name("username") val userName: String?)