package com.edumy

import com.edumy.data.user.User
import com.edumy.plugins.configureMonitoring
import com.edumy.plugins.configureRouting
import com.edumy.plugins.configureSecurity
import io.ktor.application.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {

    val client = KMongo.createClient().coroutine
    val database = client.getDatabase("users")
    val col = database.getCollection<User>()

    configureRouting(col)
    configureMonitoring()
    configureSecurity()
}
