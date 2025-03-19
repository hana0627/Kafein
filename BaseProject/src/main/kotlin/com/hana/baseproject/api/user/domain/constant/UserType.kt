package com.hana.baseproject.api.user.domain.constant

enum class UserType(
    val description: String
) {
    ADMIN("관리자"),
    OWNER("사장"),
    MANAGER("관리자"),
    CUSTOMER("고객"),
}
