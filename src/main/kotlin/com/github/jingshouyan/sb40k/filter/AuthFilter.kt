package com.github.jingshouyan.sb40k.filter

import com.github.jingshouyan.sb40k.service.TicketService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class AuthFilter(val ticketService: TicketService) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (!authHeader.isNullOrBlank() && authHeader.startsWith("Bearer ")) {

            val token = authHeader.substring(7)
            val ticket = ticketService.varifyToken(token)
            if (ticket != null) {

                val authentication = UsernamePasswordAuthenticationToken(
                    ticket,
                    token,
                    emptyList()
                )

                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        chain.doFilter(request, response)
    }
}