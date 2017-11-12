package org.dronix.ktelegramboot

import com.google.gson.Gson
import gnu.io.CommPortIdentifier
import gnu.io.CommPortIdentifier.getPortIdentifier
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

            val portIdentifier: CommPortIdentifier = CommPortIdentifier.getPortIdentifier(portName)
            if (portIdentifier.isCurrentlyOwned) {
                System.out.println("Error: Port is currently in use");
            } else {
                val timeout = 2000
                val commPort = portIdentifier.open(ArduinoPixel::class.java.name, timeout)

                if (commPort is SerialPort) {
                    var serialPort = commPort
                    serialPort.setSerialPortParams(115200,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE)

                    aa.out = serialPort.getOutputStream()
                }
            }
            return ArduinoPixel(aa)
        }
    }


    class ArduinoPixelServiceImpl : ArduinoPixelService {
        var out: OutputStream? = null

        override fun sendPushEvent(name: String?, refs: String?) {
            val event =  EventModel("push", name, refs)
            Thread{
                val json = Gson().toJson(event) + "\n"
                out?.write(json.toByteArray())
            }

        }

    }
}