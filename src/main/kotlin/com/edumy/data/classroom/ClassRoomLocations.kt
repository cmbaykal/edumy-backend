package com.edumy.data.classroom

import io.ktor.locations.*

@Location("/class/add")
class AddClass

@Location("/class/assign")
data class AssignUser(val classId: String, val userId: String)

@Location("/class/leave")
data class LeaveClass(val classId: String, val userId: String)

@Location("/class/info")
data class ClassInfo(val classId: String)

@Location("/class/all")
class AllClasses