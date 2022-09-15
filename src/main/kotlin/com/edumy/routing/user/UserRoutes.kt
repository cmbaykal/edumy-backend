package com.edumy.routing.user

import com.edumy.base.ApiResponse
import com.edumy.base.JWTConfig
import com.edumy.data.auth.AuthToken
import com.edumy.data.user.*
import com.toxicbakery.bcrypt.Bcrypt
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

fun Application.userRoutes(database: CoroutineDatabase) {

    val users = database.getCollection<UserEntity>()

    routing {
        get<UserAuth> { request ->
            try {
                val userId = request.userId
                val user = users.findOne(UserEntity::id eq userId)
                user?.let {
                    call.response.status(HttpStatusCode.OK)
                    val authToken = AuthToken(JWTConfig.generateToken(it), JWTConfig.expireTime)
                    call.respond(ApiResponse.success(authToken))
                } ?: run {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(ApiResponse.error("User not found"))
                }
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
            }
        }


        // TODO : Check if mail address is already used
        post<RegisterUser> {
            try {
                val requestBody = call.receive<UserEntity>()
                users.insertOne(requestBody).wasAcknowledged()
                call.response.status(HttpStatusCode.OK)
                call.respond(ApiResponse.ok())
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(ApiResponse.error(e.message))
            }
        }

        post<LoginUser> {
            try {
                val authUser = call.receive<UserCredentials>()
                val user = users.findOne(User::mail eq authUser.mail)

                user?.pass?.let {
                    if (Bcrypt.verify(authUser.pass, it.toByteArray())) {
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ApiResponse.success(user as User))
                    } else {
                        call.response.status(HttpStatusCode.NonAuthoritativeInformation)
                        call.respond(ApiResponse.error("Invalid User Credentials"))
                    }
                } ?: run {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(ApiResponse.error("User not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        authenticate {
            post<UpdateUser> {
                try {
                    val updateCredentials = call.receive<UpdateCredentials>()
                    val updateUser = User(
                        id = updateCredentials.userId,
                        name = updateCredentials.name,
                        bio = updateCredentials.bio
                    )
                    val updateResult = users.updateOneById(updateCredentials.userId, updateUser, updateOnlyNotNullProperties = true)

                    if (updateResult.wasAcknowledged()) {
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
            post<ChangePasswordUser> {
                try {
                    val passwordCredentials = call.receive<PasswordCredentials>()
                    val user = users.findOne(User::id eq passwordCredentials.userId)
                    user?.pass?.let {
                        if (Bcrypt.verify(passwordCredentials.oldPass, it.toByteArray())) {
                            user.pass = passwordCredentials.newPass
                            val updateResult = users.updateOneById(passwordCredentials.userId, user, updateOnlyNotNullProperties = true)

                            if (updateResult.wasAcknowledged()) {
                                call.response.status(HttpStatusCode.OK)
                                call.respond(ApiResponse.success(user as User))
                            } else {
                                call.response.status(HttpStatusCode.InternalServerError)
                                call.respond(ApiResponse.error())
                            }
                        } else {
                            call.response.status(HttpStatusCode.NonAuthoritativeInformation)
                            call.respond(ApiResponse.error("Invalid User Credentials"))
                        }
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }
        }

        post<DeleteUser> { request ->
            try {
                if (users.deleteOne(UserEntity::id eq request.userId).wasAcknowledged()) {
                    call.response.status(HttpStatusCode.OK)
                    call.respond(ApiResponse.ok())
                } else {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(ApiResponse.error())
                }
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
            }
        }

        authenticate {
            post<UserInfo> { request ->
                try {
                    val userEntity = users.findOne(UserEntity::id eq request.userId)

                    if (userEntity != null) {
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ApiResponse.success(userEntity as User))
                    } else {
                        call.response.status(HttpStatusCode.NotFound)
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }
        }

        get<AllUsers> {
            try {
                val foundUsers = users.find().toList() as List<User>
                call.response.status(HttpStatusCode.OK)
                call.respond(ApiResponse.success(foundUsers))
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
            }
        }
    }
}