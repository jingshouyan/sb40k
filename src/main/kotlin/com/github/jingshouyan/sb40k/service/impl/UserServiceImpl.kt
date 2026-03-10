package com.github.jingshouyan.sb40k.service.impl

import com.github.jingshouyan.sb40k.base.BizException
import com.github.jingshouyan.sb40k.base.RC
import com.github.jingshouyan.sb40k.config.BizConfig
import com.github.jingshouyan.sb40k.entity.User
import com.github.jingshouyan.sb40k.repository.UserRepository
import com.github.jingshouyan.sb40k.service.UserService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl(
    private val userRepo: UserRepository,
    private val cfg: BizConfig,
    private val encoder: PasswordEncoder = BCryptPasswordEncoder()
) : UserService {


    override fun addUser(user: User): User {
        val pwd = user.password
        user.password = encoder.encode(pwd).toString()
        userRepo.save(user)
        user.password = pwd
        return user
    }

    override fun checkPassword(idType: Int, id: String, password: String): User {
        val now = System.currentTimeMillis()
        var user: Optional<User> = Optional.empty()
        when (idType) {
            ID_TYPE_USERNAME -> user = userRepo.findByUsername(id)
            ID_TYPE_USERID -> user = userRepo.findById(id.toLong())
        }
        if (user.isEmpty) throw BizException(RC.NOT_FOUND)
        val u = user.get()
        // check if user is locked
        if (u.unlockedAt > now) {
            throw BizException(RC.USER_LOCKED, mapOf("unlockAt" to u.unlockedAt))
        }

        if (encoder.matches(password, u.password)) {
            return u
        }

        if (now - u.firstTryAt > cfg.passwordTryPeriodInSeconds * 1000) {
            u.firstTryAt = now
            u.tryCount = 1
        } else {
            u.tryCount++
        }
        // lock user if try count exceeds max try count
        if (u.tryCount >= cfg.passwordMaxTry) {
            u.unlockedAt = now + cfg.passwordLockExpireSeconds * 1000
        }
        userRepo.save(u)
        // check if user is locked after update try count
        if (u.unlockedAt > now) {
            throw BizException(RC.USER_LOCKED, mapOf("unlockAt" to u.unlockedAt))
        }

        throw BizException(RC.PASSWORD_INCORRECT, mapOf("tryCount" to u.tryCount))
    }

    companion object {
        const val ID_TYPE_USERID = 0
        const val ID_TYPE_USERNAME = 1

    }
}