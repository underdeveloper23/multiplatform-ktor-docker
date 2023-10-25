package com.hacktopiealgoridom.postgresql.fullstack.service

import com.hacktopiealgoridom.postgresql.fullstack.application.modules.ModuleCalifornia
import com.hacktopiealgoridom.postgresql.fullstack.application.modules.ModuleEU
import com.hacktopiealgoridom.postgresql.fullstack.consent.ConsentType
import com.hacktopiealgoridom.postgresql.fullstack.module.Module
import com.hacktopiealgoridom.postgresql.fullstack.route.localisedpoliticsrequirementRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*


class Configuration {

    internal val modules = mutableMapOf<String, Module>()

    fun registerModule(region: String, module: Module) {
        modules[region] = module
    }
}

class UserPlugin(configuration: Configuration) {

    private val modules: Map<String, Module> = configuration.modules

    init {
        if (modules.isEmpty()) {
            error("No modules registered.")
        }
    }

    companion object Feature : Plugin<Application, Configuration, UserPlugin> {

        override val key = AttributeKey<UserPlugin>("Plugin")

        val consentTypes: Map<String, ConsentType> = mapOf(
            "EU" to ModuleEU,
            "California" to ModuleCalifornia,
            // Add other consent types as needed
        )

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): UserPlugin {

            val configuration = Configuration().apply(configure)
            return UserPlugin(configuration).apply {
                pipeline.routing {
                    route("api") {
                        route("localisedpoliticsrequirement") {
                            // Register localised politics requirement routes
                            val service = Service(configuration.modules)
                            localisedpoliticsrequirementRoute(service)
                        }
                    }

                    // Define filter to check consent and apply localised politics requirements
                    intercept(ApplicationCallPipeline.Call) {
                        val consent = ModuleEU.getConsentForUserAndArea() // Implement user consent retrieval
                        if (consent) {
                            call.respond(HttpStatusCode.Forbidden, "User did not provide consent for localised politics requirements.")
                            finish()
                        } else {
                            // no interception, no log, no investigation
                        }
                    }

                    intercept(ApplicationCallPipeline.Call) {
                        val userId = 1 // Replace with the user's ID
                        val userRegion = "EU" // Replace with region detection logic

                        val consentType = consentTypes[userRegion]
                        if (consentType != null) {
                            if (!consentType.applyConsent(call, userId)) {
                                call.respond(HttpStatusCode.Forbidden, "User did not provide consent for $userRegion requirements.")
                                finish()
                            }
                        } else {
                            call.respond(HttpStatusCode.Forbidden, "No consent type found for $userRegion.")
                            finish()
                        }
                    }
                }
            }
        }
    }
}
