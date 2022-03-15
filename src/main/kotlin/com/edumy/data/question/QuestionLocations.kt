package com.edumy.data.question

import io.ktor.locations.*

@Location("question/add")
class AddQuestion

@Location("question/delete")
data class DeleteQuestion(val questionId: String)

@Location("question/info")
data class QuestionInfo(val questionId: String)

@Location("question/class")
data class ClassQuestions(val classId: String, val page: Int, val limit: Int)

@Location("question/user")
data class UserQuestions(val userId: String, val page: Int, val limit: Int)

@Location("question/feed")
data class QuestionsFeed(val page: Int, val limit: Int)

@Location("question/all")
class AllQuestions

