package com.edumy.routing

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/class/add")
class AddClass

@Serializable
@Resource("/class/assign")
data class AssignUser(val classId: String, val userMail: String)

@Serializable
@Resource("/class/leave")
data class LeaveClass(val classId: String, val userMail: String)

@Serializable
@Resource("/class/delete")
data class DeleteClass(val classId: String, val userMail: String)

@Serializable
@Resource("/class/info")
data class ClassInfo(val classId: String)

@Serializable
@Resource("/class/user")
data class UserClassrooms(val userId: String)

@Serializable
@Resource("/class/all")
class AllClasses