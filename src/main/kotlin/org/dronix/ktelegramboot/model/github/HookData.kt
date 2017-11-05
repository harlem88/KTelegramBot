package org.dronix.ktelegramboot.model.github

data class HookData(val type: String, val id : Long, val name : String, val events:Array<String>)