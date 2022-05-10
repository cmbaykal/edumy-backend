package com.edumy.routing

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("answer/add")
class AddAnswer

@Serializable
@Resource("answer/delete")
data class DeleteAnswer(val answerId: String)

@Serializable
@Resource("answer/upvote")
data class UpVoteAnswer(val answerId: String, val userId: String)

@Serializable
@Resource("answer/downvote")
data class DownVoteAnswer(val answerId: String, val userId: String)

@Serializable
@Resource("answer/question")
data class QuestionAnswers(val questionId: String)

@Serializable
@Resource("answer/user")
data class UserAnswers(val userId: String)

@Serializable
@Resource("answer/class")
data class ClassAnswers(val classId: String)

@Serializable
@Resource("answer/all")
class AllAnswers
