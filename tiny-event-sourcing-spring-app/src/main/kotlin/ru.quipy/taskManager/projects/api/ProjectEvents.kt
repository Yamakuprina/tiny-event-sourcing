package ru.quipy.taskManager.projects.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.taskManager.projects.logic.Status
import java.util.*

const val PROJECT_CREATED = "PROJECT_CREATED_EVENT"
const val PROJECT_MEMBER_ADDED = "PROJECT_MEMBER_ADDED"
const val PROJECT_MEMBER_REMOVED = "PROJECT_MEMBER_REMOVED"
const val PROJECT_STATUS_CREATED = "PROJECT_STATUS_CREATED"
const val PROJECT_STATUS_NAME_CHANGED = "PROJECT_STATUS_NAME_CHANGED"
const val PROJECT_STATUS_COLOR_CHANGED = "PROJECT_STATUS_COLOR_CHANGED"
const val PROJECT_STATUS_REMOVED = "PROJECT_STATUS_REMOVED"

@DomainEvent(name = PROJECT_CREATED)
data class ProjectCreatedEvent(
    val projectId: UUID,
    val projectName: String,
    val members: MutableList<UUID>,
    val statuses: MutableList<Status>
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED,
)

@DomainEvent(name = PROJECT_MEMBER_ADDED)
data class ProjectMemberAddedEvent(
    var newMember: UUID,
) : Event<ProjectAggregate>(
    name = PROJECT_MEMBER_ADDED,
)

@DomainEvent(name = PROJECT_MEMBER_REMOVED)
data class ProjectMemberRemovedEvent(
    var member: UUID,
) : Event<ProjectAggregate>(
    name = PROJECT_MEMBER_REMOVED,
)

@DomainEvent(name = PROJECT_STATUS_CREATED)
data class ProjectNewStatusCreatedEvent(
    val statusId: UUID,
    val statusName: String,
    val color: String,
    val numberOfTasksInStatus: Long
) : Event<ProjectAggregate>(
    name = PROJECT_STATUS_CREATED,
)

@DomainEvent(name = PROJECT_STATUS_NAME_CHANGED)
data class ProjectStatusNameChangedEvent(
    val statusId: UUID,
    val statusName: String,
) : Event<ProjectAggregate>(
    name = PROJECT_STATUS_NAME_CHANGED,
)

@DomainEvent(name = PROJECT_STATUS_COLOR_CHANGED)
data class ProjectStatusColorChangedEvent(
    val statusId: UUID,
    val color: String,
) : Event<ProjectAggregate>(
    name = PROJECT_STATUS_COLOR_CHANGED,
)

@DomainEvent(name = PROJECT_STATUS_REMOVED)
data class ProjectStatusRemovedEvent(
    val statusId: UUID,
) : Event<ProjectAggregate>(
    name = PROJECT_STATUS_REMOVED,
)