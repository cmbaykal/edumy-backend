package com.edumy.routing

import com.edumy.base.ApiResponse
import com.edumy.data.classroom.Classroom
import com.edumy.data.meeting.Meeting
import com.edumy.data.meeting.MeetingResult
import com.edumy.data.user.User
import com.edumy.data.user.UserEntity
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.aggregate

fun Application.meetingRoutes(database: CoroutineDatabase) {

    val meetings = database.getCollection<Meeting>()
    val users = database.getCollection<UserEntity>()
    val classrooms = database.getCollection<Classroom>()

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
                            val meetingClassroom = classrooms.aggregate<Classroom>(
                                match(Classroom::id eq it.classId),
                                project(
                                    exclude(
                                        Classroom::users
                                    )
                                )
                            ).first()
                            val aggregatedData = meetings.aggregate<MeetingResult>(
                                match(Meeting::id eq it.id),
                                project(
                                    MeetingResult::id from Meeting::id,
                                    MeetingResult::user from meetingUser,
                                    MeetingResult::classroom from meetingClassroom,
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