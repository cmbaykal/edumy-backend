package com.edumy.routing

import com.edumy.base.ApiResponse
import com.edumy.data.study.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

fun Application.studyRoutes(database: CoroutineDatabase) {

    val studies = database.getCollection<Study>()

    routing {
        authenticate {
            post<AddStudy> {
                try {
                    val study = call.receive<Study>()

                    if (studies.insertOne(study).wasAcknowledged()) {
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ApiResponse.ok())
                    } else {
                        call.response.status(HttpStatusCode.InternalServerError)
                        call.respond(ApiResponse.error())
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }
        }

        authenticate {
            post<DeleteStudy> { request ->
                try {
                    if (studies.deleteOne(Study::id eq request.studyId).wasAcknowledged()) {
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ApiResponse.ok())
                    } else {
                        call.response.status(HttpStatusCode.InternalServerError)
                        call.respond(ApiResponse.error())
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }
        }

        authenticate {
            post<UserStudies> { request ->
                try {
                    val foundStudies = studies.find(Study::userId eq request.userId).toList()
                    call.response.status(HttpStatusCode.OK)
                    call.respond(ApiResponse.success(foundStudies))
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(ApiResponse.error(e.message))
                }
            }
        }

        get<AllStudies> {
            try {
                val foundPerformances = studies.find().toList()
                call.response.status(HttpStatusCode.OK)
                call.respond(ApiResponse.success(foundPerformances))
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(ApiResponse.error(e.message))
            }
        }
    }
}