package com.edumy.data.study

import io.ktor.locations.*

@Location("study/add")
class AddStudy

@Location("study/delete")
data class DeleteStudy(val studyId: String)

@Location("study/user")
data class UserStudies(val userId: String)

@Location("study/all")
class AllStudies