package com.edumy.routing.study

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("study/add")
class AddStudy

@Serializable
@Resource("study/delete")
class DeleteStudy(val studyId: String)

@Serializable
@Resource("study/user")
class UserStudies(val userId: String)

@Serializable
@Resource("study/classroom")
class ClassStudies(val classId: String)

@Serializable
@Resource("study/feed")
class StudiesFeed(val userId: String)

@Serializable
@Resource("study/all")
class AllStudies