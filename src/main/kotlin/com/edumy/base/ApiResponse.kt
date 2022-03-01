package com.edumy.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class BaseResponse(
    @SerialName("dialog")
    var dialog: EdumyDialog? = null,

    @SerialName("success")
    var success: Boolean? = false,

    @SerialName("error")
    var error: String? = null
)

@Serializable
data class ApiResponse<T>(
    @SerialName("data")
    var data: T? = null
) : BaseResponse() {
    companion object {
        fun <T> success(data: T? = null) = ApiResponse(data).apply {
            success = true
        }

        fun ok(): BaseResponse = BaseResponse(success = true)

        fun error(message: String? = null, dialog: EdumyDialog? = null) = BaseResponse(
            success = false,
            error = message ?: "Oops, an error occurred. Please try again.",
            dialog = dialog
        )
    }
}

@Serializable
data class EdumyResult(
    @SerialName("success") val success: Boolean?,
    @SerialName("message") val message: String? = null
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

