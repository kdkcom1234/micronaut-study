package com.example.post


import com.example.auth.Auth
import com.example.auth.AuthProfile
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import java.time.LocalDateTime
import java.util.*

@Controller("/posts")
open class PostController(private  val db : Database) {
    @Get(produces = [MediaType.APPLICATION_JSON])
    open fun fetch() = transaction(db = db,
        transactionIsolation = Connection.TRANSACTION_READ_UNCOMMITTED,
        readOnly = true ) {
        Posts.selectAll().map { r -> PostResponse(
            r[Posts.id], r[Posts.title], r[Posts.content],
            r[Posts.createdDate].toString())
        }
    }
    //    /paging/search?size=10&page=0
    //    /paging/search?size=10&page=0&keyword="제목"
    @Get("/paging/search")
    fun searchPaging( @QueryValue size : Int,@QueryValue page : Int, @QueryValue keyword : String?) : Page<PostResponse>
            = transaction(Connection.TRANSACTION_READ_UNCOMMITTED, readOnly = true) {
        // 검색 조건 생성
        val query = when {
            keyword != null -> Posts.select {
                (Posts.title like "%${keyword}%") or
                        (Posts.content like "%${keyword}%" ) }
            else -> Posts.selectAll()
        }

        // 전체 결과 카운트
        val totalCount = query.count()

        // 페이징 조회
        val content = query
            .orderBy(Posts.id to SortOrder.DESC)
            .limit(size, offset= (size * page).toLong())
            .map { r ->
                PostResponse(r[Posts.id],
                    r[Posts.title],
                    r[Posts.content], r[Posts.createdDate].toString())
            }

        // Page 객체로 리턴
        Page.of(content,
                Pageable.from(page, size, Sort.of(Sort.Order.desc("id"))),
            totalCount)
    }

    @Post
    fun create(@Body request : PostCreateRequest,
               @RequestAttribute authProfile: AuthProfile
    ) :
            HttpResponse<Map<String, Any?>> {
        println("${request.title}, ${request.content}")

        if(!request.validate()) {
            return HttpResponse.
                status(HttpStatus.BAD_REQUEST, "title and content fields are required")
        }

        val (result, response) = transaction {
            val result = Posts.insert {
                it[title] = request.title
                it[content] = request.content
                it[createdDate] = LocalDateTime.now()
                it[profileId] = authProfile.id
            }.resultedValues
                ?:
                return@transaction Pair(false, null)

            val record = result.first()

            return@transaction Pair(true, PostResponse(
                record[Posts.id],
                record[Posts.title],
                record[Posts.content],
                record[Posts.createdDate].toString(),
            ))
        }

        // 정확히 insert 됐을 때
        if(result) {
            return HttpResponse.created(mapOf("data" to response))
        }

        return HttpResponse.status(HttpStatus.CONFLICT, "conflict")
    }
}