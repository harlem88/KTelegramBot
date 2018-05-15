import com.google.gson.Gson
import io.javalin.Javalin
import jssc.SerialPortList
import org.dronix.ktelegramboot.ArduinoPixel
import org.dronix.ktelegramboot.ArduinoService
import org.dronix.ktelegramboot.TelegramBot
import org.dronix.ktelegramboot.model.PortModel
import org.dronix.ktelegramboot.model.github.PingData
import org.dronix.ktelegramboot.model.github.PushData
import java.io.BufferedReader
import java.io.InputStreamReader


var gson: Gson? = null
var bot : TelegramBot?= null
var arduino : ArduinoService?= null
var token: String? =null
var idChat: Long ? = null

fun main(args: Array<String>){
    println("=========== START ===================")
    gson = Gson()
    if(args.size > 1){
        token = args[0]
        idChat = args[1].toLong()
        println(" chat id $idChat")
    }

    bot =  TelegramBot.create(token)
    detectSerial()

    val app = Javalin.start(16788)

    app.post("/payload"){ ctx ->

        val event  = ctx.header("X-GitHub-Event")
        println("New Event $event")
        if(event!= null && event.isNotEmpty()) {
            val json = ctx.body()
            when (event) {
                "create" ->  onCreateBranchEvent(json)
                "delete" ->  onDeleteBranchEvent(json)
                "ping"   ->  onPingEvent(json)
                "push"   ->  onPushEvent(json)
                else     ->  { println("Body ${ctx.body()}") }
//            "pull_request" -> onPushEvent()
//            "team_add" -> onPushEvent()
//            "repository" -> onPushEvent()
//            "issue" -> onIssueEvent()
            }
        }
    }

    app.get("/labraceaccesa"){ ctx ->

        onBraceEvent("test")
    }
}

fun detectSerial() {

    val ports = getPorts()

    for ((name, vendorId) in ports) {
        when (vendorId) {
            "8087" ->  {
                arduino = ArduinoService(name)
                arduino?.setup()
            }
        }
    }

    addShutdownListener()
}


fun onPushEvent(json : String){
    val pushData = gson?.fromJson(json, PushData::class.java)
    if (idChat != null) {
        println("send to $idChat")
        bot?.sendMessage(idChat!!, "" +
                "*${pushData?.commits?.last()?.author?.name}* \n" +
                "PUSHED on :\n" +
                "*${pushData?.repository?.name}*\n" +
                "branch :\n" +
                "*${pushData?.ref?.split('/')?.get(2)}* \n"+
                "message :\n" +
                "*${pushData?.commits?.last()?.message}* \n"+
                "[inline URL](${pushData?.commits?.last()?.url})", "Markdown")?.execute()
    }
    runSound(pushData?.commits?.last()?.author?.name)
    Thread {
        Thread.sleep(2000)
        arduino?.sendPushEvent(pushData?.commits?.last()?.author?.name, "${pushData?.repository?.name} ${pushData?.ref?.split('/')?.get(2)}")
    }.start()
}

fun onBraceEvent(name : String){
    Thread {
        Thread.sleep(2000)
        arduino?.sendEvent(name)
    }.start()
}

fun onCreateBranchEvent(json : String){

}

fun onDeleteBranchEvent(json : String){

}

fun onPingEvent(json : String){
    val pingData = gson?.fromJson(json, PingData::class.java)
    println("PingData ${pingData?.hook_id}")
    if (idChat != null) {
        bot?.sendMessage(idChat!!, "**test ping${pingData?.hook_id}** \n https://github.com/UDOOboard/udoo-iot-cloud-client/commit/ce830aa984bdc5b1cae9618f0ac5c32ac1f402b1 ")?.execute()
    }
}

fun runSound(user : String?){
    Thread{
        if (user != null && user.equals("Alessandro Genovese", true)) {
            Runtime.getRuntime().exec("mpg123 -q /home/udoo/code/KTelegramBot/soundGeno.mp3")
        } else {
            Runtime.getRuntime().exec("mpg123 -q /home/udoo/code/KTelegramBot/sound1.mp3")
        }
    }.start()
}

fun addShutdownListener(){
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            println("Closing serial port")
            arduino?.close()
        }
    })
}

fun getPorts() : List<PortModel> {
    val ports = ArrayList<PortModel>()
    val portNames = SerialPortList.getPortNames()
    for (name in portNames) {
        try {
            val rt = Runtime.getRuntime()
            val proc = rt.exec("udevadm info --query=property $name")

            var line: String?
            val br = BufferedReader(InputStreamReader(proc.inputStream))
            line = br.readLine()

            var productId  : String?=null
            var vendorId : String?=null

            while (line != null) {
                if(line.startsWith("ID_MODEL_ID=")){
                    productId = line.split("ID_MODEL_ID=")[1]
                }else if(line.startsWith("ID_VENDOR_ID=")){
                    vendorId = line.split("ID_VENDOR_ID=")[1]
                }
                line = br.readLine()
            }

            br.close()

            if(productId != null && vendorId != null){
                ports.add(PortModel(name, vendorId, productId))
            }
        } catch (t: Throwable) {}

        if(ports.filter { it.vendorId == "8087" }.count() <= 0){
            ports.add(PortModel("/dev/ttyACM0", "8087", ""))
        }
    }
    return ports
}
