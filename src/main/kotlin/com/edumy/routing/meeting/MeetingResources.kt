package com.edumy.routing.meeting

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("meeting/schedule")
class ScheduleMeeting

@Serializable
@Resource("meeting/user")
class UserMeetings(val userId: String)