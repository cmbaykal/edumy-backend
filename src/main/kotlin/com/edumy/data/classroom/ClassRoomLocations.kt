package com.edumy.data.classroom

import io.ktor.locations.*

@Location("/class/add")
class AddClass

@Location("/class/assign")
data class AssignUser(val classId: String, val userMail: String)

@Location("/class/leave")
data class LeaveClass(val classId: String, val userMail: String)

@Location("/class/delete")
data class DeleteClass(val classId: String, val userMail: String)

@Location("/class/info")
data class ClassInfo(val classId: String)

@Location("/class/user")
data class UserClassrooms(val userId: String)

@Location("/class/all")
class AllClasses