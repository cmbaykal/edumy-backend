package com.edumy.data.meeting

import com.edumy.data.classroom.Classroom
import com.edumy.data.user.User
import com.edumy.util.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.*

@Serializable
data class Meeting(
    @BsonId
    val id: String = ObjectId().toString(),
    @SerialName("classId")
    val classId: String,
    @SerialName("creatorId")
    val creatorId: String,
    @SerialName("description")
    val description: String,
    @SerialName("duration")
    val duration: Int,
    @Serializable(with = DateSerializer::class)
    @SerialName("date")
    val date: Date
)

@Serializable
data class MeetingResult(
    @SerialName("id")
    val id: String,
    @SerialName("user")
    val user: User?,
    @SerialName("classroom")
    val classroom: Classroom?,
    @SerialName("description")
    val description: String,
    @SerialName("duration")
    val duration: Int,
    @Serializable(with = DateSerializer::class)
    @SerialName("date")
    val date: Date
)
