package com.edumy.routing

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("meeting/schedule")
class ScheduleMeeting

@Serializable
@Resource("meeting/user")
data class UserMeetings(val userId: String)