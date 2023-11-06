package ru.quipy.taskManager.tasks.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.core.EventSourcingService
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.taskManager.tasks.api.TaskAggregate
import ru.quipy.taskManager.tasks.logic.Task
import ru.quipy.taskManager.tasks.service.TaskCacheService
import java.util.*
import javax.annotation.PostConstruct

@Configuration
class TaskBoundedContextConfig {

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Autowired
    private lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @Autowired
    private lateinit var taskCacheService: TaskCacheService

    @PostConstruct
    fun init() {
        subscriptionsManager.subscribe<TaskAggregate>(taskCacheService)
    }

    @Bean
    fun taskEsService(): EventSourcingService<UUID, TaskAggregate, Task> =
        eventSourcingServiceFactory.create()
}