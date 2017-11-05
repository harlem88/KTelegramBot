package org.dronix.ktelegramboot.model.github

data class PushData (val ref: String?, val commits:Array<CommitData>, val sender: SenderData, val repository: RepositoryData)
