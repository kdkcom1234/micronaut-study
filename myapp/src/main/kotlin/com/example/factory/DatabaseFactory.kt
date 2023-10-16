
package com.example.factory

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import javax.sql.DataSource

@Factory
class DatabaseFactory (private var dataSource: DataSource) {

//    @Inject
//    private lateinit var dataSource: DataSource

    @Singleton
    fun databaseConfig() : DatabaseConfig {
        return DatabaseConfig { useNestedTransactions = true }
    }
    @Singleton
    fun database(): Database {
        return Database.connect(dataSource)
    }
}