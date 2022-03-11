package com.edumy.data.user

import com.edumy.util.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.*

@Serializable
open class User(
    @BsonId
    val id: String = ObjectId().toString(),
    @SerialName("role")
    val role: String? = null,
    @SerialName("mail")
    val mail: String? = null,
    @Serializable(with = DateSerializer::class)
    @SerialName("birth")
    val birth: Date? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("bio")
    val bio: String? = null,
    @SerialName("classes")
    val classes: MutableList<String>? = ArrayList()
)

@Serializable
data class UserEntity(
    @SerialName("pass")
    var pass: String?
) : User()

@Serializable
data class UserCredentials(
    @SerialName("mail")
    val mail: String,
    @SerialName("pass")
    val pass: String
)

