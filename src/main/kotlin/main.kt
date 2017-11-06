import com.google.gson.Gson
import io.javalin.Javalin
import org.dronix.ktelegramboot.TelegramBot
import org.dronix.ktelegramboot.model.github.PingData
import org.dronix.ktelegramboot.model.github.PushData


var gson: Gson? = null
var bot : TelegramBot?= null
var token: String? =null
var idChat: Long ? = null

fun main(args: Array<String>){
    println("=========== STARt ===================")
    gson = Gson()
    if(args.size > 1){
        token = args[0]
        idChat = args[1].toLong()
        println(" chat id $idChat")
    }

    bot =  TelegramBot.create(token)
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
}


fun onPushEvent(json : String){
    val pushData = gson?.fromJson(json, PushData::class.java)
    if (idChat != null) {
        println("send to $idChat")
        bot?.sendMessage(idChat!!, "" +
                "PUSH => ${pushData?.repository?.name} by ${pushData?.commits?.get(0)?.author?.name} " +
                " ref: ${pushData?.ref}"+
                "${pushData?.commits?.get(0)?.url}")?.execute()
    }
}

fun onCreateBranchEvent(json : String){

}

fun onDeleteBranchEvent(json : String){

}

fun onPingEvent(json : String){
    val pingData = gson?.fromJson(json, PingData::class.java)
    println("PingData ${pingData?.hook_id}")
    if (idChat != null) {
        bot?.sendMessage(idChat!!, "test ping${pingData?.hook_id}")?.execute()
    }
}