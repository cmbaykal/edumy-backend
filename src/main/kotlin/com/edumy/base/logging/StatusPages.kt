package com.edumy.base.logging


import com.edumy.base.ApiResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        status(HttpStatusCode.Unauthorized) { it ->
            call.response.status(HttpStatusCode.Unauthorized)
            call.respond(ApiResponse.error("Unauthorized User " + it.value))
        }
    }
}