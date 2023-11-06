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
import ru.quipy.taskManager.users.service.UserCache
import ru.quipy.taskManager.users.service.UserService
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val usersEsService: EventSourcingService<UUID, UserAggregate, User>,
    val userService: UserService
) {

    @PostMapping("/create")
    fun createUser(
        @RequestParam userName: String,
        @RequestParam userNickname: String,
        @RequestParam userPassword: String
    ): UserCreatedEvent {
        if (!userService.checkIfNicknameAvailable(userNickname)) throw IllegalArgumentException("Nickname already taken")
        return usersEsService.create {
            it.createNewUser(
                userName = userName,
                userNickname = userNickname,
                userPassword = userPassword
            )
        }
    }

    @GetMapping("/nickname")
    fun getNicknameAvailable(@RequestParam userNickname: String): Boolean {
        return userService.checkIfNicknameAvailable(userNickname)
    }

    @GetMapping("/search/name")
    fun getUsersNameContains(@RequestParam query: String): List<UserCache> {
        return userService.searchUsersNameContains(query)
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID): UserCache? {
        return userService.getUser(userId)
    }

    @PostMapping("/change")
    fun changeUser(
        @RequestParam userId: UUID,
        @RequestParam userName: String,
        @RequestParam userPassword: String
    ): UserChangedEvent {
        return usersEsService.update(userId) {
            it.changeUser(
                userName = userName,
                userPassword = userPassword
            )
        }
    }
}