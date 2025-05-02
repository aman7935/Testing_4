package com.example.testing4.models.resource

data class Resource<out T>(
    val result: Result,
    val data: T?,
    val message: String?
) {
    companion object {

        fun <T> success(data: T?, message: String?) =
            Resource(result = Result.SUCCESS, data = data, message = message)

        fun <T> failure(data: T?, message: String?) =
            Resource(result = Result.FAILURE, data = data, message = message)

        fun <T> loading(data: T?, message: String?) =
            Resource(result = Result.LOADING, data = data, message = null)
    }
}