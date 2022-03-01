package com.edumy.data.auth

import com.edumy.util.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class AuthToken(
    @SerialName("token")
    val token: String,
    @Serializable(with = DateSerializer::class)
    @SerialName("expireTime")
    val expireTime: Date?
)