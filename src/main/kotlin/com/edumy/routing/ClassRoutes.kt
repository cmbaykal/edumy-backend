package com.edumy.routing

import com.edumy.base.ApiResponse
import com.edumy.data.classroom.*
import com.edumy.data.user.User
import com.edumy.data.user.UserEntity
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.litote.kmongo.MongoOperator
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

fun Application.classRoutes(database: CoroutineDatabase) {

    val users = database.getCollection<UserEntity>()
    val classes = database.getCollection<Classroom>()

    suspend fun updateClassAndUser(user: UserEntity, classroom: Classroom, remove: Boolean = false): Boolean {
        val userClasses: MutableList<String> = (user.classes ?: ArrayList()).also {
            if (remove) {
                it.remove(classroom.id)
            } else {
                it.add(classroom.id)
            }
        }
        val classUser = ClassUser(
            id = user.id,
            role = user.role,
            name = user.name
        )
        val classUsers: MutableList<ClassUser> = (classroom.users ?: ArrayList()).also {
            if (remove) {
                it.remove(classUser)
            } else {
                it.add(classUser)
            }
        }

        val userUpdate = users.updateOne(User::id eq user.id, setValue(User::classes, userClasses)).wasAcknowledged()
        val classUpdate = classes.updateOne(Classroom::id eq classroom.id, setValue(Classroom::users, classUsers)).wasAcknowledged()

        return userUpdate && classUpdate
    }

    routing {
        authenticate {
            post<AddClass> {
                try {
                    val classroom = call.receive<Classroom>()
                    val user = users.findOne(User::id eq classroom.creatorId)

                    if (user != null && classes.insertOne(classroom).wasAcknowledged() && updateClassAndUser(user, classroom)) {
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
                    val classroom = classes.findOne(Classroom::id eq request.classId)
                    val user = users.findOne(User::id eq request.userId)

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
            post<UserClassrooms> { request ->
                try {
                    val user = users.findOne(User::id eq request.userId)
                    val foundClasses: MutableList<Classroom> = mutableListOf()
                    user?.classes?.let { classList ->
                        classList.forEach { classId ->
                            val classroom = classes.findOne(Classroom::id eq classId)
                            classroom?.let {
                                foundClasses.add(it)
                            }
                        }
                    }
                    call.response.status(HttpStatusCode.OK)
                    call.respond(ApiResponse.success(foundClasses))
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(ApiResponse.error(e.message))
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