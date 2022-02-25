package com.edumy.data.performance

import com.edumy.base.BaseResponse
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
            try {
                val performance = call.receive<Performance>()

                if (performances.insertOne(performance).wasAcknowledged()) {
                    call.response.status(HttpStatusCode.OK)
                    call.respond(BaseResponse.success(performance))
                } else {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(BaseResponse.error())
                }
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(BaseResponse.error(e.message))
            }
        }

        post<DeletePerformance> { request ->
            try {
                if (performances.deleteOne(Performance::id eq request.performanceId).wasAcknowledged()) {
                    call.response.status(HttpStatusCode.OK)
                    call.respond(BaseResponse.ok())
                } else {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(BaseResponse.error())
                }
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(BaseResponse.error(e.message))
            }
        }

        get<UserPerformances> { request ->
            try {
                val foundPerformances = performances.find(Performance::userId eq request.userId).toList()
                call.response.status(HttpStatusCode.OK)
                call.respond(BaseResponse.success(foundPerformances))
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(BaseResponse.error(e.message))
            }
        }

        get<AllPerformances> {
            try {
                val foundPerformances = performances.find().toList()
                call.response.status(HttpStatusCode.OK)
                call.respond(BaseResponse.success(foundPerformances))
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(BaseResponse.error(e.message))
            }
        }
    }
}