package com.edumy

import com.edumy.base.*
import io.ktor.application.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val db = configureDatabase()
    configureSecurity()
    configureStatusPages()
    configureMonitoring()
    configureRouting(db)
}
