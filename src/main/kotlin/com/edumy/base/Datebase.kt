package com.edumy.base

import io.ktor.application.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun Application.configureDatabase(): CoroutineDatabase {
    val client = KMongo.createClient().coroutine
    return client.getDatabase("EdumyDB")
}