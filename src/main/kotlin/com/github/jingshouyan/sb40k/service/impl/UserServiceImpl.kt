package com.github.jingshouyan.sb40k.service.impl

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.github.jingshouyan.sb40k.base.BizException
import com.github.jingshouyan.sb40k.base.C
import com.github.jingshouyan.sb40k.base.R
import com.github.jingshouyan.sb40k.base.RC
import com.github.jingshouyan.sb40k.config.BizConfig
import com.github.jingshouyan.sb40k.entity.User
import com.github.jingshouyan.sb40k.mapper.UserMapper
import com.github.jingshouyan.sb40k.service.UserService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl(
    private val userMapper: UserMapper,
    private val cfg: BizConfig,
    private val encoder: PasswordEncoder = BCryptPasswordEncoder()
) : UserService {


    override fun addUser(user: User): User {
        // check email uniqueness
        if (!user.email.isNullOrBlank()) {
            val existingByEmail = userMapper.selectOne(
                LambdaQueryWrapper<User>().eq(User::email, user.email)
            )
            if (existingByEmail != null) {
                throw BizException(RC.ALREADY_EXISTS, "email already exists")
            }
        }
        // check phone uniqueness
        if (!user.phone.isNullOrBlank()) {
            val existingByPhone = userMapper.selectOne(
                LambdaQueryWrapper<User>().eq(User::phone, user.phone)
            )
            if (existingByPhone != null) {
                throw BizException(RC.ALREADY_EXISTS, "phone already exists")
            }
        }
        val pwd = user.password
        user.password = encoder.encode(pwd).toString()
        userMapper.insert(user)
        user.password = pwd
        return user
    }

    override fun getUser(idType: Int, id: String): Optional<User> {
        return when (idType) {
            C.ID_TYPE_USERNAME -> Optional.ofNullable(
                userMapper.selectOne(
                    LambdaQueryWrapper<User>().eq(User::username, id)
                )
            )
            C.ID_TYPE_USERID -> Optional.ofNullable(userMapper.selectById(id))
            C.ID_TYPE_EMAIL -> Optional.ofNullable(
                userMapper.selectOne(
                    LambdaQueryWrapper<User>().eq(User::email, id)
                )
            )
            C.ID_TYPE_PHONE -> Optional.ofNullable(
                userMapper.selectOne(
                    LambdaQueryWrapper<User>().eq(User::phone, id)
                )
            )
            else -> Optional.empty()
        }
    }

    override fun checkPassword(user: User, password: String): R<Any?> {
        val now = System.currentTimeMillis()
        // check if user is locked
        if (user.unlockedAt > now) {
            return R.error(RC.USER_LOCKED, mapOf("unlockAt" to user.unlockedAt))
        }

        if (encoder.matches(password, user.password)) {
            return R.success()
        }

        if (now - user.firstTryAt > cfg.passwordTryPeriodInSeconds * 1000) {
            user.firstTryAt = now
            user.tryCount = 1
        } else {
            user.tryCount++
        }
        // lock user if try count exceeds max try count
        if (user.tryCount >= cfg.passwordMaxTry) {
            user.unlockedAt = now + cfg.passwordLockExpireSeconds * 1000
        }
        userMapper.updateById(user)
        // check if user is locked after update try count
        if (user.unlockedAt > now) {
            return R.error(RC.USER_LOCKED, mapOf("unlockAt" to user.unlockedAt))
        }
        return R.error(RC.PASSWORD_INCORRECT, mapOf("tryCount" to user.tryCount))
    }


}
