package com.edumy.data.question

import io.ktor.locations.*

@Location("question/add")
class AddQuestion

@Location("question/delete")
data class DeleteQuestion(val questionId: String)

@Location("question/info")
data class QuestionInfo(val questionId: String)

@Location("question/class")
data class ClassQuestions(val classId: String)

@Location("question/user")
data class UserQuestions(val userId: String)

@Location("question/all")
class AllQuestions

