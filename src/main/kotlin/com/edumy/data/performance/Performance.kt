package com.edumy.data.performance

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.*

data class Performance(
    @BsonId
    val id: String = ObjectId().toString(),
    val userId: String,
    val lesson: String,
    val correctA: Int,
    val wrongA: Int,
    val date: Date
)
