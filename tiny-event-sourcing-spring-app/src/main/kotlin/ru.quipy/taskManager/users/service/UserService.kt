package ru.quipy.taskManager.users.service

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    val userCacheRepository: UserCacheRepository
) {
    fun checkIfNicknameAvailable(nickname: String): Boolean {
        val users = userCacheRepository.findAll()
        return !users.any { it.userNickname == nickname }
    }

    fun searchUsersNameContains(query: String): List<UUID> {
        return userCacheRepository.findAll().filter { it.userName.contains(query) }.map { it.userId }
    }
}