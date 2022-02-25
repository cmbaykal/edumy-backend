package com.edumy

import com.edumy.base.configureMonitoring
import com.edumy.base.configureRouting
import com.edumy.base.configureSecurity
import io.ktor.application.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureSecurity()
    configureMonitoring()
    configureRouting()
}
