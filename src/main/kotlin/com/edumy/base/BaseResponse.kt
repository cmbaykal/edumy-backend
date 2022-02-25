package com.edumy.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class BaseResult(
    @SerialName("dialog")
    var dialog: EdumyDialog? = null,

    @SerialName("result")
    var result: EdumyResult? = null,
)

@Serializable
data class BaseResponse<T>(
    @SerialName("data")
    var data: T? = null
) : BaseResult() {
    companion object {
        fun <T> success(data: T? = null): BaseResponse<T> {
            val response = BaseResponse<T>()
            response.result = EdumyResult(success = true)
            response.data = data
            return response
        }

        fun ok(): BaseResult {
            val response = BaseResult()
            response.result = EdumyResult(success = true)
            return response
        }

        fun error(message: String? = null, dialog: EdumyDialog? = null): BaseResult {
            val response = BaseResult()
            val error = message ?: "Oops, an error occurred. Please try again."
            response.result = EdumyResult(message = error, success = false)
            dialog?.let {
                response.dialog = it
            }
            return response
        }
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

