package com.edumy.routing.question

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("question/add")
class AddQuestion

@Serializable
@Resource("question/delete")
class DeleteQuestion(val questionId: String)

@Serializable
@Resource("question/info")
class QuestionInfo(val questionId: String)

@Serializable
@Resource("question/classroom")
class ClassQuestions(val classId: String, val page: Int, val limit: Int)

@Serializable
@Resource("question/user")
class UserQuestions(val userId: String, val page: Int, val limit: Int)

@Serializable
@Resource("question/feed")
class QuestionsFeed(val page: Int, val limit: Int)

@Serializable
@Resource("question/all")
class AllQuestions

