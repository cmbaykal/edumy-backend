package com.edumy.base

import io.ktor.locations.*

@Location("image/{fileName}")
data class DownloadImage(val fileName: String)

@Location("video/{fileName}")
data class DownloadVideo(val fileName: String)