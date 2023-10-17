package com.example.auth

import com.example.auth.util.JwtUtil

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.filter.HttpServerFilter
import io.micronaut.http.filter.ServerFilterChain
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
 // 모든 경로에 대해 필터 적용

open class AuthFilter : HttpServerFilter {

    override fun doFilter(request: HttpRequest<*>, chain: ServerFilterChain): Publisher<MutableHttpResponse<*>>? {
        val token: String? = request.headers.get("Authorization")

        if (token.isNullOrEmpty()) {
            return Mono.just(HttpResponse.status<HttpStatus>(HttpStatus.UNAUTHORIZED, "Unauthorized"))
        }

        val profile: AuthProfile = JwtUtil.validateToken(token.replace("Bearer ", ""))
            ?: return Mono.just(HttpResponse.status<HttpStatus>(HttpStatus.UNAUTHORIZED, "Invalid Token"))

        // 요청 속성(attribute)에 프로필 객체 추가하기
        request.attributes.put("authProfile", profile)

        return chain.proceed(request)
    }
}
