package ru.quipy.taskManager.projects.logic

import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.taskManager.projects.api.*
import java.util.*

class Project : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    private lateinit var projectName: String
    private lateinit var members: MutableList<UUID>
    private lateinit var statuses: MutableList<Status>
    override fun getId() = projectId
    fun getStatuses() = statuses
    fun getMembers() = members

    fun createNewProject(
        id: UUID = UUID.randomUUID(),
        projectName: String,
        members: MutableList<UUID> = mutableListOf(),
        statuses: MutableList<Status> = mutableListOf()
    ): ProjectCreatedEvent {
        return ProjectCreatedEvent(id, projectName, members, statuses)
    }

    fun addMember(newMember: UUID): ProjectMemberAddedEvent {
        return ProjectMemberAddedEvent(newMember)
    }

    fun addStatus(
        statusId: UUID = UUID.randomUUID(),
        statusName: String,
        color: String,
        numberOfTasksInStatus: Long
    ): ProjectNewStatusCreatedEvent {
        return ProjectNewStatusCreatedEvent(statusId, statusName, color, numberOfTasksInStatus)
    }

    fun changeStatusName(
        statusId: UUID,
        statusName: String,
    ): ProjectStatusNameChangedEvent {
        return ProjectStatusNameChangedEvent(statusId, statusName)
    }

    fun changeStatusColor(
        statusId: UUID,
        statusColor: String,
    ): ProjectStatusColorChangedEvent {
        return ProjectStatusColorChangedEvent(statusId, statusColor)
    }

    fun removeStatus(statusId: UUID): ProjectStatusRemovedEvent {
        return ProjectStatusRemovedEvent(statusId)
    }

    fun removeMember(member: UUID): ProjectMemberRemovedEvent {
        return ProjectMemberRemovedEvent(member)
    }

    fun memberIsPresent(member: UUID): Boolean {
        return members.contains(member)
    }

    fun incrementStatusTasks(status: UUID){
        statuses.find { it.id == status }?.numberOfTasksInStatus = statuses.find { it.id == status }?.numberOfTasksInStatus!! + 1
    }

    fun decrementStatusTasks(status: UUID){
        statuses.find { it.id == status }?.numberOfTasksInStatus = statuses.find { it.id == status }?.numberOfTasksInStatus!! - 1
    }

    @StateTransitionFunc
    fun createNewProject(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectName = event.projectName
        members = event.members
        statuses = event.statuses
    }

    @StateTransitionFunc
    fun addMember(event: ProjectMemberAddedEvent) {
        members.add(event.newMember)
    }

    @StateTransitionFunc
    fun removeMember(event: ProjectMemberRemovedEvent) {
        members.removeIf { it == event.member }
    }

    @StateTransitionFunc
    fun changeStatusName(event: ProjectStatusNameChangedEvent) {
        statuses.find { it.id == event.statusId }?.name = event.statusName
    }

    @StateTransitionFunc
    fun removeStatus(event: ProjectStatusRemovedEvent) {
        statuses.removeIf { it.id == event.statusId }
    }

    @StateTransitionFunc
    fun changeStatusColor(event: ProjectStatusColorChangedEvent) {
        statuses.find { it.id == event.statusId }?.color = event.color
    }

    @StateTransitionFunc
    fun addStatus(event: ProjectNewStatusCreatedEvent) {
        statuses.add(
            Status(
                id = event.statusId,
                name = event.statusName,
                color = event.color,
                numberOfTasksInStatus = event.numberOfTasksInStatus
            )
        )
    }
}