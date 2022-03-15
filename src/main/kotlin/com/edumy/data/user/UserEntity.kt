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
    val classes: MutableList<String>? = null
)

@Serializable
data class UserEntity(
    @SerialName("pass")
    var pass: String?
) : User()

@Serializable
data class UserResult(
    val id: String,
    @SerialName("role")
    val role: String? = null,
    @SerialName("name")
    val name: String? = null
)

@Serializable
data class UserCredentials(
    @SerialName("mail")
    val mail: String,
    @SerialName("pass")
    val pass: String
)

@Serializable
data class UpdateCredentials(
    @SerialName("userId")
    val userId: String,
    @SerialName("name")
    val name: String? = null,
    @SerialName("bio")
    val bio: String? = null,
)

@Serializable
data class PasswordCredentials(
    @SerialName("userId")
    val userId: String,
    @SerialName("oldPass")
    val oldPass: String,
    @SerialName("newPass")
    val newPass: String,
)
