package com.edumy.data.meeting

import io.ktor.locations.*

@Location("meeting/schedule")
class ScheduleMeeting

@Location("meeting/user")
data class UserMeetings(val userId: String)