package com.edumy.base

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

fun Application.baseRoutes() {

    routing {

        get {
            call.respond("Hello Baykal")
        }

        get<DownloadImage> { request ->
            val file = File("uploads/image/${request.fileName}")
            if (file.exists()) {
                call.response.status(HttpStatusCode.OK)
                call.respondFile(file)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get<DownloadVideo> { request ->
            val file = File("uploads/video/${request.fileName}")
            if (file.exists()) {
                call.response.status(HttpStatusCode.OK)
                call.respondFile(file)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

    }

}
