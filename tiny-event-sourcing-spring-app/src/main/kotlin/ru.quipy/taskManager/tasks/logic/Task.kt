package ru.quipy.taskManager.tasks.logic

import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.taskManager.tasks.api.*
import java.util.*

class Task : AggregateState<UUID, TaskAggregate> {
    private lateinit var taskId: UUID
    private lateinit var name: String
    private lateinit var statusId: UUID
    private var projectId: UUID? = null
    private var description: String? = null
    private var assignee: UUID? = null
    override fun getId() = taskId
    fun getProjectId() = projectId
    fun getStatusId() = statusId

    fun createNewTask(
        id: UUID = UUID.randomUUID(),
        projectId: UUID,
        name: String,
        statusId: UUID,
        description: String? = null,
        assignee: UUID? = null
    ): TaskCreatedEvent {
        return TaskCreatedEvent(id, name, projectId, statusId, description, assignee)
    }

    fun changeAssignee(
        id: UUID,
        assignee: UUID
    ): TaskAssigneeChangedEvent {
        return TaskAssigneeChangedEvent(id, assignee)
    }

    fun removeAssignee(
        id: UUID
    ): TaskRemoveAssignmentEvent {
        return TaskRemoveAssignmentEvent(id)
    }

    fun updateStatus(
        id: UUID,
        newStatusId: UUID
    ): TaskChangeStatusEvent {
        return TaskChangeStatusEvent(id, newStatusId)
    }

    fun delete(
        id: UUID
    ): TaskDeletedEvent {
        return TaskDeletedEvent(id)
    }

    fun changeDescription(
        description: String
    ): TaskDescriptionChanged {
        return TaskDescriptionChanged(description)
    }

    @StateTransitionFunc
    fun createNewTask(event: TaskCreatedEvent) {
        taskId = event.taskId
        name = event.taskName
        projectId = event.projectId
        statusId = event.statusId
        description = event.description
        assignee = event.assignee
    }

    @StateTransitionFunc
    fun changeAssignee(event: TaskAssigneeChangedEvent) {
        assignee = event.assignee
    }

    @StateTransitionFunc
    fun removeAssignee(event: TaskRemoveAssignmentEvent) {
        assignee = null
    }

    @StateTransitionFunc
    fun changeStatus(event: TaskChangeStatusEvent) {
        statusId = event.newStatusId
    }

    @StateTransitionFunc
    fun deleted(event: TaskDeletedEvent) {
        projectId = null
        assignee = null
        name = ""
    }

    @StateTransitionFunc
    fun changeDescription(event: TaskDescriptionChanged) {
        description = event.description
    }
}