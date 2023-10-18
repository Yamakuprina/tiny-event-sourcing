package ru.quipy.taskManager.users.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.taskManager.users.api.UserAggregate
import ru.quipy.taskManager.users.api.UserChangedEvent
import ru.quipy.taskManager.users.api.UserCreatedEvent
import java.util.*
import javax.annotation.PostConstruct

@Component
class UserCacheService(
    private val userCacheRepository: UserCacheRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    private val logger: Logger = LoggerFactory.getLogger(UserCacheService::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "users::users-cache") {
            `when`(UserCreatedEvent::class) { event ->
                val userCache = UserCache(
                    userId = event.userId,
                    userName = event.userName,
                    userNickname = event.userNickname,
                    userPassword = event.userPassword
                )
                userCacheRepository.save(
                    userCache
                )
                logger.info("Update user cache, create user $userCache")
            }
            `when`(UserChangedEvent::class) { event ->
                val cache = userCacheRepository.findById(event.userId).get()
                val userCache = UserCache(
                    userId = event.userId,
                    userName = event.userName,
                    userNickname = cache.userNickname,
                    userPassword = event.userPassword
                )
                userCacheRepository.save(
                    userCache
                )
                logger.info("Update user cache, change user $userCache")
            }
        }
    }
}

@Document("users-cache")
data class UserCache(
    @Id
    val userId: UUID,
    val userName: String,
    val userNickname: String,
    val userPassword: String
)

@Repository
interface UserCacheRepository : MongoRepository<UserCache, UUID>
