package com.github.jingshouyan.sb40k.service

import com.github.jingshouyan.sb40k.entity.Ticket

interface TicketService {
    fun saveTicket(ticket: Ticket): String
    fun getTicket(token: String): Ticket?
    fun removeTicket(ticket: Ticket)
}