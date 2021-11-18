package com.edumy.data.answer

import com.edumy.util.DateSerializer
import com.edumy.util.FileType
import com.edumy.util.FileType.Companion.fileType
import com.edumy.util.FileType.Companion.path
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import java.io.File
import java.util.*

fun Application.answerRoutes(database: CoroutineDatabase) {

    val answers = database.getCollection<Answer>()

    routing {

        post<AddAnswer> {
            val multipartData = call.receiveMultipart()
            val answer = Answer()

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "questionId" -> answer.questionId = part.value
                            "userId" -> answer.userId = part.value
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
                                answer.video = "http://0.0.0.0:8080/video/$fileName"
                            }
                            else -> {
                                answer.image = "http://0.0.0.0:8080/image/$fileName"
                            }
                        }
                    }
                }
            }

            if (answers.insertOne(answer).wasAcknowledged()) {
                call.response.status(HttpStatusCode.OK)
                call.respond(answer)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post<DeleteAnswer> { request ->
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
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get<QuestionAnswers> { request ->
            call.respond(answers.find(Answer::questionId eq request.questionId).toList())
        }

        get<UserAnswers> { request ->
            call.respond(answers.find(Answer::userId eq request.userId).toList())
        }

        get<AllAnswers> {
            call.respond(answers.find().toList())
        }

    }

}