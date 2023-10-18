package ru.quipy.taskManager.users.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.core.EventSourcingService
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.taskManager.users.api.UserAggregate
import ru.quipy.taskManager.users.logic.User
import java.util.*

@Configuration
class UserBoundedContextConfig {

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Bean
    fun userEsService(): EventSourcingService<UUID, UserAggregate, User> =
        eventSourcingServiceFactory.create()
}