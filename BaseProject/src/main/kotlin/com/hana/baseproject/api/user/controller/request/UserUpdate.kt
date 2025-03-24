package com.hana.baseproject.api.user.controller.request

import com.hana.baseproject.api.user.domain.constant.UserType

data class UserUpdate (
    val username: String,
    val name: String,
    val phoneNumber: String,
    val description: String,
    var userType: UserType,
    val companyCode: String,
){

    companion object {
        fun fixture(
            username: String = "hanana",
            name: String = "박하나",
            phoneNumber: String = "010-1234-5678",
            description: String = "하나다방 사장님에서 매니저로 직무전환",
            userType: UserType = UserType.MANAGER,
            companyCode: String = "A0000001",
        ): UserUpdate {
            return UserUpdate(
                username = username,
                name = name,
                phoneNumber = phoneNumber,
                description = description,
                userType = userType,
                companyCode = companyCode,
            )
        }
    }
}
