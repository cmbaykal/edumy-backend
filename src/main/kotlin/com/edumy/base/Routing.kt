package com.edumy.base

import com.edumy.routing.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.text.SimpleDateFormat
import java.util.*

fun Application.configureRouting(database: CoroutineDatabase) {
    install(Locations)
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

            decode { values, _ ->
                values.singleOrNull()?.let { format.parse(it) }
            }

            encode { value -> //
                when (value) {
                    null -> listOf()
                    is Date -> listOf(SimpleDateFormat.getInstance().format(value))
                    else -> throw DataConversionException("Cannot convert $value as Date")
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
