package com.github.jingshouyan.sb40k.handler

import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.AuthenticationEntryPoint

class RestAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: jakarta.servlet.http.HttpServletRequest,
        response: HttpServletResponse,
        authException: org.springframework.security.core.AuthenticationException
    ) {
        response.contentType = "application/json;charset=UTF-8"
        response.status = 401
        response.writer.write("""{"code":401,"message":"unauthorized"}""")
    }
}