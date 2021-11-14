package com.edumy.data.usage

import java.util.*


data class UsageData(
    val userId: String,
    val usages: MutableList<Usage>? = ArrayList()
)

data class Usage(
    val name: String,
    val usage: Int,
    val date: Date
)
