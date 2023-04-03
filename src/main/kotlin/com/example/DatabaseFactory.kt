package com.example

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object DatabaseFactory {
    fun init() {
        val driverClassName = "com.mysql.cj.jdbc.Driver"
        val username = "wishuser"
        val password = "wishpass123"
        val database = "bdclienttest"
        val host = "81.90.182.182"
        val jdbcUrl =
            "jdbc:mysql://$host:3306/$database?autoReconnect=true&useSSL=false&serverTimezone=UTC"
        Database.connect(jdbcUrl, driverClassName, user = username, password = password)

    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}