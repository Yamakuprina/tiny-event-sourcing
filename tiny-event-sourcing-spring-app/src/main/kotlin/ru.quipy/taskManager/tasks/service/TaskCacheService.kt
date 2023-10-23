package ru.quipy.taskManager.tasks.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.taskManager.tasks.api.*
import ru.quipy.taskManager.users.api.UserAggregate
import ru.quipy.taskManager.users.api.UserChangedEvent
import ru.quipy.taskManager.users.api.UserCreatedEvent
import java.util.*
import javax.annotation.PostConstruct

@Component
class TaskCacheService(
    private val taskCacheRepository: TaskCacheRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    private val logger: Logger = LoggerFactory.getLogger(TaskCacheService::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TaskAggregate::class, "tasks::tasks-cache") {
            `when`(TaskCreatedEvent::class) { event ->
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
            `when`(TaskAssigneeChangedEvent::class) { event ->
                val cache = taskCacheRepository.findById(event.taskId).get()
                val taskCache = cache.copy(assignee = event.assignee)
                taskCacheRepository.save(
                    taskCache
                )
                logger.info("Update task cache, change task $taskCache")
            }
            `when`(TaskRemoveAssignmentEvent::class) { event ->
                val cache = taskCacheRepository.findById(event.taskId).get()
                val taskCache = cache.copy(assignee = null)
                taskCacheRepository.save(
                    taskCache
                )
                logger.info("Update task cache, change task $taskCache")
            }
            `when`(TaskChangeStatusEvent::class) { event ->
                val cache = taskCacheRepository.findById(event.taskId).get()
                val taskCache = cache.copy(statusId = event.newStatusId)
                taskCacheRepository.save(
                    taskCache
                )
                logger.info("Update task cache, change task $taskCache")
            }
            `when`(TaskDeletedEvent::class) { event ->
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
