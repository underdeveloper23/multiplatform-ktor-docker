package com.hacktopiealgoridom.postgresql.fullstack.service

import com.hacktopiealgoridom.postgresql.fullstack.module.Module

class Service(private val modules: Map<String, Module>) {
    fun getModule(region: String): Module? = modules[region]
    // Implement other region-specific services here
}
