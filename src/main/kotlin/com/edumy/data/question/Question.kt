package com.edumy.data.question

import com.edumy.util.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.*

@Serializable
data class Question(
    @BsonId
    val id: String = ObjectId().toString(),
    @SerialName("userId")
    var userId: String? = null,
    @SerialName("lesson")
    var lesson: String? = null,
    @SerialName("description")
    var description: String? = null,
    @Serializable(with = DateSerializer::class)
    @SerialName("date")
    var date: Date? = null,
    @SerialName("image")
    var image: String? = null
)
