package com.hacktopiealgoridom.postgresql.fullstack.application.modules

import com.hacktopiealgoridom.postgresql.fullstack.consent.ConsentType
import com.hacktopiealgoridom.postgresql.fullstack.module.Module
import io.ktor.server.application.*

object ModuleCalifornia : Module, ConsentType {

    override fun getUserConsent(userId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateUserConsent(userId: Int, consent: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getConsentForUserAndArea(): Any {
        TODO("Not yet implemented")
    }

    override fun applyConsent(call: ApplicationCall, userId: Int): Boolean {
        TODO("Not yet implemented")
    }

}
