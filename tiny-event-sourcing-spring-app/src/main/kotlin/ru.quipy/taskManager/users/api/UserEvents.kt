package ru.quipy.taskManager.users.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val USER_CREATED = "USER_CREATED_EVENT"
const val USER_CHANGED = "USER_CHANGED_EVENT"

@DomainEvent(name = USER_CREATED)
data class UserCreatedEvent(
    val userId: UUID,
    val userName: String,
    val userNickname: String,
    val userPassword: String
) : Event<UserAggregate>(
    name = USER_CREATED,
)

@DomainEvent(name = USER_CHANGED)
data class UserChangedEvent(
    val userId: UUID,
    val userName: String,
    val userNickname: String,
    val userPassword: String
) : Event<UserAggregate>(
    name = USER_CHANGED,
)
