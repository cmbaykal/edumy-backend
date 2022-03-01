package com.edumy.routing

import com.edumy.base.ApiResponse
import com.edumy.data.classroom.*
import com.edumy.data.user.User
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
import org.litote.kmongo.setValue

fun Application.classRoutes(database: CoroutineDatabase) {

    val users = database.getCollection<User>()
    val classes = database.getCollection<Classroom>()

    routing {
        authenticate {
            post<AddClass> {
                try {
                    val classroom = call.receive<Classroom>()

                    if (classes.insertOne(classroom).wasAcknowledged()) {
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ApiResponse.ok())
                    } else {
                        call.response.status(HttpStatusCode.InternalServerError)
                        call.respond(ApiResponse.error())
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(ApiResponse.error(e.message))
                }
            }
        }

        authenticate {
            post<AssignUser> { request ->
                try {
                    val classroom = classes.findOne(Classroom::id eq request.classId)
                    val user = users.findOne(User::id eq request.userId)

                    if (classroom != null && user != null) {
                        val classUsers = classroom.users ?: ArrayList()
                        classUsers.add(user)

                        val updateState = classes.updateOne(Classroom::id eq request.classId, setValue(Classroom::users, classUsers)).wasAcknowledged()

                        if (updateState) {
                            call.response.status(HttpStatusCode.OK)
                            call.respond(ApiResponse.ok())
                        } else {
                            call.response.status(HttpStatusCode.InternalServerError)
                            call.respond(ApiResponse.error())
                        }
                    } else {
                        call.response.status(HttpStatusCode.NotFound)
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }
        }

        authenticate {
            post<LeaveClass> { request ->
                try {
                    val classroom = classes.findOne(Classroom::id eq request.classId)
                    val user = users.findOne(User::id eq request.userId)

                    if (classroom != null && user != null) {
                        val classUsers = classroom.users ?: ArrayList()
                        classUsers.remove(user)

                        val updateState = classes.updateOne(Classroom::id eq request.classId, setValue(Classroom::users, classUsers)).wasAcknowledged()

                        if (updateState) {
                            call.response.status(HttpStatusCode.OK)
                            call.respond(ApiResponse.ok())
                        } else {
                            call.response.status(HttpStatusCode.InternalServerError)
                            call.respond(ApiResponse.error())
                        }
                    } else {
                        call.response.status(HttpStatusCode.NotFound)
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }
        }

        authenticate {
            get<ClassInfo> { request ->
                try {
                    val classroom = classes.findOne(Classroom::id eq request.classId)

                    if (classroom != null) {
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ApiResponse.success(classroom))
                    } else {
                        call.response.status(HttpStatusCode.NotFound)
                    }

                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }
        }

        get<AllClasses> {
            try {
                val foundClasses = classes.find().toList()
                call.response.status(HttpStatusCode.OK)
                call.respond(ApiResponse.success(foundClasses))
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(ApiResponse.error(e.message))
            }
        }
    }
}