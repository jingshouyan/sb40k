package com.github.jingshouyan.sb40k.im.controller

import com.github.jingshouyan.sb40k.base.BizException
import com.github.jingshouyan.sb40k.base.C
import com.github.jingshouyan.sb40k.base.R
import com.github.jingshouyan.sb40k.base.RC
import com.github.jingshouyan.sb40k.entity.LoginRecord
import com.github.jingshouyan.sb40k.entity.Ticket
import com.github.jingshouyan.sb40k.entity.User
import com.github.jingshouyan.sb40k.service.LoginRecordService
import com.github.jingshouyan.sb40k.service.TicketService
import com.github.jingshouyan.sb40k.service.UserService
import com.github.jingshouyan.sb40k.service.VerificationCodeService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.*


@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val userService: UserService,
    private val ticketService: TicketService,
    private val loginRecordService: LoginRecordService,
    private val verificationCodeService: VerificationCodeService,
) {

    @RequestMapping("/ping")
    fun ping(): R<Ticket> {
        val ticket = SecurityContextHolder.getContext().authentication?.principal as Ticket
        return R.success(ticket)
    }

    @PostMapping("/signin")
    fun login(@RequestBody req: LoginReq): R<LoginResult> {
        val optUser = userService.getUser(req.idType, req.account);
        if (optUser.isEmpty) {
            throw BizException(RC.NOT_FOUND)
        }
        val u = optUser.get()
        val r = userService.checkPassword(u, req.password)
        if (r.code != RC.SUCCESS) {
            val fakeToken = "fake-$u.id"
            saveLoginRecord(u.id ?: "", fakeToken, r.code, req.deviceInfo)
            throw BizException(r.code, r.data)
        }

        return R.success(processLogin(u, req.deviceInfo))
    }


    @PostMapping("/code-login")
    fun codeLogin(@RequestBody req: CodeLoginReq): R<LoginResult> {
        val verified = verificationCodeService.verify(
            req.codeId, req.code, req.account, req.idType, C.VC_BIZ_TYPE_LOGIN
        )
        if (!verified) {
            throw BizException(RC.PARAM_INVALID)
        }
        val optUser = userService.getUser(req.idType, req.account)
        val u = optUser.orElseGet {
            userService.addUser(
                User(
                    password = UUID.randomUUID().toString(),
                    email = if (req.idType == C.ID_TYPE_EMAIL) req.account else null,
                    phone = if (req.idType == C.ID_TYPE_PHONE) req.account else null,
                    nickname = req.account,
                )
            )
        }
        return R.success(processLogin(u, req.deviceInfo))
    }

    @PostMapping("/code")
    fun code(@RequestBody req: CodeReq): R<String> {
        val id =
            verificationCodeService.trigger(req.account, req.idType, req.businessType.uppercase(), req.lang, req.params)
        return R.success(id)
    }

    @PostMapping("/signup")
    fun signup(@RequestBody req: SignupReq): R<LoginResult> {
        val u = User(
            password = req.password,
            email = req.email,
            phone = req.phone,
            nickname = req.nickname,
        )
        val newUser = userService.addUser(u)

        return R.success(processLogin(newUser, req.deviceInfo))
    }


    @GetMapping("/singout")
    fun signout(): R<Any?> {
        val ticket = SecurityContextHolder.getContext().authentication?.principal as Ticket
        ticketService.removeTicket(ticket)

        loginRecordService.logoutToken(ticket.token)
        return R.success()
    }

    private fun processLogin(
        u: User,
        deviceInfo: DeviceInfo
    ): LoginResult {
        val ticket = Ticket(
            u.id ?: "",
            deviceInfo.deviceType,
            System.currentTimeMillis(),
            deviceInfo.deviceId,
            ""
        )
        val token = ticketService.saveTicket(ticket)

        val authentication = UsernamePasswordAuthenticationToken(
            ticket,
            token,
            emptyList()
        )
        SecurityContextHolder.getContext().authentication = authentication

        saveLoginRecord(u.id ?: "", ticket.token, RC.SUCCESS, deviceInfo)

        return LoginResult(ticket, u)
    }

    private fun saveLoginRecord(userId: String, token: String, resultCode: Int, deviceInfo: DeviceInfo) {
        val record = LoginRecord(
            userId = userId,
            deviceType = deviceInfo.deviceType,
            deviceId = deviceInfo.deviceId,
            token = token,
            clientIP = deviceInfo.clientIP,
            remoteIP = getRemoteIP(),
            deviceName = deviceInfo.deviceName,
            deviceDesc = deviceInfo.deviceDesc,
            loginAt = System.currentTimeMillis(),
            result = resultCode,
            extInfo = deviceInfo.extInfo,
        )
        if (resultCode != RC.SUCCESS) {
            record.logoutAt = -1L
        }

        loginRecordService.addLoginRecord(record)

    }

    private fun getRemoteIP(): String {
        return RequestContextHolder.getRequestAttributes()?.let {
            val request = (it as ServletRequestAttributes).request
            request.remoteAddr
        } ?: "can not get remote IP"
    }

}


data class CodeReq(
    val account: String,
    val idType: Int,
    val businessType: String,
    val lang: String? = null,
    val params: Map<String, String>? = null,
)

data class CodeLoginReq(
    val codeId: String,
    val code: String,
    val account: String,
    val idType: Int,
    val deviceInfo: DeviceInfo,
)

data class LoginReq(
    val account: String,
    val idType: Int,
    val password: String,
    val deviceInfo: DeviceInfo,
)

data class SignupReq(
    val password: String,
    val email: String? = null,
    val phone: String? = null,
    val nickname: String? = null,
    val deviceInfo: DeviceInfo,
)

data class DeviceInfo(
    val deviceType: Byte,
    val deviceId: String,
    val clientIP: String,
    val deviceName: String,
    val deviceDesc: String,
    val extInfo: String,
)

data class LoginResult(
    val ticket: Ticket,
    val user: User,
)