package com.edumy.routing.classroom

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/class/add")
class AddClass

@Serializable
@Resource("/class/assign")
class AssignUser(val classId: String, val userMail: String)

@Serializable
@Resource("/class/leave")
class LeaveClass(val classId: String, val userMail: String)

@Serializable
@Resource("/class/delete")
class DeleteClass(val classId: String, val userMail: String)

@Serializable
@Resource("/class/info")
class ClassInfo(val classId: String)

@Serializable
@Resource("/class/user")
class UserClassrooms(val userId: String)

@Serializable
@Resource("/class/all")
class AllClasses