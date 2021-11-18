package com.edumy.data.answer

import com.edumy.util.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.*

@Serializable
data class Answer(
    @BsonId
    val id: String = ObjectId().toString(),
    @SerialName("questionId")
    var questionId: String? = null,
    @SerialName("userId")
    var userId: String? = null,
    @SerialName("text")
    var text: String? = null,
    @Serializable(with = DateSerializer::class)
    @SerialName("date")
    var date: Date? = null,
    @SerialName("image")
    var image: String? = null,
    @SerialName("video")
    var video: String? = null
)
