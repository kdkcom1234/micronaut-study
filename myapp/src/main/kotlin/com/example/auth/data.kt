package com.example.auth


data class SignupRequest (
     val username: String,
     val password: String,
     val nickname: String,
     val email: String,
)

data class SigninRequest (
     val username: String,
     val password: String,
)

data class AuthProfile (
     val id: Long = 0, // 프로필 id
     val nickname: String, // 프로필 별칭
     val username: String, // 로그인 사용자이름
)
