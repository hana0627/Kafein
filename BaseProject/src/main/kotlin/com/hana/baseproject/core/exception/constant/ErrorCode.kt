package com.hana.baseproject.core.exception.constant

import org.springframework.http.HttpStatus

enum class ErrorCode (
    val status: HttpStatus,
    val message: String,
){
    SAMPLE_ERROR_CODE(HttpStatus.INTERNAL_SERVER_ERROR, "예시용 에러코드 입니다."),
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "회사정보를 찾을 수 없습니다."),
}
