package com.hana.baseproject.api.user.controller.request

import com.hana.baseproject.api.user.domain.constant.Gender
import com.hana.baseproject.api.user.domain.constant.UserType

data class UserCreate (

    val username: String,
    val name: String,
    val phoneNumber: String,
    val description: String,
    val gender: Gender,
    var userType: UserType,
    val companyCode: String,
){
    companion object {
        fun fixture(
            username: String = "hanana",
            name: String = "박하나",
            phoneNumber: String = "010-1234-5678",
            description: String = "하나다방 사장님",
            gender: Gender = Gender.F,
            userType: UserType = UserType.OWNER,
            companyCode: String = "A0000001",
        ): UserCreate {
            return UserCreate(
                username = username,
                name = name,
                phoneNumber = phoneNumber,
                description = description,
                gender = gender,
                userType = userType,
                companyCode = companyCode,
            )
        }
    }
}
