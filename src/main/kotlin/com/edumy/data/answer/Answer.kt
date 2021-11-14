package com.edumy.data.answer

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Answer(
    @BsonId
    val id: String = ObjectId().toString(),
    var questionId: String? = null,
    var userId: String? = null,
    var text: String? = null,
    var image: String? = null,
    var video: String? = null
)
