package com.edumy.data.performance

import com.edumy.util.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.*

@Serializable
data class Performance(
    @BsonId
    val id: String = ObjectId().toString(),
    @SerialName("userId")
    val userId: String,
    @SerialName("lesson")
    val lesson: String,
    @SerialName("correctA")
    val correctA: Int,
    @SerialName("wrongA")
    val wrongA: Int,
    @Serializable(with = DateSerializer::class)
    @SerialName("date")
    val date: Date
)
