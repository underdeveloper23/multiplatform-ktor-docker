@file:Suppress("InvalidPackageDeclaration")

package com.hacktopiealgoridom.postgresql.fullstack.service

import com.hacktopiealgoridom.postgresql.fullstack.application.User
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedUser(val name: String, val tenant: Int)

@Serializable
data class UserCredentials(val name: String, val password: String)

class UserService(private val database: Database) {

    var currentUser: Int? = null

    object Users : Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", length = 50)
        val tenant = integer("tenant")
        val password = varchar("password", length = 32)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(user: User): Int = dbQuery {
        Users.insert {
            it[name] = user.name
            it[tenant] = user.tenant
            it[password] = user.password
        }[Users.id]
    }

    suspend fun read(id: Int): ExposedUser? {
        return dbQuery {
            Users.select { Users.id eq id }
                .map { ExposedUser(it[Users.name], it[Users.tenant]) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, user: User) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[name] = user.name
                it[tenant] = user.tenant
                it[password] = user.password
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }

    suspend fun getExposedUser(name: String): ExposedUser? {
        return dbQuery {
            Users.select { Users.name eq name }
                .map { ExposedUser(it[Users.name], it[Users.tenant]) }
                .singleOrNull()
        }
    }

    suspend fun getUser(tenantUser: ExposedUser): User? {
        return dbQuery {
            Users.select { (Users.name eq tenantUser.name) and (Users.tenant eq tenantUser.tenant) }
                .map { User(it[Users.id], it[Users.tenant], it[Users.name], it[Users.password]) }
                .singleOrNull()
        }
    }

    suspend fun getTenants(tenant: Int): List<ExposedUser> {
        return dbQuery {
            Users.select { (Users.tenant eq tenant) }
                .map { ExposedUser(it[Users.name], tenant) }
        }
    }

    fun setCurrentUser(userId: Int) {
        currentUser = userId
    }

    suspend fun getCurrentUser(): ExposedUser? {
        if (currentUser != null) {
            return read(currentUser!!)
        }
        return null
    }

}
