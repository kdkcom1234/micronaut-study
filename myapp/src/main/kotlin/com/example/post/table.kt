package com.example.post

import com.example.auth.Profiles
import io.micronaut.context.annotation.Factory
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

// exposed kotlin ORM(DSL, domain-specific language)
// exposed 테이블 객체명은 파스칼케이스에 복수로 쓰고 있음.
object Posts : Table("post") {
    // val 필드명 = 컬럼타입("컬럼명")
    val id = long("id").autoIncrement()
    val title = varchar("title", 100)
    //    val content = text("content").nullable() // 널 가능
    val content = text("content")
    val createdDate  = datetime("created_date")
    // primary key 설정(제약조건)
    override val primaryKey = PrimaryKey(id, name = "pk_post_id")
    val profileId = reference("profile_id", Profiles);
}
// id가 long(mysql bigint)이고 primary key, auto_increment 필드/컬럼 생성
object PostComments : LongIdTable("post_comment") {
    // foreign key를 넣으면 됨
    // val 필드명 = reference("컬럼명", 참조필드)
    val postId = reference("post_id", Posts.id)
    val comment = text("comment")
    val profileId = reference("profile_id", Profiles);
}

// 파일의 메타정보
object PostFiles : LongIdTable("post_file") {
    val postId = reference("post_id", Posts.id)
    val originalFileName = varchar("original_file_name", 200)
    val uuidFileName = varchar("uuid", 50).uniqueIndex()
    val contentType = varchar("content_type", 100)
}

@Factory
class PostTableSetup(private val database: Database) {
    @EventListener
    fun onStartup(e: StartupEvent) {
        // 백그라운드 작업 실행
        transaction(database) {
//            addLogger(StdOutSqlLogger)
            SchemaUtils.createMissingTablesAndColumns(Posts, PostComments, PostFiles)
        }
    }
}
