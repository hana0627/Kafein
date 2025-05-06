package com.hana.baseproject.api.user.service.impl

import com.hana.baseproject.api.company.domain.CompanyEntity
import com.hana.baseproject.api.company.repository.CompanyRepository
import com.hana.baseproject.api.user.controller.request.UserCreate
import com.hana.baseproject.api.user.controller.request.UserUpdate
import com.hana.baseproject.api.user.controller.response.UserInformation
import com.hana.baseproject.api.user.domain.UserEntity
import com.hana.baseproject.api.user.repository.UserRepository
import com.hana.baseproject.api.user.service.UserService
import com.hana.baseproject.core.config.ClockConfig
import com.hana.baseproject.core.exception.ApplicationException
import com.hana.baseproject.core.exception.constant.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val companyRepository: CompanyRepository,
    private val clockConfig: ClockConfig,
) : UserService {
    override fun duplicateUser(username: String): Boolean {
        // 회원아이디 중복확인을 진행한다.

        // null -> return true
        // notnull -> return false
        return userRepository.findByUsername(username) == null
    }

    override fun findUser(companyCode: String, username: String): UserInformation {
        // 회원한명을 조회한다.
        val foundCompany: CompanyEntity =
            companyRepository.findByCompanyCode(companyCode) ?: throw ApplicationException(
                ErrorCode.COMPANY_NOT_FOUND, ErrorCode.COMPANY_NOT_FOUND.message
            )

        val foundUser: UserEntity = userRepository.findByUsername(username) ?: throw ApplicationException(
            ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.message
        )

        return UserInformation(
            username = foundUser.username,
            name = foundUser.name,
            phoneNumber = foundUser.phoneNumber,
            description = foundUser.description,
            gender = foundUser.gender,
            userType = foundUser.userType,
            point = foundUser.point,
            deleted = foundUser.deleted,
            deletedDate = foundUser.deletedDate,
            companyCode = foundCompany.companyCode,
            companyName = foundCompany.companyName,
        )

    }

    override fun findUsersByCompanyCode(companyCode: String): List<UserInformation> {
        // 특정 회사의 회원을 조회한다.
        val foundCompany: CompanyEntity =
            companyRepository.findByCompanyCode(companyCode) ?: throw ApplicationException(
                ErrorCode.COMPANY_NOT_FOUND, ErrorCode.COMPANY_NOT_FOUND.message
            )

        //TODO N+1 문제 발생
        val userEntity: List<UserEntity> = foundCompany.userEntity

        return userEntity.map { it: UserEntity ->
            UserInformation(
                username = it.username,
                name = it.name,
                phoneNumber = it.phoneNumber,
                description = it.description,
                gender = it.gender,
                userType = it.userType,
                point = it.point,
                deleted = it.deleted,
                deletedDate = it.deletedDate,
                companyCode = it.company!!.companyCode,
                companyName = it.company!!.companyName,
            )
        }

    }

    override fun findUsers(): List<UserInformation> {
        // 모든 회원의 목록을 조회한다.
        return userRepository.findAll().map { it: UserEntity ->
            UserInformation(
                username = it.username,
                name = it.name,
                phoneNumber = it.phoneNumber,
                description = it.description,
                gender = it.gender,
                userType = it.userType,
                point = it.point,
                deleted = it.deleted,
                deletedDate = it.deletedDate,
                companyCode = it.company!!.companyCode,
                companyName = it.company!!.companyName
            )
        }
    }

    @Transactional
    override fun createUser(userCreate: UserCreate): UserInformation {
        // 회원을 등록한다.
        val foundCompany: CompanyEntity =
            companyRepository.findByCompanyCode(userCreate.companyCode) ?: throw ApplicationException(
                ErrorCode.COMPANY_NOT_FOUND, ErrorCode.COMPANY_NOT_FOUND.message
            )
        val optionalUser = userRepository.findByUsername(userCreate.username)
        if (optionalUser != null) {
            throw ApplicationException(
                ErrorCode.ALREADY_USED_USERNAME, ErrorCode.ALREADY_USED_USERNAME.message
            )
        }

        val user: UserEntity = UserEntity(
            username = userCreate.username,
            name = userCreate.name,
            password = userCreate.password,
            phoneNumber = userCreate.phoneNumber,
            description = userCreate.description,
            userType = userCreate.userType,
            gender = userCreate.gender,
            point = 0,
            deleted = false,
            deletedDate = null,
            company = foundCompany
        )

        userRepository.save(user)

        return UserInformation(
            username = user.username,
            name = user.name,
            phoneNumber = user.phoneNumber,
            description = user.description,
            gender = user.gender,
            userType = user.userType,
            point = user.point,
            deleted = user.deleted,
            deletedDate = user.deletedDate,
            companyCode = user.company!!.companyCode,
            companyName = user.company!!.companyName,
        )

    }

    @Transactional
    override fun updateUser(userUpdate: UserUpdate): UserInformation {
        // 회원 정보를 갱신한다.
        val foundUser: UserEntity = userRepository.findByUsername(userUpdate.username) ?: throw ApplicationException(
            ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.message
        )

        val updateUser: UserEntity = foundUser.updateUser(userUpdate)

        userRepository.save(updateUser)

        return UserInformation(
            username = updateUser.username,
            name = updateUser.name,
            phoneNumber = updateUser.phoneNumber,
            description = updateUser.description,
            gender = updateUser.gender,
            userType = updateUser.userType,
            point = updateUser.point,
            deleted = updateUser.deleted,
            deletedDate = updateUser.deletedDate,
            companyCode = updateUser.company!!.companyCode,
            companyName = updateUser.company!!.companyName,
        )
    }

    @Transactional
    override fun deleteUser(username: String): UserInformation {
        // 회원를 삭제한다.
        val foundUser: UserEntity = userRepository.findByUsername(username) ?: throw ApplicationException(
            ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.message
        )
        val deleteUser = foundUser.delete(clockConfig.getClock())

        userRepository.save(deleteUser)

        return UserInformation(
            username = deleteUser.username,
            name = deleteUser.name,
            phoneNumber = deleteUser.phoneNumber,
            description = deleteUser.description,
            gender = deleteUser.gender,
            userType = deleteUser.userType,
            point = deleteUser.point,
            deleted = deleteUser.deleted,
            deletedDate = deleteUser.deletedDate,
            companyCode = deleteUser.company!!.companyCode,
            companyName = deleteUser.company!!.companyName,
        )
    }


}
