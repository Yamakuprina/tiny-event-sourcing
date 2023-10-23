package ru.quipy.taskManager.tasks.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.core.EventSourcingService
import ru.quipy.taskManager.tasks.api.TaskAggregate
import ru.quipy.taskManager.tasks.api.TaskCreatedEvent
import ru.quipy.taskManager.tasks.logic.Task
import ru.quipy.taskManager.tasks.service.TaskService
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(
    val tasksEsService: EventSourcingService<UUID, TaskAggregate, Task>,
    val taskService: TaskService
) {

    @PostMapping("/create")
    fun createTask(
        @RequestParam taskName: String,
        @RequestParam projectId: UUID,
        @RequestParam statusId: UUID,
        @RequestParam assignee: UUID?,
        @RequestParam description: String?
    ): TaskCreatedEvent {
        return tasksEsService.create {
            it.createNewTask(
                projectId = projectId,
                name = taskName,
                statusId = statusId,
                assignee = assignee,
                description = description
            )
        }
    }

    @PostMapping("/{taskId}/assignee")
    fun changeTaskAssignee(
        @PathVariable taskId: String,
        @RequestParam assignee: UUID,
    ) {
        tasksEsService.update(UUID.fromString(taskId)) {
            it.changeAssignee(
                id = UUID.fromString(taskId),
                assignee = assignee,
            )
        }
    }

    @DeleteMapping("/{taskId}/assignee")
    fun deleteTaskAssignee(
        @PathVariable taskId: String,
        @RequestParam assignee: UUID,
    ) {
        tasksEsService.update(UUID.fromString(taskId)) {
            it.removeAssignee(UUID.fromString(taskId))
        }
    }

    @GetMapping("/{taskId}")
    fun getTask(
        @PathVariable taskId: String,
    ): Task? {
        return tasksEsService.getState(UUID.fromString(taskId))
    }

    @GetMapping("/search/assignee")
    fun searchTasksByAssignee(
        @RequestParam assignee: UUID,
    ): List<Task> {
        return taskService.searchTasksByAssignee(assignee).mapNotNull { tasksEsService.getState(it) }
    }

    @GetMapping("/search/status")
    fun searchTasksByStatus(
        @RequestParam statusId: UUID,
    ): List<Task> {
        return taskService.searchTasksByStatus(statusId).mapNotNull { tasksEsService.getState(it) }
    }

    @GetMapping("/search/name")
    fun searchTasksByNameContains(
        @RequestParam query: String,
    ): List<Task> {
        return taskService.searchTasksByNameContains(query).mapNotNull { tasksEsService.getState(it) }
    }

    @PostMapping("/{taskId}/status")
    fun changeTaskStatus(
        @PathVariable taskId: String,
        @RequestParam statusId: UUID,
    ) {
        tasksEsService.update(UUID.fromString(taskId)) {
            it.updateStatus(
                id = UUID.fromString(taskId),
                newStatusId = statusId
            )
        }
    }

    @PostMapping("/{taskId}/description")
    fun changeTaskDescription(
        @PathVariable taskId: String,
        @RequestParam description: String,
    ) {
        tasksEsService.update(UUID.fromString(taskId)) {
            it.changeDescription(
                description = description
            )
        }
    }

    @DeleteMapping("/{taskId}")
    fun deleteTask(
        @PathVariable taskId: String
    ) {
        tasksEsService.update(UUID.fromString(taskId)) {
            it.delete(
                id = UUID.fromString(taskId),
            )
        }
    }
}
