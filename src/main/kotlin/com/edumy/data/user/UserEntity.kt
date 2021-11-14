package com.edumy.data.user

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.*

data class User(
    @BsonId
    val id: String = ObjectId().toString(),
    val role: String? = null,
    val mail: String? = null,
    val pass: String? = null,
    val birth: Date? = null,
    val name: String? = null,
    val bio: String? = null,
)

data class UserCredentials(
    val mail: String,
    val pass: String
)

