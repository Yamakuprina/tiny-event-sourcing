package ru.quipy.taskManager.users.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.core.EventSourcingService
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.taskManager.users.api.UserAggregate
import ru.quipy.taskManager.users.logic.User
import ru.quipy.taskManager.users.service.UserCacheService
import java.util.*
import javax.annotation.PostConstruct

@Configuration
class UserBoundedContextConfig {

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Autowired
    private lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @Autowired
    private lateinit var userCacheService: UserCacheService

    @PostConstruct
    fun init() {
        subscriptionsManager.subscribe<UserAggregate>(userCacheService)
    }

    @Bean
    fun userEsService(): EventSourcingService<UUID, UserAggregate, User> =
        eventSourcingServiceFactory.create()
}