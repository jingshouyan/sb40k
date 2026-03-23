package com.github.jingshouyan.sb40k.service

import com.github.jingshouyan.sb40k.entity.Ticket

interface TicketService {
    fun saveTicket(ticket: Ticket): String
    fun varifyToken(token: String): Ticket?
}