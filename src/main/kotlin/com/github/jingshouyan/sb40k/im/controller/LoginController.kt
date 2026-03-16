package com.github.jingshouyan.sb40k.im.controller

import com.github.jingshouyan.sb40k.base.C
import com.github.jingshouyan.sb40k.base.R
import com.github.jingshouyan.sb40k.config.BizConfig
import com.github.jingshouyan.sb40k.entity.Ticket
import com.github.jingshouyan.sb40k.service.UserService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@RestController
@RequestMapping("/im/v1")
class LoginController(
    private val userService: UserService,
    private val bizConfig: BizConfig,

    ) {

    @RequestMapping("/ping")
    fun ping(): R {
        return R.success("pong")
    }

    @RequestMapping("/login")
    @OptIn(ExperimentalUuidApi::class)
    fun login(req: LoginReq): R {
        val u = userService.checkPassword(C.ID_TYPE_USERNAME, req.username, req.password)
        val token = Uuid.generateV7().toHexString()
        val ticket = Ticket(
            token,
            req.deviceType,
            req.deviceId,
            System.currentTimeMillis() + bizConfig.tokenExpireSeconds * 1000,
            u
        )
        return R.success(ticket)
    }
}

data class LoginReq(
    val username: String,
    val password: String,
    val deviceType: Int,
    val deviceId: String
)