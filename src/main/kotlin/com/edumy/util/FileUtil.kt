package com.edumy.util

import com.edumy.util.FileType.Companion.extension
import com.edumy.util.FileType.Companion.path
import java.io.File
import java.util.*

fun decodeFile(type: FileType, fileString: String): File {
    val fileBytes = Base64.getDecoder().decode(fileString)
    val fileName = UUID.randomUUID().toString()

    val file = File("uploads/${type.path}/$fileName.${type.extension}")
    file.writeBytes(fileBytes)

    return file
}

enum class FileType(val type: String) {
    ImageJPEG("image/jpeg"),
    ImagePNG("image/png"),
    VideoMP4("video/mp4");

    companion object {
        val String.fileType: FileType
            get() {
                return values().first { it.type == this }
            }

        val FileType.extension: String
            get() {
                return when (this) {
                    ImageJPEG -> "jpg"
                    ImagePNG -> "png"
                    VideoMP4 -> "mp4"
                }
            }

        val FileType.path: String
            get() {
                return when (this) {
                    ImageJPEG -> "image"
                    ImagePNG -> "image"
                    VideoMP4 -> "video"
                }
            }
    }
}

