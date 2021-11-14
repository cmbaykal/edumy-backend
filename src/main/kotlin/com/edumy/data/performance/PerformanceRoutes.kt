package com.edumy.data.performance

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

fun Application.performanceRoutes(database: CoroutineDatabase) {

    val performances = database.getCollection<Performance>()

    routing {

        post<AddPerformance> {
            val performance = call.receive<Performance>()

            if (performances.insertOne(performance).wasAcknowledged()) {
                call.response.status(HttpStatusCode.OK)
                call.respond(performance)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post<DeletePerformance> { request ->
            if (performances.deleteOne(Performance::id eq request.performanceId).wasAcknowledged()) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        get<UserPerformances> { request ->
            call.respond(performances.find(Performance::userId eq request.userId).toList())
        }

        get<AllPerformances> {
            call.respond(performances.find().toList())
        }

    }

}