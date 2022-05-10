package com.edumy

import com.edumy.base.configureDatabase
import com.edumy.base.configureRouting
import com.edumy.base.configureSecurity
import com.edumy.base.logging.configureMonitoring
import com.edumy.base.logging.configureStatusPages
import io.ktor.server.application.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureSecurity()
    configureStatusPages()
    configureMonitoring()
    val db = configureDatabase()
    configureRouting(db)
}
