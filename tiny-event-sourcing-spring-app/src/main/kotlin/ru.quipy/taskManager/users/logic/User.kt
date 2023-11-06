package ru.quipy.taskManager.users.logic

import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.taskManager.users.api.UserAggregate
import ru.quipy.taskManager.users.api.UserChangedEvent
import ru.quipy.taskManager.users.api.UserCreatedEvent
import java.util.*

class User : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    private lateinit var userName: String
    private lateinit var userNickname: String
    private lateinit var userPassword: String
    override fun getId() = userId
    fun getName() = userName

    fun createNewUser(
        id: UUID = UUID.randomUUID(),
        userName: String,
        userNickname: String,
        userPassword: String
    ): UserCreatedEvent {
        return UserCreatedEvent(id, userName, userNickname, userPassword)
    }

    fun changeUser(
        id: UUID = UUID.randomUUID(),
        userName: String,
        userPassword: String
    ): UserChangedEvent {
        return UserChangedEvent(id, userName, userPassword)
    }

    @StateTransitionFunc
    fun createNewUser(event: UserCreatedEvent) {
        userId = event.userId
        userName = event.userName
        userNickname = event.userNickname
        userPassword = event.userPassword
    }

    @StateTransitionFunc
    fun changeUser(event: UserChangedEvent) {
        userId = event.userId
        userName = event.userName
        userPassword = event.userPassword
    }
}
