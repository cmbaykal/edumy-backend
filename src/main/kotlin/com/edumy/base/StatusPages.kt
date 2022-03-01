package com.edumy.base

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        status(HttpStatusCode.Unauthorized) { it ->
            call.response.status(HttpStatusCode.Unauthorized)
            call.respond(ApiResponse.error("Unauthorized User" + it.value))
        }
    }
}