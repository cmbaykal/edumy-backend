package com.edumy.data.answer

import com.edumy.base.BaseResponse
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
            try {
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
                    call.respond(BaseResponse.success(answer))
                } else {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(BaseResponse.error())
                }
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(BaseResponse.error(e.message))
            }
        }

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
                        call.respond(BaseResponse.ok())
                    } else {
                        call.response.status(HttpStatusCode.InternalServerError)
                        call.respond(BaseResponse.error())
                    }
                } else {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(BaseResponse.error())
                }
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(BaseResponse.error(e.message))
            }
        }

        get<QuestionAnswers> { request ->
            try {
                val foundAnswers = answers.find(Answer::questionId eq request.questionId).toList()
                call.response.status(HttpStatusCode.OK)
                call.respond(BaseResponse.success(foundAnswers))
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(BaseResponse.error(e.message))
            }
        }

        get<UserAnswers> { request ->
            try {
                val foundAnswers = answers.find(Answer::userId eq request.userId).toList()
                call.response.status(HttpStatusCode.OK)
                call.respond(BaseResponse.success(foundAnswers))
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(BaseResponse.error(e.message))
            }
        }

        get<AllAnswers> {
            try {
                val foundAnswers = answers.find().toList()
                call.response.status(HttpStatusCode.OK)
                call.respond(BaseResponse.success(foundAnswers))
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(BaseResponse.error(e.message))
            }
        }
    }
}