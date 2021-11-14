package com.edumy.base

import com.edumy.data.answer.answerRoutes
import com.edumy.data.classroom.classRoutes
import com.edumy.data.performance.performanceRoutes
import com.edumy.data.question.questionRoutes
import com.edumy.data.usage.usageRoutes
import com.edumy.data.user.userRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.locations.*
import io.ktor.routing.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun Application.configureRouting() {
    install(Locations)
    install(Routing)
    install(ContentNegotiation) {
        gson()
    }

    val client = KMongo.createClient().coroutine
    val database = client.getDatabase("users")

    baseRoutes()
    userRoutes(database)
    classRoutes(database)
    usageRoutes(database)
    questionRoutes(database)
    answerRoutes(database)
    performanceRoutes(database)
}
