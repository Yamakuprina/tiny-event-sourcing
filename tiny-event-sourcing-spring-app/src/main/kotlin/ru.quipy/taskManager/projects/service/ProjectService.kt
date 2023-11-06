package ru.quipy.taskManager.projects.service

import org.springframework.stereotype.Service
import ru.quipy.taskManager.projects.logic.Status
import java.util.UUID

@Service
class ProjectService(
    val projectsCacheRepository: ProjectCacheRepository
) {
    fun getProject(projectId: UUID): ProjectCache? {
        return projectsCacheRepository.findById(projectId).orElse(null)
    }

    fun getProjectStatuses(projectId: UUID): List<Status> {
        return projectsCacheRepository.findById(projectId).get().statuses
    }
}