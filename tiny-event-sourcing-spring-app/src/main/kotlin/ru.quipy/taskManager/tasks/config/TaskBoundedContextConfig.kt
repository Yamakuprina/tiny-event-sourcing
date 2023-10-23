package ru.quipy.taskManager.tasks.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.core.EventSourcingService
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.taskManager.tasks.api.TaskAggregate
import ru.quipy.taskManager.tasks.logic.Task
import java.util.*

@Configuration
class TaskBoundedContextConfig {

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Bean
    fun taskEsService(): EventSourcingService<UUID, TaskAggregate, Task> =
        eventSourcingServiceFactory.create()
}