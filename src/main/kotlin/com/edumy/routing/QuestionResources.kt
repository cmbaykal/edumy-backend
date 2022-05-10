package com.edumy.routing

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("question/add")
class AddQuestion

@Serializable
@Resource("question/delete")
data class DeleteQuestion(val questionId: String)

@Serializable
@Resource("question/info")
data class QuestionInfo(val questionId: String)

@Serializable
@Resource("question/class")
data class ClassQuestions(val classId: String, val page: Int, val limit: Int)

@Serializable
@Resource("question/user")
data class UserQuestions(val userId: String, val page: Int, val limit: Int)

@Serializable
@Resource("question/feed")
data class QuestionsFeed(val page: Int, val limit: Int)

@Serializable
@Resource("question/all")
class AllQuestions

