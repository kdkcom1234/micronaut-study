package com.example.post


import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Controller("/posts")
class PostController( private  val db : Database) {
    // exposed selectAll -> List<ResultRow>
    // ResultRow는 transaction {} 구문 밖에서 접근 불가능함
    // transaction 구분 외부로 보낼 때는 별도의 객체로 변환해서 내보낸다.
    // 결과값: List<PostResponse>


    @Get
    fun fetch() = transaction(db) {
        Posts.selectAll().map { r -> PostResponse(
            r[Posts.id], r[Posts.title], r[Posts.content],
            r[Posts.createdDate].toString())
        }
    }
}