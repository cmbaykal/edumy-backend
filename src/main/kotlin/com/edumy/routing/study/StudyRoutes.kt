package com.edumy.routing.study

import com.edumy.base.ApiResponse
import com.edumy.data.classroom.Classroom
import com.edumy.data.study.Study
import com.edumy.data.user.User
import com.edumy.data.user.UserEntity
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import org.litote.kmongo.`in`
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.aggregate
import org.litote.kmongo.eq
import org.litote.kmongo.match

fun Application.studyRoutes(database: CoroutineDatabase) {

    val studies = database.getCollection<Study>()
    val users = database.getCollection<UserEntity>()
    val classrooms = database.getCollection<Classroom>()

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
            post<ClassStudies> { request ->
                try {
                    val classroom = classrooms.findOne(Classroom::id eq request.classId)
                    classroom?.let {
                        val foundStudies = studies.aggregate<Study>(
                            match(
                                Study::userId `in` it.users.toList(),
                                Study::lesson eq it.lesson
                            )
                        ).toList()
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ApiResponse.success(foundStudies.distinctBy { study -> study.id }))
                    } ?: run {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(ApiResponse.error(e.message))
                }
            }
        }

        authenticate {
            post<UserStudies> { request ->
                try {
                    val user = users.findOne(User::id eq request.userId)
                    val result = mutableListOf<Study>()
                    user?.let {
                        val foundStudies = studies.find(Study::userId eq request.userId).toList()
                        result.addAll(foundStudies)
                    }
                    call.response.status(HttpStatusCode.OK)
                    call.respond(ApiResponse.success(result.distinctBy { study -> study.id }))
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(ApiResponse.error(e.message))
                }
            }
        }

        authenticate {
            post<StudiesFeed> { request ->
                try {
                    val foundClassrooms = classrooms.find(Classroom::creatorId eq request.userId).toList()
                    val result = mutableListOf<Study>()
                    foundClassrooms.forEach { classroom ->
                        val foundStudies = studies.aggregate<Study>(
                            match(
                                Study::userId `in` classroom.users,
                            )
                        ).toList()
                        result.addAll(foundStudies)
                    }
                    call.response.status(HttpStatusCode.OK)
                    call.respond(ApiResponse.success(result.distinctBy { study -> study.id }))
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