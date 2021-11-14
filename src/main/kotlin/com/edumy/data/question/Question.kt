package com.edumy.data.question

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Question(
    @BsonId
    val id: String = ObjectId().toString(),
    var classId: String? = null,
    var userId: String? = null,
    var lesson: String? = null,
    var question: String? = null,
    var image: String? = null
)
