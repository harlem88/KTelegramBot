package org.dronix.ktelegramboot

import com.google.gson.Gson
import gnu.io.CommPortIdentifier
import gnu.io.SerialPort
import org.dronix.ktelegramboot.model.arduino.EventModel
import java.io.OutputStream

class ArduinoPixel internal constructor(
        serviceProvider: ArduinoPixelService
) : ArduinoPixelService by serviceProvider {
    companion object {
        @JvmStatic
        fun create(portName: String?): ArduinoPixel {
            val aa = ArduinoPixelServiceImpl()
            System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0")
            val results = CommPortIdentifier.getPortIdentifiers().toList().filter { (it as CommPortIdentifier).name == portName  }

            if (results.isNotEmpty()) {
                val portIdentifier: CommPortIdentifier = results[0] as CommPortIdentifier
                if (portIdentifier.isCurrentlyOwned) {
                    println("Error: Port is currently in use")
                } else {
                    println("open on ${portIdentifier.name}")
                    val timeout = 2000
                    val commPort = portIdentifier.open(ArduinoPixel::class.java.name, timeout)

                    if (commPort is SerialPort) {
                        var serialPort = commPort
                        serialPort.setSerialPortParams(115200,
                                SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_1,
                                SerialPort.PARITY_NONE)

                        aa.out = serialPort.outputStream
                    }
                }
            }
            return ArduinoPixel(aa)
        }
    }


    class ArduinoPixelServiceImpl : ArduinoPixelService {
        var out: OutputStream? = null


        fun writeData(data: String) {
            try {
                val d: CharArray = data.toCharArray()
                for (i in d.indices) {
                    out?.write(d[i].toInt())
                }
                out
            } catch (e: Exception) {
                println("could not write to port")
            }

        }

        override fun sendPushEvent(name: String?, refs: String?) {
            val event =  EventModel("push", name, refs)
            Thread{
                val json = Gson().toJson(event) + "\n"
                println(json)
                writeData(json)
            }.start()

        }

    }
}