package ru.quipy.taskManager.projects.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.core.EventSourcingService
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.taskManager.projects.api.ProjectAggregate
import ru.quipy.taskManager.projects.logic.Project
import ru.quipy.taskManager.projects.service.ProjectsCacheService
import java.util.*
import javax.annotation.PostConstruct

@Configuration
class ProjectBoundedContextConfig {

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Autowired
    private lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @Autowired
    private lateinit var projectCacheService: ProjectsCacheService

    @PostConstruct
    fun init() {
        subscriptionsManager.subscribe<ProjectAggregate>(projectCacheService)
    }

    @Bean
    fun projectsEsService(): EventSourcingService<UUID, ProjectAggregate, Project> =
        eventSourcingServiceFactory.create()
}