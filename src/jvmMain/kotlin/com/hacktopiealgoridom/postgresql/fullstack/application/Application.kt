package com.hacktopiealgoridom.postgresql.fullstack.application

import com.hacktopiealgoridom.postgresql.fullstack.localisedpolitics.ExposedTenant
import com.hacktopiealgoridom.postgresql.fullstack.service.ExposedUser
import com.hacktopiealgoridom.postgresql.fullstack.localisedpolitics.TenantService
import com.hacktopiealgoridom.postgresql.fullstack.service.UserCredentials
import com.hacktopiealgoridom.postgresql.fullstack.service.UserService
import com.hacktopiealgoridom.postgresql.fullstack.service.UserPlugin
import com.hacktopiealgoridom.postgresql.fullstack.application.modules.ModuleCalifornia
import com.hacktopiealgoridom.postgresql.fullstack.application.modules.ModuleEU

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database

@Serializable
data class Tenant(val id: Int, val name: String)

@Serializable
data class User(val id: Int, val tenant: Int, val name: String, val password: String)


object Tenants : IntIdTable() {
    val name = varchar("name", 255)
}

object Users : IntIdTable() {
    val tenantId = reference("tenant_id", Tenants)
    val username = varchar("username", 50)
    val password = varchar("password", 100)
}


fun Application.module() {

    val database = Database.connect(
        "jdbc:postgresql://localhost:5432/db",
        driver = "org.postgresql.Driver",
        user = "user",
        password = "password"
    )

    val userService = UserService(database)
    val tenantService = TenantService(database)

    routing {

        get("/") {
            call.respondHtml(HttpStatusCode.OK, HTML::index)
        }

        static("/static") {
            resources()
        }

        // Create user
        post("/api/users") {
            val user = call.receive<User>()
            val id = userService.create(user)
            call.respond(HttpStatusCode.Created, id)
        }

        // Read user
        get("/api/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val user = userService.read(id)
            if (user != null) {
                call.respond(HttpStatusCode.OK, user)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Update user
        put("/api/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val user = call.receive<User>()
            userService.update(id, user)
            call.respond(HttpStatusCode.OK)
        }

        // Delete user
        delete("/api/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            userService.delete(id)
            call.respond(HttpStatusCode.OK)
        }

        post("/api/users/login") {
            val user = call.receive<UserCredentials>()
            val tenantUser = userService.getExposedUser(user.name)
            if (tenantUser != null) {
                val userInstance = userService.getUser(tenantUser)
                if (userInstance != null && user.password == userInstance.password) {

                    // todo login implementation

                    call.respond(HttpStatusCode.OK, "Login successful")
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Login failed")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Tenant not found")
            }
        }

        post("/api/tenants") {
            val tenant = call.receive<ExposedTenant>()
            val id = tenantService.create(tenant)
            call.respond(HttpStatusCode.Created, id)
        }

        get("/api/tenants") {
            val tenants = tenantService.getAll();
            call.respond(HttpStatusCode.OK, tenants)
        }

        post("/api/tenants/switch") {
            val tenantId = call.receive<Int>()
            // Implement tenant switching logic
            // todo userService.switchTenant(currentUser, tenantId)
            call.respond(HttpStatusCode.OK)
        }

        get("/api/tenants/current/users") {
            // Retrieve users for the current tenant
            val user: ExposedUser? = userService.getCurrentUser()
            if (user != null) {
                val users = userService.getTenants(user.tenant) // listOf(User(0, 0, "", "")) // listOf(User(Tenant(1, "Nájemce 1"), "uzivatel1", "heslo123"))
                call.respond(users)
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Login failed")
            }
        }
    }

    // Register localised politics requirement plugin
    install(UserPlugin) {
        registerModule("EU", ModuleEU)
        registerModule("cali", ModuleCalifornia)
        // Add modules for other regions as needed
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

//            post("/users/login") {
//                val userCredentials = call.receive<User>()
//                val tenant = transaction {
//                    Tenant.findById(userCredentials.tenant.id)
//                }
//                if (tenant != null) {
//                    val user = transaction {
//                        User.find { (Users.tenantId eq tenant.id) and (Users.username eq userCredentials.username) }.firstOrNull()
//                    }
//                    if (user != null && user.password == userCredentials.password) {
//                        call.respond(HttpStatusCode.OK, "Login successful")
//                    } else {
//                        call.respond(HttpStatusCode.Unauthorized, "Login failed")
//                    }
//                } else {
//                    call.respond(HttpStatusCode.BadRequest, "Tenant not found")
//                }
//            }


}

fun HTML.index() {

    /* val url = "jdbc:postgresql://localhost/main"
    UserTableManagement.connect(url, "user", "password")

    var dbs = UserTableManagement.listDatabases()
    var schemas = UserTableManagement.listSchemas()
    var tables = UserTableManagement.listTables()
    var users = UserTableManagement.listUsers()

    UserTableManagement.createDatabase("test")
    var tab = UserTableManagement.createTable("table", "id SERIAL PRIMARY KEY, name VARCHAR(20)")  //

    var schemas2 = UserTableManagement.listSchemas()

    UserTableManagement.registerUser("tester", "tester")

    UserTableManagement.setPermissions("tester", "ALL TABLES", "ALL PRIVILEGES")

    UserTableManagement.connect(url, "tester", "tester")

    var dbs2 = UserTableManagement.listDatabases()
    var schemas3 = UserTableManagement.listSchemas()
    var tables2 = UserTableManagement.listTables()
    var users2 = UserTableManagement.listUsers() */

    head {
        title("postgresql")
        //  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/blueprint-css/3.59.0/blueprint.css">
        //
        link {
            rel = "stylesheet"
            href = "https://cdnjs.cloudflare.com/ajax/libs/blueprint-css/3.59.0/blueprint.css"
        }
    }
    body {
        div {
            id = "root"
        }
        script(src = "/static/full-stack.js") {}
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1", module = Application::module).start(wait = true)
}
