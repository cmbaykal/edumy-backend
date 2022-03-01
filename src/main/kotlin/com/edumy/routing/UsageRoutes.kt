package com.edumy.routing

import com.edumy.base.ApiResponse
import com.edumy.data.usage.*
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
import org.litote.kmongo.json


fun Application.usageRoutes(database: CoroutineDatabase) {

    val users = database.getCollection<User>()
    val usages = database.getCollection<UsageData>()

    routing {
        authenticate {
            post<AddUsage> {
                try {
                    val usageData = call.receive<UsageData>()
                    var existUsage = usages.findOne(UsageData::userId eq usageData.userId)

                    if (existUsage != null) {
                        val appUsages = existUsage.usages ?: ArrayList()
                        usageData.usages?.let {
                            appUsages.addAll(it)
                        }

                        existUsage = existUsage.copy(usages = appUsages)
                        val updateResult = usages.updateOne(
                            UsageData::userId eq usageData.userId,
                            existUsage,
                            updateOnlyNotNullProperties = true
                        )

                        if (updateResult.wasAcknowledged()) {
                            call.response.status(HttpStatusCode.OK)
                            call.respond(ApiResponse.ok())
                        } else {
                            call.response.status(HttpStatusCode.InternalServerError)
                            call.respond(ApiResponse.error())
                        }
                    } else {
                        if (usages.insertOne(usageData).wasAcknowledged()) {
                            call.response.status(HttpStatusCode.OK)
                            call.respond(ApiResponse.success(usageData))
                        } else {
                            call.response.status(HttpStatusCode.InternalServerError)
                            call.respond(ApiResponse.error())
                        }
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }
        }

        authenticate {
            get<UserUsages> { request ->
                try {
                    val user = users.findOneById(request.userId)

                    if (user != null) {
                        val usageData = usages.findOne(UsageData::userId eq request.userId)
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ApiResponse.success(usageData ?: UsageData(request.userId, ArrayList())))
                    } else {
                        call.response.status(HttpStatusCode.NotFound)
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }
        }

        post<DeleteUsages> { request ->
            try {
                if (usages.deleteOne(UsageData::userId eq request.userId).wasAcknowledged()) {
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

        post<DeleteAllUsages> {
            try {
                usages.drop()
                call.response.status(HttpStatusCode.OK)
                call.respond(ApiResponse.ok())
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(ApiResponse.error(e.message))
            }
        }

        get<AllUsages> {
            try {
                val foundUsages = usages.find().toList()
                call.response.status(HttpStatusCode.OK)
                call.respond(ApiResponse.success(foundUsages))
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(ApiResponse.error(e.message))
            }
        }
    }
}