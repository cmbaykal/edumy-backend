package com.edumy.plugins

import com.edumy.data.user.User
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.litote.kmongo.coroutine.CoroutineCollection

fun Application.configureRouting(col :CoroutineCollection<User>) {
    install(Locations)
    install(Routing)
    install(ContentNegotiation) {
        gson()
    }

    routing {

        get("/"){
            call.respond("Sa")
        }

        get("/users") {
            val users = col.find().toList()
            call.respond(users)
        }

        post("/user") {
            call.parameters
            val requestBody = call.receive<User>()
            val isSuccess = col.insertOne(requestBody).wasAcknowledged()
            call.respond(isSuccess)
        }

    }

}
