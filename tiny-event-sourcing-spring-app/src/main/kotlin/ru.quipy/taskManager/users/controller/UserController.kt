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
    fun getUsersNameContain(@RequestParam query: String): List<User> {
        return userService.searchUsersNameContains(query).mapNotNull { usersEsService.getState(it) }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID): User? {
        return usersEsService.getState(userId)
    }

    @PostMapping("/change")
    fun changeUser(
        @RequestParam userName: String,
        @RequestParam userPassword: String
    ): UserChangedEvent {
        return usersEsService.create {
            it.changeUser(
                userName = userName,
                userPassword = userPassword
            )
        }
    }
}