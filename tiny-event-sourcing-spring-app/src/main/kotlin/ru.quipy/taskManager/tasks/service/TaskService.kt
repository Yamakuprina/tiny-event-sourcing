package ru.quipy.taskManager.tasks.service

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TaskService(
    val taskCacheRepository: TaskCacheRepository
) {

    fun searchTasksByAssignee(assignee: UUID): List<UUID> {
        return taskCacheRepository.findAll().filter { it.assignee == assignee }.map { it.taskId }
    }

    fun searchTasksByStatus(statusId: UUID): List<UUID> {
        return taskCacheRepository.findAll().filter { it.statusId == statusId }.map { it.taskId }
    }

    fun searchTasksByNameContains(query: String): List<UUID> {
        return taskCacheRepository.findAll().filter { it.name.contains(query) }.map { it.taskId }
    }
}