package com.github.jingshouyan.sb40k.im.controller

import com.github.jingshouyan.sb40k.base.R
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/im/v1")
class LoginController {

    @RequestMapping("/ping")
    fun ping(): R {
        return R.success("pong")
    }

    fun login(req: LoginReq): R {

        return R.success()
    }
}

data class LoginReq(
    val username: String,
    val password: String,
    val deviceType: Int,
    val deviceId: String
)