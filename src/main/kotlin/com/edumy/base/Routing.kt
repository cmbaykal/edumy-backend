package com.edumy.base

import com.edumy.data.answer.answerRoutes
import com.edumy.data.classroom.classRoutes
import com.edumy.data.performance.performanceRoutes
import com.edumy.data.question.questionRoutes
import com.edumy.data.usage.usageRoutes
import com.edumy.data.user.userRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import java.text.SimpleDateFormat
import java.util.*

fun Application.configureRouting() {
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
        convert<Date> { // this: DelegatingConversionService
            val format = SimpleDateFormat("dd.mm.yyyy HH.mm.ss")

            decode { values, _ -> // converter: (values: List<String>, type: Type) -> Any?
                values.singleOrNull()?.let { format.parse(it) }
            }

            encode { value -> // converter: (value: Any?) -> List<String>
                when (value) {
                    null -> listOf()
                    is Date -> listOf(SimpleDateFormat.getInstance().format(value))
                    else -> throw DataConversionException("Cannot convert $value as Date")
                }
            }
        }
    }

    val client = KMongo.createClient().coroutine
    val database = client.getDatabase("EdumyDB")

    baseRoutes()
    userRoutes(database)
    classRoutes(database)
    usageRoutes(database)
    questionRoutes(database)
    answerRoutes(database)
    performanceRoutes(database)
}
