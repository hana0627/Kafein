package com.hana.baseproject.api.user.service

import com.hana.baseproject.api.user.controller.request.UserCreate
import com.hana.baseproject.api.user.controller.request.UserUpdate
import com.hana.baseproject.api.user.controller.response.UserInformation

interface UserService {
    fun duplicateUser(username: String): Boolean
    fun findUser(companyCode: String, username: String): UserInformation
    fun findUsersByCompanyCode(companyCode: String): List<UserInformation>
    fun findUsers(): List<UserInformation>
    fun createUser(userCreate: UserCreate): UserInformation
    fun updateUser(userUpdate: UserUpdate): UserInformation
    fun deleteUser(username: String): UserInformation
}
