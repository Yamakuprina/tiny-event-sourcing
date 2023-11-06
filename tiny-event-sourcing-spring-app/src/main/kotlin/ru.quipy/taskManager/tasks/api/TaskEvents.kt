package ru.quipy.taskManager.tasks.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val TASK_CREATED = "TASK_CREATED_EVENT"
const val TASK_ASSIGNEE_CHANGED = "TASK_ASSIGNEE_CHANGED"
const val TASK_DESCRIPTION_CHANGED = "TASK_DESCRIPTION_CHANGED"
const val TASK_REMOVE_ASSIGNMENT = "TASK_REMOVE_ASSIGNMENT"
const val TASK_CHANGE_STATUS = "TASK_CHANGE_STATUS"
const val TASK_DELETED = "TASK_DELETED"

@DomainEvent(name = TASK_CREATED)
data class TaskCreatedEvent(
    val taskId: UUID,
    val taskName: String,
    val projectId: UUID,
    val statusId: UUID,
    val description: String? = null,
    val assignee: UUID? = null
) : Event<TaskAggregate>(
    name = TASK_CREATED,
)

@DomainEvent(name = TASK_ASSIGNEE_CHANGED)
data class TaskAssigneeChangedEvent(
    val taskId: UUID,
    val assignee: UUID
) : Event<TaskAggregate>(
    name = TASK_ASSIGNEE_CHANGED,
)

@DomainEvent(name = TASK_REMOVE_ASSIGNMENT)
data class TaskRemoveAssignmentEvent(
    val taskId: UUID,
) : Event<TaskAggregate>(
    name = TASK_REMOVE_ASSIGNMENT,
)

@DomainEvent(name = TASK_CHANGE_STATUS)
data class TaskChangeStatusEvent(
    val taskId: UUID,
    val newStatusId: UUID
) : Event<TaskAggregate>(
    name = TASK_CHANGE_STATUS,
)

@DomainEvent(name = TASK_DELETED)
data class TaskDeletedEvent(
    val taskId: UUID
) : Event<TaskAggregate>(
    name = TASK_DELETED,
)

@DomainEvent(name = TASK_DESCRIPTION_CHANGED)
data class TaskDescriptionChanged(
    val description: String
) : Event<TaskAggregate>(
    name = TASK_DESCRIPTION_CHANGED,
)
