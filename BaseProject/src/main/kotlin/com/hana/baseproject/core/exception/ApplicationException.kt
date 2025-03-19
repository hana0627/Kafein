package com.hana.baseproject.core.exception

import com.hana.baseproject.core.exception.constant.ErrorCode

class ApplicationException(
    private val errorCode: ErrorCode,
    override val message: String? = null
) : RuntimeException() {


    val getErrorCode: ErrorCode
        get() = errorCode


    val getMessage: String
        get() = message ?: errorCode.message
}
