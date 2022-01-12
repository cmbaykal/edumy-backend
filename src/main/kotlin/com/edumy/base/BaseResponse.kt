package com.edumy.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    @SerialName("dialog")
    val dialog: EdumyDialog? = null,

    @SerialName("result")
    var result: EdumyResult? = null,

    @SerialName("data")
    var data: T? = null
) {
    companion object {
        fun <T> success(data: T? = null): BaseResponse<T> {
            val response = BaseResponse<T>()
            response.result = EdumyResult(success = true)
            response.data = data
            return response
        }

        fun error(message: String? = null): BaseResponse<Nothing> {
            val response = BaseResponse<Nothing>()
            val error = message ?: "Oops, an error occurred. Please try again."
            response.result = EdumyResult(error = error, success = false)
            return response
        }
    }
}

@Serializable
data class EdumyResult(
    @SerialName("error") val error: String? = null,
    @SerialName("success") val success: Boolean?
)

@Serializable
data class EdumyDialog(
    @SerialName("title") val title: String,
    @SerialName("message") val message: String,
    @SerialName("buttons") val buttons: List<DialogButton>
)

@Serializable
data class DialogButton(
    @SerialName("text") val text: String,
    @SerialName("action") val action: ButtonAction? = ButtonAction.dismiss
)

@Serializable
enum class ButtonAction {
    @SerialName("dismiss")
    dismiss,

    @SerialName("retry")
    retry,

    @SerialName("close")
    close;
}

