package ru.quipy.taskManager.tasks.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import ru.quipy.taskManager.tasks.api.*
import java.util.*

@Service
@AggregateSubscriber(
    aggregateClass = TaskAggregate::class, subscriberName = "tasks::tasks-cache"
)
class TaskCacheService(
    private val taskCacheRepository: TaskCacheRepository,
) {
    private val logger: Logger = LoggerFactory.getLogger(TaskCacheService::class.java)

    @SubscribeEvent
    fun taskCreatedSubscriber(event: TaskCreatedEvent) {
        val taskCache = TaskCache(
            taskId = event.taskId,
            name = event.taskName,
            statusId = event.statusId,
            projectId = event.projectId,
            description = event.description,
            assignee = event.assignee
        )
        taskCacheRepository.save(
            taskCache
        )
        logger.info("Update task cache, create task $taskCache")
    }

    @SubscribeEvent
    fun taskChangedSubscriber(event: TaskAssigneeChangedEvent) {
        val cache = taskCacheRepository.findById(event.taskId).get()
        val taskCache = cache.copy(assignee = event.assignee)
        taskCacheRepository.save(
            taskCache
        )
        logger.info("Update task cache, change task $taskCache")
    }

    @SubscribeEvent
    fun taskChangedSubscriber(event: TaskRemoveAssignmentEvent) {
        val cache = taskCacheRepository.findById(event.taskId).get()
        val taskCache = cache.copy(assignee = null)
        taskCacheRepository.save(
            taskCache
        )
        logger.info("Update task cache, change task $taskCache")
    }

    @SubscribeEvent
    fun taskChangedSubscriber(event: TaskChangeStatusEvent) {
        val cache = taskCacheRepository.findById(event.taskId).get()
        val taskCache = cache.copy(statusId = event.newStatusId)
        taskCacheRepository.save(
            taskCache
        )
        logger.info("Update task cache, change task $taskCache")
    }

    @SubscribeEvent
    fun taskChangedSubscriber(event: TaskDeletedEvent) {
        val cache = taskCacheRepository.findById(event.taskId).get()
        val taskCache = cache.copy(
            projectId = null,
            assignee = null,
            name = ""
        )
        taskCacheRepository.save(
            taskCache
        )
        logger.info("Update task cache, change task $taskCache")
    }
}

@Document("tasks-cache")
data class TaskCache(
    @Id
    var taskId: UUID,
    var name: String,
    var statusId: UUID,
    var projectId: UUID? = null,
    var description: String? = null,
    var assignee: UUID? = null,
)

@Repository
interface TaskCacheRepository : MongoRepository<TaskCache, UUID>
