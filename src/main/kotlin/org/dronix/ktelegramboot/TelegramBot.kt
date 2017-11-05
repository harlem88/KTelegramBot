package org.dronix.ktelegramboot

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.dronix.ktelegramboot.api.TelegramBotService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class TelegramBot internal constructor(
        serviceProvider: TelegramBotService,
        val timeout: Int = 30
) : TelegramBotService by serviceProvider {
    companion object {
        @JvmStatic
        fun create(token:String ?, timeout: Int = 30): TelegramBot {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val httpClient = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(timeout + 10L, TimeUnit.SECONDS)
                    .readTimeout(timeout + 10L, TimeUnit.SECONDS)
                    .writeTimeout(timeout + 10L, TimeUnit.SECONDS)
                    .build()

            val gson = Gson()
            val adapter = Retrofit.Builder()
                    .baseUrl("https://api.telegram.org/bot$token/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient)
                    .build()

            return TelegramBot(adapter.create(TelegramBotService::class.java), timeout)
        }
    }
}