package com.edumy.routing.answer

import com.edumy.base.ApiResponse
import com.edumy.data.answer.Answer
import com.edumy.data.answer.AnswerInformation
import com.edumy.data.classroom.Classroom
import com.edumy.data.question.Question
import com.edumy.data.user.User
import com.edumy.data.user.UserEntity
import com.edumy.util.DateSerializer
import com.edumy.util.FileType
import com.edumy.util.FileType.Companion.fileType
import com.edumy.util.FileType.Companion.path
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.aggregate
import java.io.File
import java.util.*

fun Application.answerRoutes(database: CoroutineDatabase) {

    val users = database.getCollection<UserEntity>()
    val classes = database.getCollection<Classroom>()
    val questions = database.getCollection<Question>()
    val answers = database.getCollection<Answer>()

    suspend fun getAnswerInfo(foundAnswers: List<Answer>): MutableList<AnswerInformation> {
        val result = mutableListOf<AnswerInformation>()

        foundAnswers.forEach { answer ->
            val user = users.aggregate<User>(
                match(User::id eq answer.userId),
                project(
                    exclude(
                        User::role,
                        User::mail,
                        User::birth,
                        User::classes
                    )
                )
            ).first()
            val answerInformation = answers.aggregate<AnswerInformation>(
                match(
                    Answer::id eq answer.id
                ),
                project(
                    AnswerInformation::id from Answer::id,
                    AnswerInformation::questionId from Answer::questionId,
                    AnswerInformation::user from user,
                    AnswerInformation::description from Answer::description,
                    AnswerInformation::date from Answer::date,
                    AnswerInformation::image from Answer::image,
                    AnswerInformation::video from Answer::video,
                    AnswerInformation::upVote from Answer::upVote,
                    AnswerInformation::downVote from Answer::downVote,
                )
            ).first()
            answerInformation?.let {
                result.add(it)
            }
        }

        return result
    }

    routing {
        authenticate {
            post<AddAnswer> {
                try {
                    val multipartData = call.receiveMultipart()
                    val answer = Answer()

                    multipartData.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                when (part.name) {
                                    "questionId" -> answer.questionId = part.value
                                    "userId" -> answer.userId = part.value
                                    "description" -> answer.description = part.value
                                    "date" -> answer.date = DateSerializer.parse(part.value)
                                }
                            }
                            is PartData.FileItem -> {
                                val type = part.contentType.toString().fileType
                                val extension = (part.originalFileName as String).substringAfterLast(".")
                                val fileName = UUID.randomUUID().toString() + "." + extension
                                val fileBytes = part.streamProvider().readBytes()
                                File("uploads/${type.path}/$fileName").writeBytes(fileBytes)

                                when (type) {
                                    FileType.VideoMP4 -> {
                                        answer.video = fileName
                                    }
                                    else -> {
                                        answer.image = fileName
                                    }
                                }
                            }
                            is PartData.BinaryItem -> TODO()
                        }
                    }

                    if (answers.insertOne(answer).wasAcknowledged()) {
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ApiResponse.ok())
                    } else {
                        call.response.status(HttpStatusCode.InternalServerError)
                        call.respond(ApiResponse.error())
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }
        }

        authenticate {
            post<DeleteAnswer> { request ->
                try {
                    val answer = answers.findOne(Answer::id eq request.answerId)
                    if (answer != null) {
                        if (!answer.image.isNullOrEmpty()) {
                            val fileName = answer.image!!.substringAfterLast("/")
                            val file = File("uploads/image/$fileName")
                            if (file.exists()) {
                                file.delete()
                            }
                        }
                        if (!answer.video.isNullOrEmpty()) {
                            val fileName = answer.video!!.substringAfterLast("/")
                            val file = File("uploads/video/$fileName")
                            if (file.exists()) {
                                file.delete()
                            }
                        }

                        if (answers.deleteOneById(request.answerId).wasAcknowledged()) {
                            call.response.status(HttpStatusCode.OK)
                            call.respond(ApiResponse.ok())
                        } else {
                            call.response.status(HttpStatusCode.InternalServerError)
                            call.respond(ApiResponse.error())
                        }
                    } else {
                        call.response.status(HttpStatusCode.NotFound)
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }
        }

        authenticate {
            post<UpVoteAnswer> { request ->
                val answer = answers.findOne(Answer::id eq request.answerId)
                answer?.let {
                    it.upVote.add(request.userId)
                    it.downVote.remove(request.userId)
                    val updateResult = answers.updateOneById(it.id, it, updateOnlyNotNullProperties = true)
                    if (updateResult.wasAcknowledged()) {
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ApiResponse.ok())
                    } else {
                        call.response.status(HttpStatusCode.InternalServerError)
                        call.respond(ApiResponse.error())
                    }
                } ?: run {
                    call.response.status(HttpStatusCode.NotFound)
                }
            }
        }

        authenticate {
            post<DownVoteAnswer> { request ->
                val answer = answers.findOne(Answer::id eq request.answerId)
                answer?.let {
                    it.downVote.add(request.userId)
                    it.upVote.remove(request.userId)
                    val updateResult = answers.updateOneById(it.id, it, updateOnlyNotNullProperties = true)
                    if (updateResult.wasAcknowledged()) {
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ApiResponse.ok())
                    } else {
                        call.response.status(HttpStatusCode.InternalServerError)
                        call.respond(ApiResponse.error())
                    }
                } ?: run {
                    call.response.status(HttpStatusCode.NotFound)
                }
            }
        }

        authenticate {
            post<QuestionAnswers> { request ->
                try {
                    val foundAnswers = answers.find(Answer::questionId eq request.questionId).toList()
                    val result = getAnswerInfo(foundAnswers)
                    call.response.status(HttpStatusCode.OK)
                    call.respond(ApiResponse.success(result))
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(ApiResponse.error(e.message))
                }
            }
        }

        authenticate {
            post<UserAnswers> { request ->
                try {
                    val foundAnswers = answers.find(Answer::userId eq request.userId).toList()
                    val result = getAnswerInfo(foundAnswers)
                    call.response.status(HttpStatusCode.OK)
                    call.respond(ApiResponse.success(result))
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(ApiResponse.error(e.message))
                }
            }
        }

        authenticate {
            post<ClassAnswers> { request ->
                try {
                    val classroom = classes.findOne(Classroom::id eq request.classId)
                    classroom?.let {
                        val foundAnswers = answers.aggregate<Answer>(
                            match(
                                Answer::userId `in` it.users.toList(),
                            )
                        ).toList()
                        val result = getAnswerInfo(foundAnswers)
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ApiResponse.success(result))
                    } ?: run {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(ApiResponse.error(e.message))
                }
            }
        }

        get<AllAnswers> {
            try {
                val foundAnswers = answers.find().toList()
                call.response.status(HttpStatusCode.OK)
                call.respond(ApiResponse.success(foundAnswers))
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(ApiResponse.error(e.message))
            }
        }
    }
}