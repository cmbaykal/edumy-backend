package com.edumy.data.study

import com.edumy.util.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.*

@Serializable
data class Study(
    @BsonId
    val id: String = ObjectId().toString(),
    @SerialName("userId")
    val userId: String,
    @SerialName("lesson")
    val lesson: String,
    @SerialName("correctA")
    val correctA: String,
    @SerialName("wrongA")
    val wrongA: String,
    @SerialName("emptyQ")
    val emptyQ: String,
    @Serializable(with = DateSerializer::class)
    @SerialName("date")
    val date: Date
)
