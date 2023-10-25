@file:Suppress("InvalidPackageDeclaration")

package com.hacktopiealgoridom.postgresql.fullstack.localisedpolitics

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction


data class ExposedTenant(val name: String)

class TenantService(database: Database) {

    object Tenant : Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", length = 50)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Tenant)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(tenant: ExposedTenant): Int = dbQuery {
        Tenant.insert {
            it[name] = tenant.name
        }[Tenant.id]
    }

    suspend fun read(id: Int): ExposedTenant? {
        return dbQuery {
            Tenant.select { Tenant.id eq id }
                .map { ExposedTenant(it[Tenant.name]) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, tenant: ExposedTenant) {
        dbQuery {
            Tenant.update({ Tenant.id eq id }) {
                it[name] = tenant.name
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Tenant.deleteWhere { Tenant.id.eq(id) }
        }
    }

    suspend fun getAll(): List<ExposedTenant> {
        return dbQuery {
            Tenant.selectAll()
                .map { ExposedTenant(it[Tenant.name]) }
        }
    }
}
