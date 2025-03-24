package com.hana.baseproject.api.user.service.impl

import com.hana.baseproject.api.user.controller.request.UserCreate
import com.hana.baseproject.api.user.controller.request.UserUpdate
import com.hana.baseproject.api.user.controller.response.UserInformation
import com.hana.baseproject.api.user.service.UserService
import org.springframework.stereotype.Service

@Service
class UserServiceImpl : UserService {
    override fun duplicateUser(username: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun findUser(companyCode: String, username: String): UserInformation {
        TODO("Not yet implemented")
    }

    override fun findUsersByCompanyCode(companyCode: String): List<UserInformation> {
        TODO("Not yet implemented")
    }

    override fun findUsers(): List<UserInformation> {
        TODO("Not yet implemented")
    }

    override fun createUser(userCreate: UserCreate): UserInformation {
        TODO("Not yet implemented")
    }

    override fun updateUser(userUpdate: UserUpdate): UserInformation {
        TODO("Not yet implemented")
    }

    override fun deleteUser(username: String): UserInformation {
        TODO("Not yet implemented")
    }


}
