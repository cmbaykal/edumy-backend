package com.edumy.base.routing

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("image/{fileName}")
data class DownloadImage(val fileName: String)

@Serializable
@Resource("video/{fileName}")
data class DownloadVideo(val fileName: String)