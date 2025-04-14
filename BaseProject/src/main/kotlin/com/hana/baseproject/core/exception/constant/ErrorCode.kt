package com.hana.baseproject.core.exception.constant

import org.springframework.http.HttpStatus

enum class ErrorCode (
    val status: HttpStatus,
    val message: String,
){
    SAMPLE_ERROR_CODE(HttpStatus.INTERNAL_SERVER_ERROR, "예시용 에러코드 입니다."),
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "회사정보를 찾을 수 없습니다."),
    ALREADY_USED_USERNAME(HttpStatus.CONFLICT, "이미사용중인_유저명_입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테코리를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    CATEGORY_CODE_LIMIT_EXCEEDED(HttpStatus.UNPROCESSABLE_ENTITY, "등록할 수 있는 상품갯수를 초과했습니다."),


}
