package com.hacktopiealgoridom.postgresql.fullstack.module

interface Module {
    fun getUserConsent(userId: Int): Boolean
    fun updateUserConsent(userId: Int, consent: Boolean)
    fun getConsentForUserAndArea(): Any
}