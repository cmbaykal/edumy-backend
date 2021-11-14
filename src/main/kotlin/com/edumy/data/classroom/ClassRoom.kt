package com.edumy.data.classroom

import com.edumy.data.user.User
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class ClassRoom(
    @BsonId
    val id: String = ObjectId().toString(),
    val lesson: String,
    val name: String,
    val users: MutableList<User>? = ArrayList()
)
