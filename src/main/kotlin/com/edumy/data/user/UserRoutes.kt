package com.edumy.data.user

import com.edumy.base.BaseResponse
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.json

fun Application.userRoutes(database: CoroutineDatabase) {

    val users = database.getCollection<User>()

    routing {
        post<RegisterUser> {
            val requestBody = call.receive<User>()

            if (users.insertOne(requestBody).wasAcknowledged()) {
                call.response.status(HttpStatusCode.OK)
                call.respond(BaseResponse.success(requestBody))
            } else {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(BaseResponse.error())
            }
        }

        post<LoginUser> {
            val authUser = call.receive<UserCredentials>()
            val user = users.findOne(User::mail eq authUser.mail)

            if (user != null && user.pass == authUser.pass) {
                call.response.status(HttpStatusCode.OK)
                call.respond(BaseResponse.success(user))
            } else {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(BaseResponse.error())
            }
        }

        post<UpdateUser> { request ->
            val updateUser = call.receive<User>().copy(id = request.userId)
            val updateResult = users.updateOneById(request.userId, updateUser, updateOnlyNotNullProperties = true)

            if (updateResult.wasAcknowledged()) {
                call.response.status(HttpStatusCode.OK)
                call.respond(BaseResponse.success(updateResult.json))
            } else {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(BaseResponse.error())
            }
        }

        post<DeleteUser> { request ->
            if (users.deleteOne(User::id eq request.userId).wasAcknowledged()) {
                call.response.status(HttpStatusCode.OK)
                call.respond(BaseResponse.ok())
            } else {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(BaseResponse.error())
            }
        }

        get<UserInfo> { request ->
            val user = users.findOne(User::id eq request.userId)

            if (user != null) {
                call.response.status(HttpStatusCode.OK)
                call.respond(BaseResponse.success(user))
            } else {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(BaseResponse.error())
            }
        }

        get<AllUsers> {
            try {
                val foundUsers = users.find().toList()
                call.response.status(HttpStatusCode.OK)
                call.respond(foundUsers)
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(BaseResponse.error(e.message))
            }
        }
    }
}