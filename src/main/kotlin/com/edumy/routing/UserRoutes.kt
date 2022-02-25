package com.edumy.routing

import at.favre.lib.crypto.bcrypt.BCrypt
import com.edumy.base.BaseResponse
import com.edumy.base.JWTConfig
import com.edumy.data.auth.AuthToken
import com.edumy.data.user.*
import com.toxicbakery.bcrypt.Bcrypt
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
import org.litote.kmongo.json

fun Application.userRoutes(database: CoroutineDatabase) {

    val users = database.getCollection<UserEntity>()

    routing {
        get<UserAuth> { request ->
            try {
                val user = users.findOne(UserEntity::id eq request.userId)
                user?.let {
                    call.response.status(HttpStatusCode.OK)
                    val authToken = AuthToken(JWTConfig.generateToken(it), JWTConfig.expireTime)
                    call.respond(BaseResponse.success(authToken))
                } ?: run {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(BaseResponse.error())
                }
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(BaseResponse.error(e.message))
            }
        }

        post<RegisterUser> {
            try {
                val requestBody = call.receive<UserEntity>()
                users.insertOne(requestBody).wasAcknowledged()
                call.response.status(HttpStatusCode.OK)
                call.respond(BaseResponse.ok())
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(BaseResponse.error(e.message))
            }
        }

        post<LoginUser> {
            try {
                val authUser = call.receive<UserCredentials>()
                val userEntity = users.findOne(User::mail eq authUser.mail)

                if (userEntity != null && Bcrypt.verify(authUser.pass, userEntity.pass.toByteArray())) {
                    call.response.status(HttpStatusCode.OK)
                    call.respond(BaseResponse.ok())
                } else {
                    call.response.status(HttpStatusCode.NonAuthoritativeInformation)
                    call.respond(BaseResponse.error())
                }
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(BaseResponse.error(e.message))
            }
        }

        authenticate {
            post<UpdateUser> { request ->
                try {
                    val updateUser = call.receive<User>()
                    val updateResult = users.updateOneById(request.userId, updateUser, updateOnlyNotNullProperties = true)

                    if (updateResult.wasAcknowledged()) {
                        call.response.status(HttpStatusCode.OK)
                        call.respond(BaseResponse.success(updateResult.json))
                    } else {
                        call.response.status(HttpStatusCode.InternalServerError)
                        call.respond(BaseResponse.error())
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(BaseResponse.error(e.message))
                }
            }
        }

        post<DeleteUser> { request ->
            try {
                if (users.deleteOne(UserEntity::id eq request.userId).wasAcknowledged()) {
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

        authenticate {
            get<UserInfo> { request ->
                try {
                    val userEntity = users.findOne(UserEntity::id eq request.userId)

                    if (userEntity != null) {
                        call.response.status(HttpStatusCode.OK)
                        call.respond(BaseResponse.success(userEntity as User))
                    } else {
                        call.response.status(HttpStatusCode.NotFound)
                        call.respond(BaseResponse.error())
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(BaseResponse.error())
                }
            }
        }

        get<AllUsers> {
            try {
                val foundUsers = users.find().toList() as List<User>
                call.response.status(HttpStatusCode.OK)
                call.respond(foundUsers)
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(BaseResponse.error(e.message))
            }
        }
    }
}