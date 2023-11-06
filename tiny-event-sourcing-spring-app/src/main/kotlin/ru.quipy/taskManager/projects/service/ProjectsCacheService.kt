package ru.quipy.taskManager.projects.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import ru.quipy.taskManager.projects.api.*
import ru.quipy.taskManager.projects.logic.Status
import java.util.*

@Service
@AggregateSubscriber(
    aggregateClass = ProjectAggregate::class, subscriberName = "projects::projects-cache"
)
class ProjectsCacheService(
    private val projectCacheRepository: ProjectCacheRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(ProjectsCacheService::class.java)

    @SubscribeEvent
    fun projectCreatedSubscriber(event: ProjectCreatedEvent) {
        val projectCache = ProjectCache(
            projectId = event.projectId,
            projectName = event.projectName,
            members = event.members,
            statuses = event.statuses
        )
        projectCacheRepository.save(
            projectCache
        )
        logger.info("Update project cache $projectCache")
    }

    @SubscribeEvent
    fun projectMemberAddedSubscriber(event: ProjectMemberAddedEvent) {
        val cache = projectCacheRepository.findById(
            event.projectId
        ).get()
        val newMembers = cache.members
        newMembers.add(event.newMember)
        val copy = cache.copy(members = newMembers)
        projectCacheRepository.save(copy)
        logger.info("Update project cache $copy")
    }

    @SubscribeEvent
    fun projectMemberRemovedSubscriber(event: ProjectMemberRemovedEvent) {
        val cache = projectCacheRepository.findById(
            event.projectId
        ).get()
        val newMembers = cache.members
        newMembers.remove(event.member)
        val copy = cache.copy(members = newMembers)
        projectCacheRepository.save(copy)
        logger.info("Update project cache $copy")
    }

    @SubscribeEvent
    fun projectStatusCreatedSubscriber(event: ProjectNewStatusCreatedEvent) {
        val cache = projectCacheRepository.findById(
            event.projectId
        ).get()
        val newStatuses = cache.statuses
        newStatuses.add(Status(event.statusId, event.statusName, event.color, event.numberOfTasksInStatus))
        val copy = cache.copy(statuses = newStatuses)
        projectCacheRepository.save(copy)
        logger.info("Update project cache $copy")
    }

    @SubscribeEvent
    fun projectStatusNameChangedSubscriber(event: ProjectStatusNameChangedEvent) {
        val cache = projectCacheRepository.findById(
            event.projectId
        ).get()
        val newStatuses = cache.statuses
        val status = newStatuses.find { it.id == event.statusId }
        status?.name = event.statusName
        val copy = cache.copy(statuses = newStatuses)
        projectCacheRepository.save(copy)
        logger.info("Update project cache $copy")
    }

    @SubscribeEvent
    fun projectStatusColorChangedSubscriber(event: ProjectStatusColorChangedEvent) {
        val cache = projectCacheRepository.findById(
            event.projectId
        ).get()
        val newStatuses = cache.statuses
        val status = newStatuses.find { it.id == event.statusId }
        status?.color = event.color
        val copy = cache.copy(statuses = newStatuses)
        projectCacheRepository.save(copy)
        logger.info("Update project cache $copy")
    }

    @SubscribeEvent
    fun projectStatusRemovedSubscriber(event: ProjectStatusRemovedEvent) {
        val cache = projectCacheRepository.findById(
            event.projectId
        ).get()
        val newStatuses = cache.statuses
        newStatuses.removeIf { it.id == event.statusId }
        val copy = cache.copy(statuses = newStatuses)
        projectCacheRepository.save(copy)
        logger.info("Update project cache $copy")
    }
}

@Document("projects-cache")
data class ProjectCache(
    @Id
    val projectId: UUID,
    val projectName: String,
    val members: MutableList<UUID>,
    val statuses: MutableList<Status>
)

@Repository
interface ProjectCacheRepository : MongoRepository<ProjectCache, UUID>
