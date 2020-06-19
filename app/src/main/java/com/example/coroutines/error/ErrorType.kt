package com.example.coroutines.error

sealed class ErrorType {
    data class GenericErrorType(val code: Int? = null, val error: ErrorResponse? = null) : ErrorType()
    object NetworkErrorType : ErrorType()
}