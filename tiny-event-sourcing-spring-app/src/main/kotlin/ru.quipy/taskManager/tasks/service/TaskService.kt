package ru.quipy.taskManager.tasks.service

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TaskService(
    val taskCacheRepository: TaskCacheRepository
) {

    fun searchTasksByAssignee(assignee: UUID): List<TaskCache> {
        return taskCacheRepository.findAll().filter { it.assignee == assignee }
    }

    fun searchTasksByStatus(statusId: UUID): List<TaskCache> {
        return taskCacheRepository.findAll().filter { it.statusId == statusId }
    }

    fun searchTasksByNameContains(query: String): List<TaskCache> {
        return taskCacheRepository.findAll().filter { it.name.contains(query) }
    }

    fun getTask(taskId: UUID): TaskCache {
        return taskCacheRepository.findById(taskId).orElse(null)
    }

}