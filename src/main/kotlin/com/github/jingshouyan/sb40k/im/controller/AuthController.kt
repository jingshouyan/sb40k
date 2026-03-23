package com.github.jingshouyan.sb40k.im.controller

import com.github.jingshouyan.sb40k.base.C
import com.github.jingshouyan.sb40k.base.R
import com.github.jingshouyan.sb40k.config.BizConfig
import com.github.jingshouyan.sb40k.entity.Ticket
import com.github.jingshouyan.sb40k.entity.User
import com.github.jingshouyan.sb40k.service.TicketService
import com.github.jingshouyan.sb40k.service.UserService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val userService: UserService,
    private val ticketService: TicketService,
    private val bizConfig: BizConfig,

    ) {

    @RequestMapping("/ping")
    fun ping(): R {
        val ticket = SecurityContextHolder.getContext().authentication?.principal as Ticket
        return R.success(ticket)
    }

    @PostMapping("/signin")
    fun signin(@RequestBody req: SigninReq): R {
        val u = userService.checkPassword(C.ID_TYPE_USERNAME, req.username, req.password)
        println(u.id)
        val ticket = Ticket(
            u.id ?: 0L,
            req.deviceType,
            System.currentTimeMillis(),
            req.deviceId,
        )
        val token = ticketService.saveTicket(ticket)
        return R.success(mapOf("u" to u, "token" to token))
    }

    @PostMapping("/signup")
    fun signup(@RequestBody req: SignupReq): R {
        val u = User(
            username = req.username,
            password = req.password,
            email = req.email,
        )
        val newUser = userService.addUser(u)

        return R.success(newUser)
    }
}

data class SigninReq(
    val username: String,
    val password: String,
    val deviceType: Byte,
    val deviceId: String
)

data class SignupReq(
    val username: String,
    val password: String,
    val email: String,
    val deviceType: Byte,
    val deviceId: String
)