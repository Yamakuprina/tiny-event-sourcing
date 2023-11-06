package ru.quipy.taskManager.tasks.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.core.EventSourcingService
import ru.quipy.taskManager.projects.api.ProjectAggregate
import ru.quipy.taskManager.projects.logic.Project
import ru.quipy.taskManager.projects.logic.Status
import ru.quipy.taskManager.tasks.api.*
import ru.quipy.taskManager.tasks.logic.Task
import ru.quipy.taskManager.tasks.service.TaskCache
import ru.quipy.taskManager.tasks.service.TaskService
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(
    val tasksEsService: EventSourcingService<UUID, TaskAggregate, Task>,
    val projectsEsService: EventSourcingService<UUID, ProjectAggregate, Project>,
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
        projectsEsService.getState(projectId)?.incrementStatusTasks(statusId)
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
    ): TaskAssigneeChangedEvent {
        if (tasksEsService.getState(UUID.fromString(taskId)) == null)
            throw IllegalArgumentException("Task not found")
        return tasksEsService.update(UUID.fromString(taskId)) {
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
    ): TaskRemoveAssignmentEvent {
        if (tasksEsService.getState(UUID.fromString(taskId)) == null)
            throw IllegalArgumentException("Task not found")
        return tasksEsService.update(UUID.fromString(taskId)) {
            it.removeAssignee(UUID.fromString(taskId))
        }
    }

    @GetMapping("/{taskId}")
    fun getTask(
        @PathVariable taskId: String,
    ): TaskCache? {
        return taskService.getTask(UUID.fromString(taskId))
    }

    @GetMapping("/search/assignee")
    fun searchTasksByAssignee(
        @RequestParam assignee: UUID,
    ): List<TaskCache> {
        return taskService.searchTasksByAssignee(assignee)
    }

    @GetMapping("/search/status")
    fun searchTasksByStatus(
        @RequestParam statusId: UUID,
    ): List<TaskCache> {
        return taskService.searchTasksByStatus(statusId)
    }

    @GetMapping("/search/project")
    fun searchTasksByProjectStatuses(@RequestParam projectId: UUID): Map<Status, List<TaskCache>> {
        val statuses = projectsEsService.getState(projectId)?.getStatuses()
            ?: throw IllegalArgumentException("Project not found")
        return statuses.associateWith { taskService.searchTasksByStatus(it.id) }
    }

    @GetMapping("/search/name")
    fun searchTasksByNameContains(
        @RequestParam query: String,
    ): List<TaskCache> {
        return taskService.searchTasksByNameContains(query)
    }

    @PostMapping("/{taskId}/status")
    fun changeTaskStatus(
        @PathVariable taskId: String,
        @RequestParam statusId: UUID,
    ): TaskChangeStatusEvent {
        val task = tasksEsService.getState(UUID.fromString(taskId)) ?: throw IllegalArgumentException("Task not found")
        task.getProjectId()?.let { projectsEsService.getState(it)?.decrementStatusTasks(task.getStatusId()) }
        task.getProjectId()?.let { projectsEsService.getState(it)?.incrementStatusTasks(statusId) }
        return tasksEsService.update(UUID.fromString(taskId)) {
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
    ): TaskDescriptionChanged {
        if (tasksEsService.getState(UUID.fromString(taskId)) == null)
            throw IllegalArgumentException("Task not found")
        return tasksEsService.update(UUID.fromString(taskId)) {
            it.changeDescription(
                description = description
            )
        }
    }

    @DeleteMapping("/{taskId}")
    fun deleteTask(
        @PathVariable taskId: String
    ): TaskDeletedEvent {
        val task = tasksEsService.getState(UUID.fromString(taskId)) ?: throw IllegalArgumentException("Task not found")
        task.getProjectId()?.let { projectsEsService.getState(it)?.decrementStatusTasks(task.getStatusId()) }
        return tasksEsService.update(UUID.fromString(taskId)) {
            it.delete(
                id = UUID.fromString(taskId),
            )
        }
    }
}
