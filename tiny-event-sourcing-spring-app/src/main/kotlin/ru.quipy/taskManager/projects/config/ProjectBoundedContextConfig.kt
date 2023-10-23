package ru.quipy.taskManager.projects.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.core.EventSourcingService
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.taskManager.projects.api.ProjectAggregate
import ru.quipy.taskManager.projects.logic.Project
import java.util.*

@Configuration
class ProjectBoundedContextConfig {

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Bean
    fun projectsEsService(): EventSourcingService<UUID, ProjectAggregate, Project> =
        eventSourcingServiceFactory.create()
}