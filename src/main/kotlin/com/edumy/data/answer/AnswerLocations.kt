package com.edumy.data.answer

import io.ktor.locations.*

@Location("answer/add")
class AddAnswer

@Location("answer/delete")
data class DeleteAnswer(val answerId: String)

@Location("answer/upvote")
data class UpVoteAnswer(val answerId: String, val userId: String)

@Location("answer/downvote")
data class DownVoteAnswer(val answerId: String, val userId: String)

@Location("answer/question")
data class QuestionAnswers(val questionId: String)

@Location("answer/user")
data class UserAnswers(val userId: String)

@Location("answer/class")
data class ClassAnswers(val classId: String)

@Location("answer/all")
class AllAnswers
