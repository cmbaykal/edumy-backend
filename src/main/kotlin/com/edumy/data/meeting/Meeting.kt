package com.edumy.data.meeting

import java.util.*

data class Meeting(
    val id:Int,
    val classId:Int,
    val userId:Int,
    val lesson:String,
    val duration:Int,
    val time:Date
)
