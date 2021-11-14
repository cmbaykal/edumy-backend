package com.edumy.data.classroom

import com.edumy.data.user.User
import io.ktor.application.*
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
    val classes = database.getCollection<ClassRoom>()

    routing {

        post<AddClass> {
            val classRoom = call.receive<ClassRoom>()

            if (classes.insertOne(classRoom).wasAcknowledged()) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post<AssignUser> { request ->
            val classRoom = classes.findOne(ClassRoom::id eq request.classId)
            val user = users.findOne(User::id eq request.userId)

            if (classRoom != null && user != null) {
                val classUsers = classRoom.users ?: ArrayList()
                classUsers.add(user)

                val updateState = classes.updateOne(
                    ClassRoom::id eq request.classId,
                    setValue(ClassRoom::users, classUsers)
                ).wasAcknowledged()

                if (updateState) {
                    call.response.status(HttpStatusCode.OK)
                    call.respond(classRoom)
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post<LeaveClass> { request ->
            val classRoom = classes.findOne(ClassRoom::id eq request.classId)
            val user = users.findOne(User::id eq request.userId)

            if (classRoom != null && user != null) {
                val classUsers = classRoom.users ?: ArrayList()
                classUsers.remove(user)

                val updateState = classes.updateOne(
                    ClassRoom::id eq request.classId,
                    setValue(ClassRoom::users, classUsers)
                ).wasAcknowledged()

                if (updateState) {
                    call.response.status(HttpStatusCode.OK)
                    call.respond(classRoom)
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        get<ClassInfo> { request ->
            val classRoom = classes.findOne(ClassRoom::id eq request.classId)

            if (classRoom != null) {
                call.response.status(HttpStatusCode.OK)
                call.respond(classRoom)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        get<AllClasses> {
            call.respond(classes.find().toList())
        }

    }

}