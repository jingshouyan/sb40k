package com.github.jingshouyan.sb40k.config

import com.github.jingshouyan.sb40k.entity.Ticket
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class AuditorAwareImpl : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        val ticket = SecurityContextHolder.getContext().authentication?.principal as? Ticket ?: return Optional.empty()
        return Optional.of(ticket.userId.toString())
    }
}