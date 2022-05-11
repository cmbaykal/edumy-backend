package com.edumy.routing.answer

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("answer/add")
class AddAnswer

@Serializable
@Resource("answer/delete")
class DeleteAnswer(val answerId: String)

@Serializable
@Resource("answer/upvote")
class UpVoteAnswer(val answerId: String, val userId: String)

@Serializable
@Resource("answer/downvote")
class DownVoteAnswer(val answerId: String, val userId: String)

@Serializable
@Resource("answer/question")
class QuestionAnswers(val questionId: String)

@Serializable
@Resource("answer/user")
class UserAnswers(val userId: String)

@Serializable
@Resource("answer/classroom")
class ClassAnswers(val classId: String)

@Serializable
@Resource("answer/all")
class AllAnswers
