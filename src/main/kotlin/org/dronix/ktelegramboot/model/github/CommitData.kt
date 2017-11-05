package org.dronix.ktelegramboot.model.github

data class CommitData (val id: String, val message: String, val timestamp: String, val url:String, val author: UserData, val committer: UserData)
