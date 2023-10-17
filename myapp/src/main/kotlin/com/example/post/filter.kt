package com.example.post

import com.example.auth.AuthFilter
import io.micronaut.http.HttpMethod
import io.micronaut.http.annotation.Filter

@Filter("/posts/**", methods = [HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE])
class PostCommandFilter : AuthFilter()

@Filter("/posts/*", methods = [HttpMethod.GET])
class PostGetOneFilter : AuthFilter()