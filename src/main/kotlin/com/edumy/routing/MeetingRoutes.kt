package com.edumy.routing

import com.edumy.base.ApiResponse
import com.edumy.data.answer.*
import com.edumy.data.classroom.Classroom
import com.edumy.data.meeting.Meeting
import com.edumy.data.meeting.MeetingResult
import com.edumy.data.meeting.ScheduleMeeting
import com.edumy.data.meeting.UserMeetings
import com.edumy.data.question.Question
import com.edumy.data.user.User
import com.edumy.data.user.UserEntity
import com.edumy.util.DateSerializer
import com.edumy.util.FileType
import com.edumy.util.FileType.Companion.fileType
import com.edumy.util.FileType.Companion.path
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.aggregate
import org.litote.kmongo.coroutine.insertOne
import java.io.File
import java.util.*

fun Application.meetingRoutes(database: CoroutineDatabase) {

    val meetings = database.getCollection<Meeting>()
    val users = database.getCollection<UserEntity>()

    routing {
        authenticate {
            post<ScheduleMeeting> {
                try {
                    val meeting = call.receive<Meeting>()
                    if (meetings.insertOne(meeting).wasAcknowledged()) {
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ApiResponse.ok())
                    } else {
                        call.response.status(HttpStatusCode.InternalServerError)
                        call.respond(ApiResponse.error())
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(ApiResponse.error(e.message))
                }
            }
        }

        authenticate {
            post<UserMeetings> { request ->
                try {
                    val user = users.findOne(User::id eq request.userId)

                    user?.classes?.let { classList ->
                        val result = mutableListOf<MeetingResult>()
                        val meetingList = mutableListOf<Meeting>()
                        classList.forEach {
                            val foundMeetings = meetings.find(Meeting::classId eq it).toList()
                            meetingList.addAll(foundMeetings)
                        }
                        meetingList.forEach {
                            val meetingUser = users.aggregate<User>(
                                match(User::id eq it.creatorId),
                                project(
                                    exclude(
                                        User::classes,
                                        User::role,
                                        User::birth,
                                    )
                                )
                            ).first()
                            val aggregatedData = meetings.aggregate<MeetingResult>(
                                match(Meeting::id eq it.id),
                                project(
                                    MeetingResult::id from Meeting::id,
                                    MeetingResult::user from meetingUser,
                                    MeetingResult::lesson from Meeting::lesson,
                                    MeetingResult::description from Meeting::description,
                                    MeetingResult::duration from Meeting::duration,
                                    MeetingResult::date from Meeting::date,
                                )
                            ).toList()
                            result.addAll(aggregatedData)
                        }

                        call.response.status(HttpStatusCode.OK)
                        call.respond(ApiResponse.success(result))
                    } ?: run {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(ApiResponse.error(e.message))
                }
            }
        }
    }
}