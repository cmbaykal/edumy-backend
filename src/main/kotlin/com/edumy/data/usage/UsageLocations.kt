package com.edumy.data.usage

import io.ktor.locations.*

@Location("/usage/add")
class AddUsage

@Location("/usage/{userId}")
data class UserUsages(val userId: String)

@Location("/usage/all")
class AllUsages

@Location("/usage/delete")
data class DeleteUsages(val userId:String)

@Location("/usage/delete")
class DeleteAllUsages