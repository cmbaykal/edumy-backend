package com.edumy.base

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        status(HttpStatusCode.Unauthorized) {
            call.response.status(HttpStatusCode.Unauthorized)
            call.respond(BaseResponse.error("Unauthorized User"))
        }
    }
}