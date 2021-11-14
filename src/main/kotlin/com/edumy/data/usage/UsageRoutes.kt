package com.edumy.data.usage

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


fun Application.usageRoutes(database: CoroutineDatabase) {

    val users = database.getCollection<User>()
    val usages = database.getCollection<UsageData>()

    routing {

        post<AddUsage> {
            val usageData = call.receive<UsageData>()
            var existUsage = usages.findOne(UsageData::userId eq usageData.userId)

            if (existUsage != null) {
                val appUsages = existUsage.usages ?: ArrayList()
                appUsages.addAll(usageData.usages!!)
                existUsage = existUsage.copy(usages = appUsages)
                val updateResult = usages.updateOne(
                    UsageData::userId eq usageData.userId,
                    existUsage,
                    updateOnlyNotNullProperties = true
                )

                if (updateResult.wasAcknowledged()) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } else {
                if (usages.insertOne(usageData).wasAcknowledged()) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }

        get<UserUsages> { request ->
            val user = users.findOneById(request.userId)

            if (user != null) {
                call.response.status(HttpStatusCode.OK)
                val usageData = usages.findOne(UsageData::userId eq request.userId)
                if (usageData != null) {
                    call.respond(usageData)
                } else {
                    call.respond(UsageData(request.userId, ArrayList()))
                }
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post<DeleteUsages> { request ->
            if (usages.deleteOne(UsageData::userId eq request.userId).wasAcknowledged()) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post<DeleteAllUsages> {
            usages.drop()
            call.respond(HttpStatusCode.OK)
        }

        get<AllUsages> {
            call.respond(usages.find().toList())
        }

    }

}