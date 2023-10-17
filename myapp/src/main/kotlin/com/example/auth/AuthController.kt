package com.example.auth

import com.example.auth.util.JwtUtil
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.cookie.Cookie
import java.net.URI
import java.util.*

@Controller("/auth")
class AuthController(private val service: AuthService) {
    @Post("/signup")
    fun signUp(@Body req: SignupRequest): HttpResponse<Long> {
        println(req)

        // 1. Validation
        // 입력값 검증
        // 패스워드없거나, 닉네임, 이메일 없음...
        // 필수값은 SingupRequest에서 자동으로 검증

        // 2. Buisness Logic(데이터 처리)
        // profile, login 생성 트랜잭션 처리
        val profileId = service.createIdentity(req)

        if(profileId > 0) {
            // 3. Response
            // 201: created
            return HttpResponse.created(profileId)
        } else {
            return HttpResponse.status(HttpStatus.CONFLICT);
        }
    }

    //1. (브라우저) 로그인 요청
    // [RequestLine]
    //   HTTP 1.1 POST 로그인주소
    // [RequestHeader]
    //   content-type: www-form-urlencoded
    // [Body]
    //   id=...&pw=...
    //2. (서버) 로그인 요청을 받고 인증처리 후 쿠키 응답 및 웹페이지로 이동
    // HTTP Status 302 (리다이렉트)
    // [Response Header]
    //   Set-Cookie: 인증키=키........; domain=.naver.com
    //   Location: "리다이렉트 주소"
    //3. (브라우저) 쿠키를 생성(도메인에 맞게)
    @Post("/signin")
    fun signIn(
        @Body req : SigninRequest,
    ): HttpResponse<Void> {
        val (username, password) = req;
        println(username)
        println(password)

        val (result, message) = service.authenticate(username, password)

        if(result) {
            // 3. cookie와 헤더를 생성한후 리다이렉트
            val cookie = Cookie.of("token", message)
                                .path("/")
                                .maxAge(JwtUtil.TOKEN_TIMEOUT / 1000L)
                                .domain("localhost");

            // 응답헤더에 쿠키 추가
            return HttpResponse
                .seeOther<Void>( URI.create("http://localhost:5500"))
                .cookie(cookie)
        }

        return HttpResponse
            .seeOther( URI.create("http://localhost:5500/login.html?err=$message"))

    }
}
