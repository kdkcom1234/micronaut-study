
package com.example.factory

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import javax.sql.DataSource





@Factory
class DatabaseFactory (private var dataSource: DataSource) {
//class DatabaseFactory () {

//    @Inject
//    private lateinit var dataSource: DataSource

//    @Singleton
//    fun databaseConfig() : DatabaseConfig {
//        return DatabaseConfig { useNestedTransactions = true }
//    }
    @Singleton
    fun database(): Database {
// datasources.default.url=jdbc:mysql://localhost:3306/myapp2?rewriteBatchedStatements=true
// datasources.default.username: root
// datasources.default.password: password1234!


//        return Database.connect(
//            "jdbc:mysql://localhost:3306/myapp2?rewriteBatchedStatements=true",
//            driver = "com.mysql.cj.jdbc.Driver",
//            user = "root", password = "password1234!")

        return Database.connect(dataSource)
    }
}