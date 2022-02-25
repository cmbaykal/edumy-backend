package com.edumy.data.question

import com.edumy.base.BaseResponse
import com.edumy.data.answer.Answer
import com.edumy.util.DateSerializer
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.routing
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import java.io.File
import java.util.*

fun Application.questionRoutes(database: CoroutineDatabase) {

    val questions = database.getCollection<Question>()

    routing {
        post<AddQuestion> {
            val multipartData = call.receiveMultipart()
            val question = Question()

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "classId" -> question.classId = part.value
                            "userId" -> question.userId = part.value
                            "lesson" -> question.lesson = part.value
                            "question" -> question.question = part.value
                            "date" -> question.date = DateSerializer.parse(part.value)
                        }
                    }
                    is PartData.FileItem -> {
                        val extension = (part.originalFileName as String).substringAfterLast(".")
                        val fileName = UUID.randomUUID().toString() + "." + extension
                        val fileBytes = part.streamProvider().readBytes()
                        File("uploads/image/$fileName").writeBytes(fileBytes)

                        question.image = "http://0.0.0.0:8080/image/$fileName"
                    }
                    is PartData.BinaryItem -> TODO()
                }
            }

            if (questions.insertOne(question).wasAcknowledged()) {
                call.response.status(HttpStatusCode.OK)
                call.respond(BaseResponse.success(question))
            } else {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(BaseResponse.error())
            }
        }

        post<DeleteQuestion> { request ->
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
                    call.respond(BaseResponse.ok())
                } else {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(BaseResponse.error())
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
                call.respond(BaseResponse.error())
            }
        }

        get<AllQuestions> {
            try {
                call.respond(questions.find().toList())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
                call.respond(BaseResponse.error(e.message))
            }
        }

        get<QuestionInfo> { request ->
            try {
                val foundQuestions = questions.find(Question::id eq request.questionId).toList()
                call.response.status(HttpStatusCode.OK)
                call.respond(BaseResponse.success(foundQuestions))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
                call.respond(BaseResponse.error(e.message))
            }
        }

        get<ClassQuestions> { request ->
            try {
                val foundQuestions = questions.find(Question::classId eq request.classId).toList()
                call.response.status(HttpStatusCode.OK)
                call.respond(BaseResponse.success(foundQuestions))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
                call.respond(BaseResponse.error(e.message))
            }
        }

        get<UserQuestions> { request ->
            try {
                val foundQuestions = questions.find(Question::userId eq request.userId).toList()
                call.response.status(HttpStatusCode.OK)
                call.respond(BaseResponse.success(foundQuestions))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
                call.respond(BaseResponse.error(e.message))
            }
        }
    }
}