package org.dronix.ktelegramboot

interface ArduinoPixelService {

    fun sendPushEvent(name: String?, refs : String?)
    fun sendEvent(name: String)
}