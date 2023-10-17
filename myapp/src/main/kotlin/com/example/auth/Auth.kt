package com.example.auth

import io.micronaut.aop.Around

@Around
@Target(AnnotationTarget.FUNCTION)
annotation class Auth
