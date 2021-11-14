package com.edumy.data.performance

import io.ktor.locations.*

@Location("performance/add")
class AddPerformance

@Location("performance/delete")
data class DeletePerformance(val performanceId: String)

@Location("performance/user")
data class UserPerformances(val userId: String)

@Location("performance/all")
class AllPerformances