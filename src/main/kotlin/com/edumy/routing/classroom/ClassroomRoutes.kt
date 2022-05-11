package com.edumy.routing.classroom

import com.edumy.base.ApiResponse
import com.edumy.data.classroom.Classroom
import com.edumy.data.classroom.ClassroomResult
import com.edumy.data.user.User
import com.edumy.data.user.UserEntity
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.aggregate

fun Application.classRoutes(database: CoroutineDatabase) {

    val users = database.getCollection<UserEntity>()
    val classrooms = database.getCollection<Classroom>()

    suspend fun updateClassAndUser(user: UserEntity, classroom: Classroom, remove: Boolean = false): Boolean {
        val userClasses: MutableList<String> = (user.classes ?: ArrayList()).also {
            if (remove) {
                it.remove(classroom.id)
            } else {
                it.add(classroom.id)
            }
        }
        val classUsers: MutableList<String> = (classroom.users).also {
            if (remove) {
                it.remove(user.id)
            } else {
                it.add(user.id)
            }
        }

        val userUpdate = users.updateOne(User::id eq user.id, setValue(User::classes, userClasses)).wasAcknowledged()
        val classUpdate = classrooms.updateOne(Classroom::id eq classroom.id, setValue(Classroom::users, classUsers)).wasAcknowledged()

        return userUpdate && classUpdate
    }

    routing {
        authenticate {
            post<AddClass> {
                try {
                    val classroom = call.receive<Classroom>()
                    val user = users.findOne(User::id eq classroom.creatorId)

                    if (user != null && classrooms.insertOne(classroom).wasAcknowledged() && updateClassAndUser(user, classroom)) {
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
                    val classroom = classrooms.findOne(Classroom::id eq request.classId)
                    val user = users.findOne(User::mail eq request.userMail)

                    if (classroom != null && user != null && user.role == "student") {
                        if (updateClassAndUser(user, classroom)) {
                            call.response.status(HttpStatusCode.OK)
                            call.respond(ApiResponse.ok())
                        } else {
                            call.response.status(HttpStatusCode.InternalServerError)
                            call.respond(ApiResponse.error())
                        }
                    } else {
                        call.response.status(HttpStatusCode.NotFound)
                        call.respond(ApiResponse.error())
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }
        }

        authenticate {
            post<LeaveClass> { request ->
                try {
                    val classroom = classrooms.findOne(Classroom::id eq request.classId)
                    val user = users.findOne(User::mail eq request.userMail)

                    if (classroom != null && user != null && updateClassAndUser(user, classroom, true)) {
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
            post<DeleteClass> { request ->
                try {
                    val deleteProcess = classrooms.deleteOne(Classroom::id eq request.classId)
                    val user = users.findOne(User::mail eq request.userMail)

                    if (user != null && deleteProcess.wasAcknowledged()) {
                        val userClasses: MutableList<String> = (user.classes ?: ArrayList()).also {
                            it.remove(request.classId)
                        }
                        users.updateOne(User::id eq user.id, setValue(User::classes, userClasses))

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
            post<UserClassrooms> { request ->
                try {
                    val foundClasses = classrooms.aggregate<Classroom>(
                        match(
                            Classroom::creatorId eq request.userId
                        ),
                        sort(
                            ascending(
                                Classroom::name
                            )
                        )
                    ).toList()

                    val result = mutableListOf<ClassroomResult>()

                    foundClasses.forEach {
                        val classUsers = users.aggregate<User>(
                            match(
                                User::classes contains it.id
                            ),
                            project(
                                exclude(
                                    User::classes
                                )
                            )
                        ).toList()
                        result.add(
                            ClassroomResult(
                                id = it.id,
                                lesson = it.lesson,
                                name = it.name,
                                creatorId = it.creatorId,
                                users = classUsers
                            )
                        )
                    }

                    call.response.status(HttpStatusCode.OK)
                    call.respond(ApiResponse.success(result))
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(ApiResponse.error(e.message))
                }
            }
        }

        authenticate {
            post<ClassInfo> { request ->
                try {
                    val classroom = classrooms.findOne(Classroom::id eq request.classId)
                    classroom?.let {
                        val classUsers = users.aggregate<User>(
                            match(
                                User::classes contains it.id
                            ),
                            project(
                                exclude(
                                    User::classes
                                )
                            )
                        ).toList()

                        val result = ClassroomResult(
                            id = it.id,
                            lesson = it.lesson,
                            name = it.name,
                            creatorId = it.creatorId,
                            users = classUsers
                        )
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ApiResponse.success(result))
                    } ?: run {
                        call.response.status(HttpStatusCode.NotFound)
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }
        }

        get<AllClasses> {
            try {
                val foundClasses = classrooms.find().toList()
                call.response.status(HttpStatusCode.OK)
                call.respond(ApiResponse.success(foundClasses))
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(ApiResponse.error(e.message))
            }
        }
    }
}