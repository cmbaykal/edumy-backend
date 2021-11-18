package com.edumy.data.usage

import com.edumy.util.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UsageData(
    @SerialName("userId")
    val userId: String,
    @SerialName("usages")
    val usages: MutableList<Usage>? = ArrayList()
)

@Serializable
data class Usage(
    @SerialName("name")
    val name: String,
    @SerialName("usage")
    val usage: Int,
    @Serializable(with = DateSerializer::class)
    @SerialName("date")
    val date: Date
)