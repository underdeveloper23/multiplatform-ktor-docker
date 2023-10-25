package com.hacktopiealgoridom.postgresql.fullstack.consent

import io.ktor.server.application.*

interface ConsentType {
    fun applyConsent(call: ApplicationCall, userId: Int): Boolean
}