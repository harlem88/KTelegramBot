package org.dronix.ktelegramboot

import com.google.gson.Gson
import jssc.SerialPort
import jssc.SerialPortException
import org.dronix.ktelegramboot.model.arduino.EventModel
import java.io.OutputStream

class ArduinoService(private val devName: String) : ArduinoPixelService {
    private var port : SerialPort ?= null
    private var mDevName = devName
    private var buffer : StringBuilder = StringBuilder()

    public fun setup() {
        try {
            port = SerialPort(mDevName)
            println("====== arduino init reset port =====")

            port?.openPort()
            port?.setParams(SerialPort.BAUDRATE_1200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE)
            port?.closePort()
            println("====== arduino end reset port =====")

        } catch (e: SerialPortException) {}
    }

    private fun start() {
        setup()

        Thread {
            Thread.sleep(6000)
            println("====== arduino open port =====")
            port?.openPort()
            port?.setParams(SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE)

            val mask = SerialPort.MASK_RXCHAR
            port?.eventsMask = mask

        }.start()
    }

    private fun open(){

        port?.openPort()
        port?.setParams(SerialPort.BAUDRATE_115200,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE)
    }

    private fun writeData(data: String) {

        Thread{
            open()
            Thread.sleep(1000)
            port?.let {
                if(it.isOpened){
                    it.writeString(data)
                }
            }
            Thread.sleep(1000)
            close()
        }.start()
    }

    fun close(){
        port?.let { if(it.isOpened) it.closePort() }
    }

    override fun sendEvent(name: String) {
        val event =  EventModel(name, "", "")
        Thread{
            val json = Gson().toJson(event) + "\n"
            println(json)
            writeData(json)
        }.start()
    }

    var out: OutputStream? = null


    override fun sendPushEvent(name: String?, refs: String?) {
        val event =  EventModel("push", name, refs)
        Thread{
            val json = Gson().toJson(event) + "\n"
            println(json)
            writeData(json)
        }.start()

    }

    fun reset(){
        close()
        port = null
        start()
    }
}
