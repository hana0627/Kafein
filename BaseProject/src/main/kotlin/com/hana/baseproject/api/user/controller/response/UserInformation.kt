package com.hana.baseproject.api.user.controller.response

import com.hana.baseproject.api.user.domain.constant.Gender
import com.hana.baseproject.api.user.domain.constant.UserType
import java.time.LocalDateTime

data class UserInformation(
    val username: String,
    val name: String,
    val phoneNumber: String,
    val description: String,
    val gender: Gender,
    val userType: UserType,
    val point: Int,
    val deleted: Boolean,
    val deletedDate: LocalDateTime?,
    val companyCode: String,
    val companyName: String,
) {

    companion object {
        fun fixture(
            username: String = "hanana",
            name: String = "박하나",
            phoneNumber: String = "010-1234-5678",
            description: String = "하나다방 사장님",
            gender: Gender = Gender.F,
            userType: UserType = UserType.OWNER,
            point: Int = 0,
            deleted: Boolean = false,
            deletedDate: LocalDateTime? = null,
            companyCode: String = "A0000001",
            companyName: String = "하나다방",
        ): UserInformation {
            return UserInformation(
                username = username,
                name = name,
                phoneNumber = phoneNumber,
                description = description,
                gender = gender,
                userType = userType,
                point = point,
                deleted = deleted,
                deletedDate = deletedDate,
                companyCode = companyCode,
                companyName = companyName,
            )
        }
    }
}
