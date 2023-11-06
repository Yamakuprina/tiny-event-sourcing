package ru.quipy.taskManager.projects.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.core.EventSourcingService
import ru.quipy.taskManager.projects.api.*
import ru.quipy.taskManager.projects.logic.Project
import ru.quipy.taskManager.projects.logic.Status
import ru.quipy.taskManager.projects.service.ProjectCache
import ru.quipy.taskManager.projects.service.ProjectService
import ru.quipy.taskManager.users.api.UserAggregate
import ru.quipy.taskManager.users.logic.User
import ru.quipy.taskManager.users.service.UserCache
import ru.quipy.taskManager.users.service.UserService
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectsEsService: EventSourcingService<UUID, ProjectAggregate, Project>,
    val usersEsService: EventSourcingService<UUID, UserAggregate, User>,
    val projectService: ProjectService,
    val userService: UserService
) {

    @PostMapping("/create")
    fun createProject(
        @RequestParam projectName: String,
        @RequestParam creator: UUID,
    ): ProjectCreatedEvent {
        return projectsEsService.create { project ->
            project.createNewProject(
                projectName = projectName
            ).also {
                project.addMember(it.projectId, creator)
            }
        }
    }

    @GetMapping("/{projectId}")
    fun getProject(
        @PathVariable projectId: String
    ): ProjectCache? {
        return projectService.getProject(UUID.fromString(projectId))
    }

    @PostMapping("/{projectId}/members")
    fun addMember(
        @PathVariable projectId: String,
        @RequestParam member: UUID,
        @RequestParam invitor: UUID
    ): ProjectMemberAddedEvent {
        val project = projectsEsService.getState(UUID.fromString(projectId))
            ?: throw IllegalArgumentException("Project doesn't exist")
        if (!project.memberIsPresent(invitor)) throw IllegalArgumentException("Invitor must be in project")
        if (project.memberIsPresent(member)) throw IllegalArgumentException("Member is already present")
        return projectsEsService.update(UUID.fromString(projectId)) {
            it.addMember(UUID.fromString(projectId), member)
        }
    }

    @DeleteMapping("/{projectId}/members")
    fun deleteMember(
        @PathVariable projectId: String,
        @RequestParam member: UUID,
    ): ProjectMemberRemovedEvent {
        val project = projectsEsService.getState(UUID.fromString(projectId))
            ?: throw IllegalArgumentException("Project doesn't exist")
        return projectsEsService.update(UUID.fromString(projectId)) {
            it.removeMember(UUID.fromString(projectId), member)
        }
    }

    @PostMapping("/{projectId}/statuses")
    fun addStatus(
        @PathVariable projectId: String,
        @RequestParam name: String,
        @RequestParam color: String,
    ): ProjectNewStatusCreatedEvent {
        val project = projectsEsService.getState(UUID.fromString(projectId))
            ?: throw IllegalArgumentException("Project doesn't exist")
        return projectsEsService.update(UUID.fromString(projectId)) {
            it.addStatus(
                projectId = UUID.fromString(projectId),
                statusName = name,
                color = color,
                numberOfTasksInStatus = 0
            )
        }
    }

    @PostMapping("/{projectId}/{statusId}/name")
    fun changeStatusName(
        @PathVariable projectId: String,
        @PathVariable statusId: String,
        @RequestParam name: String,
    ): ProjectStatusNameChangedEvent {
        val project = projectsEsService.getState(UUID.fromString(projectId))
            ?: throw IllegalArgumentException("Project doesn't exist")
        return projectsEsService.update(UUID.fromString(projectId)) {
            it.changeStatusName(
                projectId = UUID.fromString(projectId),
                statusId = UUID.fromString(statusId),
                statusName = name,
            )
        }
    }

    @PostMapping("/{projectId}/{statusId}/color")
    fun changeStatusColor(
        @PathVariable projectId: String,
        @PathVariable statusId: String,
        @RequestParam color: String,
    ): ProjectStatusColorChangedEvent {
        val project = projectsEsService.getState(UUID.fromString(projectId))
            ?: throw IllegalArgumentException("Project doesn't exist")
        return projectsEsService.update(UUID.fromString(projectId)) {
            it.changeStatusColor(
                projectId = UUID.fromString(projectId),
                statusId = UUID.fromString(statusId),
                statusColor = color,
            )
        }
    }

    @DeleteMapping("/{projectId}/{statusId}")
    fun removeStatus(
        @PathVariable projectId: String,
        @PathVariable statusId: String
    ): ProjectStatusRemovedEvent {
        val project = projectsEsService.getState(UUID.fromString(projectId))
            ?: throw IllegalArgumentException("Project doesn't exist")
        return projectsEsService.update(UUID.fromString(projectId)) {
            it.removeStatus(
                projectId = UUID.fromString(projectId),
                statusId = UUID.fromString(statusId),
            )
        }
    }

    @GetMapping("/{projectId}/statuses")
    fun getStatuses(
        @PathVariable projectId: String,
    ): List<Status> {
        val project = projectsEsService.getState(UUID.fromString(projectId))
            ?: throw IllegalArgumentException("Project doesn't exist")
        return projectService.getProjectStatuses(UUID.fromString(projectId))
    }

    @GetMapping("/{projectId}/users")
    fun getProjectUsers(@PathVariable projectId: String): List<UserCache> {
        val project = projectsEsService.getState(UUID.fromString(projectId))
            ?: throw IllegalArgumentException("Project doesn't exist")
        return project.getMembers().mapNotNull { userService.getUser(it) }
    }

    @GetMapping("/{projectId}/members")
    fun getMemberNames(
        @PathVariable projectId: String,
    ): List<String> {
        val project = projectsEsService.getState(UUID.fromString(projectId))
            ?: throw IllegalArgumentException("Project doesn't exist")
        return project.getMembers().mapNotNull { usersEsService.getState(it) }.map { it.getName() }
    }
}
