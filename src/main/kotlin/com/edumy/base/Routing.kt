package com.edumy.base

import com.edumy.base.routing.baseRoutes
import com.edumy.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.dataconversion.DataConversion
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.ktor.util.converters.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.text.SimpleDateFormat
import java.util.*

fun Application.configureRouting(database: CoroutineDatabase) {
    install(Resources)
    install(Routing)
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(DataConversion) {
        convert<Date> {
            val format = SimpleDateFormat("dd.mm.yyyy HH:mm:ss")

            decode { value ->
                value.singleOrNull().let { format.parse(it) }
            }

            encode { value ->
                try {
                    listOf(SimpleDateFormat.getInstance().format(value))
                } catch (e: Exception) {
                    throw DataConversionException("Cannot convert $value as Date")
                }
            }
        }
    }

    baseRoutes()
    userRoutes(database)
    classRoutes(database)
    questionRoutes(database)
    answerRoutes(database)
    studyRoutes(database)
    meetingRoutes(database)
}
