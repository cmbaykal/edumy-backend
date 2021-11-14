package com.edumy.data.question

import com.edumy.data.answer.Answer
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
                        }
                    }
                    is PartData.FileItem -> {
                        val extension = (part.originalFileName as String).substringAfterLast(".")
                        val fileName = UUID.randomUUID().toString() + "." + extension
                        val fileBytes = part.streamProvider().readBytes()
                        File("uploads/image/$fileName").writeBytes(fileBytes)

                        question.image = "http://0.0.0.0:8080/image/$fileName"
                    }
                }
            }

            if (questions.insertOne(question).wasAcknowledged()) {
                call.response.status(HttpStatusCode.OK)
                call.respond(question)
            } else {
                call.response.status(HttpStatusCode.InternalServerError)
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
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get<AllQuestions> {
            call.respond(questions.find().toList())
        }

        get<QuestionInfo> { request ->
            call.respond(questions.find(Question::id eq request.questionId).toList())
        }

        get<ClassQuestions> { request ->
            call.respond(questions.find(Question::classId eq request.classId).toList())
        }

        get<UserQuestions> { request ->
            call.respond(questions.find(Question::userId eq request.userId).toList())
        }

    }

}