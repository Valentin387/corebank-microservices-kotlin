package com.corebank.commons.model

data class ResponseDTO<T>(
    val statusCode: Int,
    val body: T?,
    val extraArgs: Map<String, Any>? = null
) {
    companion object {
        fun <T> success(body: T): ResponseDTO<T> = ResponseDTO(200, body)
        fun <T> error(statusCode: Int, body: T, extraArgs: Map<String, Any>? = null): ResponseDTO<T> =
            ResponseDTO(statusCode, body, extraArgs)
    }
}
