package com.edumy.routing.question

import com.edumy.base.ApiResponse
import com.edumy.data.answer.Answer
import com.edumy.data.classroom.Classroom
import com.edumy.data.question.Question
import com.edumy.data.question.QuestionInformation
import com.edumy.data.user.User
import com.edumy.data.user.UserEntity
import com.edumy.util.DateSerializer
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

fun Application.questionRoutes(database: CoroutineDatabase) {

    val users = database.getCollection<UserEntity>()
    val classrooms = database.getCollection<Classroom>()
    val questions = database.getCollection<Question>()

    routing {
        authenticate {
            post<AddQuestion> {
                try {
                    val multipartData = call.receiveMultipart()
                    val question = Question()

                    multipartData.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                when (part.name) {
                                    "userId" -> question.userId = part.value
                                    "lesson" -> question.lesson = part.value
                                    "description" -> question.description = part.value
                                    "date" -> question.date = DateSerializer.parse(part.value)
                                }
                            }
                            is PartData.FileItem -> {
                                val extension = (part.originalFileName as String).substringAfterLast(".")
                                val fileName = UUID.randomUUID().toString() + "." + extension
                                val fileBytes = part.streamProvider().readBytes()
                                File("uploads/image/$fileName").writeBytes(fileBytes)

                                question.image = fileName
                            }
                            else -> {}
                        }
                    }

                    if (questions.insertOne(question).wasAcknowledged()) {
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ApiResponse.ok())
                    } else {
                        call.response.status(HttpStatusCode.InternalServerError)
                        call.respond(ApiResponse.error())
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                    print(e)
                }
            }
        }

        authenticate {
            post<DeleteQuestion> { request ->
                try {
                    val question = questions.findOne(Answer::id eq request.questionId)

                    if (question != null) {
                        if (!question.image.isNullOrEmpty()) {
                            val fileName = question.image!!.substringAfterLast("/")
                            val file = File("uploads/image/$fileName")
                            if (file.exists()) {
                                file.delete()
                            }
                        }

                        if (questions.deleteOneById(request.questionId).wasAcknowledged()) {
                            call.response.status(HttpStatusCode.OK)
                            call.respond(ApiResponse.ok())
                        } else {
                            call.response.status(HttpStatusCode.InternalServerError)
                            call.respond(ApiResponse.error())
                        }
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }
        }

        authenticate {
            post<QuestionInfo> { request ->
                try {
                    val foundQuestion = questions.findOne(Question::id eq request.questionId)
                    foundQuestion?.let {
                        val user = users.findOne(User::id eq it.userId)
                        val result = questions.aggregate<QuestionInformation>(
                            match(
                                Question::id eq request.questionId
                            ),
                            project(
                                QuestionInformation::id from Question::id,
                                QuestionInformation::user from user,
                                QuestionInformation::lesson from Question::lesson,
                                QuestionInformation::description from Question::description,
                                QuestionInformation::date from Question::date,
                                QuestionInformation::image from Question::image
                            )
                        ).first()
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

        authenticate {
            post<ClassQuestions> { request ->
                try {
                    val classroom = classrooms.findOne(Classroom::id eq request.classId)
                    classroom?.let {
                        val foundQuestions = questions.aggregate<Question>(
                            match(
                                Question::userId `in` it.users.toList(),
                                Question::lesson eq it.lesson
                            ),
                            skip(request.limit * request.page),
                            limit(request.limit)
                        ).toList()
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ApiResponse.success(foundQuestions))
                    } ?: run {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(ApiResponse.error(e.message))
                }
            }
        }

        authenticate {
            post<UserQuestions> { request ->
                try {
                    val foundQuestions = questions
                        .find(Question::userId eq request.userId)
                        .skip(request.limit * request.page)
                        .limit(request.limit)
                        .toList()
                    call.response.status(HttpStatusCode.OK)
                    call.respond(ApiResponse.success(foundQuestions))
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(ApiResponse.error(e.message))
                }
            }
        }

        authenticate {
            post<QuestionsFeed> { request ->
                try {
                    val feedQuestions = questions
                        .find()
                        .skip(request.limit * request.page)
                        .limit(request.limit)
                        .toList()
                    call.response.status(HttpStatusCode.OK)
                    call.respond(ApiResponse.success(feedQuestions))
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(ApiResponse.error(e.message))
                }
            }
        }

        get<AllQuestions> {
            try {
                val foundQuestions = questions.find().toList()
                call.response.status(HttpStatusCode.OK)
                call.respond(ApiResponse.success(foundQuestions))
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(ApiResponse.error(e.message))
            }
        }
    }
}