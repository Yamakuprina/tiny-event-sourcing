package ru.quipy.taskManager.users.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.core.EventSourcingService
import ru.quipy.taskManager.users.api.UserAggregate
import ru.quipy.taskManager.users.api.UserChangedEvent
import ru.quipy.taskManager.users.api.UserCreatedEvent
import ru.quipy.taskManager.users.logic.User
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val usersEsService: EventSourcingService<UUID, UserAggregate, User>
) {

    @PostMapping("/create")
    fun createUser(
        @RequestParam userName: String,
        @RequestParam userNickname: String,
        @RequestParam userPassword: String
    ): UserCreatedEvent {
        return usersEsService.create {
            it.createNewUser(
                userName = userName,
                userNickname = userNickname,
                userPassword = userPassword
            )
        }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID): User? {
        return usersEsService.getState(userId)
    }

    @PostMapping("/change")
    fun changeUser(
        @RequestParam userName: String,
        @RequestParam userNickname: String,
        @RequestParam userPassword: String
    ): UserChangedEvent {
        return usersEsService.create {
            it.changeUser(
                userName = userName,
                userNickname = userNickname,
                userPassword = userPassword
            )
        }
    }
}