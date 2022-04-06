package com.edumy.data.answer

import com.edumy.data.user.User
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
    @SerialName("description")
    var description: String? = null,
    @Serializable(with = DateSerializer::class)
    @SerialName("date")
    var date: Date? = null,
    @SerialName("image")
    var image: String? = null,
    @SerialName("video")
    var video: String? = null,
    @SerialName("upVote")
    var upVote: MutableList<String> = mutableListOf(),
    @SerialName("downVote")
    var downVote: MutableList<String> = mutableListOf()
)


@Serializable
data class AnswerInformation(
    @SerialName("id")
    val id: String,
    @SerialName("questionId")
    val questionId: String,
    @SerialName("user")
    var user: User? = null,
    @SerialName("description")
    var description: String? = null,
    @Serializable(with = DateSerializer::class)
    @SerialName("date")
    var date: Date? = null,
    @SerialName("image")
    var image: String? = null,
    @SerialName("video")
    var video: String? = null,
    @SerialName("upVote")
    var upVote: MutableList<String>? = null,
    @SerialName("downVote")
    var downVote: MutableList<String>? = null
)