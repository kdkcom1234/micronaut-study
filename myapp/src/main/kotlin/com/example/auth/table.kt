package com.example.auth

import com.example.post.PostComments
import com.example.post.PostFiles
import com.example.post.Posts
import io.micronaut.context.annotation.Factory
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import jakarta.annotation.PostConstruct
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object Identities : LongIdTable("identity") {
    val secret = varchar("secret", 200)
    val username = varchar("username", length = 100)
}

object Profiles : LongIdTable("profile") {
    val email = varchar("email", 200)
    val nickname = varchar("nickname", 100)
    val identityId = reference("identity_id", Identities )
}

@Factory
class AuthTableSetup(private val database: Database) {
    @EventListener
    fun onStartup(e: StartupEvent) {
        // 백그라운드 작업 실행
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(Identities, Profiles)
        }
    }
}

