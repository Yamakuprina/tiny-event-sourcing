package ru.quipy.taskManager.projects.logic

import java.util.UUID

data class Status(
    val id: UUID,
    var name: String,
    var color: String,
    var numberOfTasksInStatus: Long
)