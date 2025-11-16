package com.example.model

import io.ktor.server.application.Application
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseSingleton {
    fun init(application: Application) {
        val conf = application.environment.config

        fun lookup(vararg paths: String) =
            paths.mapNotNull { conf.propertyOrNull(it)?.getString() }.firstOrNull()

        val url = lookup("ktor.datasource.url", "ktor.application.datasource.url")
            ?: "jdbc:postgresql://localhost:5432/quotes"
        val driver = lookup("ktor.datasource.driverClassName", "ktor.application.datasource.driverClassName")
            ?: "org.postgresql.Driver"
        val user = lookup("ktor.datasource.username", "ktor.application.datasource.username") ?: "example"
        val password = lookup("ktor.datasource.password", "ktor.application.datasource.password") ?: "example"

        Database.connect(
            url = url,
            driver = driver,
            user = user,
            password = password
        )
    }
    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}
