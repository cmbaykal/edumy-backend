package com.edumy.data.classroom

import com.edumy.data.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Classroom(
    @BsonId
    val id: String = ObjectId().toString(),
    @SerialName("lesson")
    val lesson: String,
    @SerialName("name")
    val name: String,
    @SerialName("creatorId")
    val creatorId: String,
    @SerialName("users")
    val users: MutableList<String> = ArrayList()
)

@Serializable
data class ClassroomResult(
    @BsonId
    val id: String,
    @SerialName("lesson")
    val lesson: String,
    @SerialName("name")
    val name: String,
    @SerialName("creatorId")
    val creatorId: String,
    @SerialName("users")
    val users: List<User>? = ArrayList()
)
