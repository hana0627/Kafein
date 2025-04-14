package com.hana.baseproject.api.user.service.impl

import com.hana.baseproject.api.company.domain.CompanyEntity
import com.hana.baseproject.api.company.repository.CompanyRepository
import com.hana.baseproject.api.user.controller.request.UserCreate
import com.hana.baseproject.api.user.controller.request.UserUpdate
import com.hana.baseproject.api.user.controller.response.UserInformation
import com.hana.baseproject.api.user.domain.UserEntity
import com.hana.baseproject.api.user.domain.constant.Gender
import com.hana.baseproject.api.user.domain.constant.UserType
import com.hana.baseproject.api.user.repository.UserRepository
import com.hana.baseproject.core.config.ClockConfig
import com.hana.baseproject.core.exception.ApplicationException
import com.hana.baseproject.core.exception.constant.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


@ExtendWith(MockitoExtension::class)
class UserServiceImplTest(
    @Mock private val userRepository: UserRepository,
    @Mock private val companyRepository: CompanyRepository,
    @Mock private val clockConfig: ClockConfig,
) {

    @InjectMocks
    private val userService: UserServiceImpl = UserServiceImpl(userRepository, companyRepository, clockConfig)

    @Test
    fun 중복아이디_검증시_중복된_username이라면_false를_반환한다() {
        //given
        val user: UserEntity = UserEntity.fixture()

        given(userRepository.findByUsername(user.username)).willReturn(user)

        //when
        val result: Boolean = userService.duplicateUser(user.username)

        //then
        then(userRepository).should().findByUsername(user.username)
        assertThat(result).isEqualTo(false)
    }

    @Test
    fun 중복아이디_검증시_중복되지_않은_username이라면_ture를_반환한다() {
        //given
        val user: UserEntity = UserEntity.fixture()

        given(userRepository.findByUsername(user.username)).willReturn(null)

        //when
        val result: Boolean = userService.duplicateUser(user.username)

        //then
        then(userRepository).should().findByUsername(user.username)
        assertThat(result).isEqualTo(true)
    }

    @Test
    fun companyCode와_username으로_회원_한명_조회에_성공한다() {
        //given
        val company: CompanyEntity = CompanyEntity.fixture()
        val user: UserEntity = UserEntity.fixture(
            company = company
        )

        given(companyRepository.findByCompanyCode(companyCode = company.companyCode)).willReturn(company)
        given(userRepository.findByUsername(user.username)).willReturn(user)

        //when
        val result: UserInformation = userService.findUser(companyCode = company.companyCode, username = user.username)

        //then
        then(companyRepository).should().findByCompanyCode(company.companyCode)
        then(userRepository).should().findByUsername(user.username)

        assertThat(result.username).isEqualTo(user.username)
        assertThat(result.name).isEqualTo(user.name)
        assertThat(result.phoneNumber).isEqualTo(user.phoneNumber)
        assertThat(result.description).isEqualTo(user.description)
        assertThat(result.gender).isEqualTo(user.gender)
        assertThat(result.userType).isEqualTo(user.userType)
        assertThat(result.point).isEqualTo(user.point)
        assertThat(result.deleted).isEqualTo(user.deleted)
        assertThat(result.deletedDate).isEqualTo(user.deletedDate)
        assertThat(result.companyCode).isEqualTo(user.company!!.companyCode)
        assertThat(result.companyName).isEqualTo(user.company!!.companyName)
    }

    @Test
    fun 없는_companyCode와_유효한_username으로_회원_한명_조회시_예외가_발생한다() {
        //given
        val company: CompanyEntity = CompanyEntity.fixture()
        val user: UserEntity = UserEntity.fixture(
            company = company
        )

        given(companyRepository.findByCompanyCode(companyCode = company.companyCode)).willReturn(null)

        //when
        val result = assertThrows<ApplicationException> {
            userService.findUser(
                companyCode = company.companyCode,
                username = user.username
            )
        }

        //then
        then(companyRepository).should().findByCompanyCode(company.companyCode)
        then(userRepository).shouldHaveNoInteractions()

        assertThat(result.getErrorCode).isEqualTo(ErrorCode.COMPANY_NOT_FOUND)
        assertThat(result.getMessage).isEqualTo(ErrorCode.COMPANY_NOT_FOUND.message)
    }

    @Test
    fun 유효한_companyCode와_없는_username으로_회원_한명_조회시_예외가_발생한다() {
        //given
        val company: CompanyEntity = CompanyEntity.fixture()
        val user: UserEntity = UserEntity.fixture(
            company = company
        )

        given(companyRepository.findByCompanyCode(companyCode = company.companyCode)).willReturn(company)
        given(userRepository.findByUsername(user.username)).willReturn(null)

        //when
        val result = assertThrows<ApplicationException> {
            userService.findUser(
                companyCode = company.companyCode,
                username = user.username
            )
        }

        //then
        then(companyRepository).should().findByCompanyCode(company.companyCode)
        then(userRepository).should().findByUsername(user.username)

        assertThat(result.getErrorCode).isEqualTo(ErrorCode.USER_NOT_FOUND)
        assertThat(result.getMessage).isEqualTo(ErrorCode.USER_NOT_FOUND.message)
    }

    @Test
    fun companyCode를_이용하여_회사에_속한_유저의_목록을_조회한다() {
        //given

        val company: CompanyEntity = CompanyEntity.fixture()

        val user1: UserEntity = UserEntity.fixture(
            company = company
        )

        val user2: UserEntity = UserEntity.fixture(
            username = "username1234",
            name = "신세경",
            password = "1q2w3e4r",
            description = "우리가게 매니저",
            phoneNumber = "010-1111-2222",
            userType = UserType.MANAGER,
            gender = Gender.F,
            company = company,
        )

        val user3: UserEntity = UserEntity.fixture(
            username = "user1111",
            name = "윤시윤",
            password = "fdksjn123",
            description = "",
            phoneNumber = "010-2222-3333",
            userType = UserType.CUSTOMER,
            gender = Gender.M,
            company = company,
        )
        val users: List<UserEntity> = mutableListOf(user1, user2, user3)
        company.userEntity.addAll(users)

        given(companyRepository.findByCompanyCode(company.companyCode)).willReturn(company)

        //when
        val result: List<UserInformation> = userService.findUsersByCompanyCode(company.companyCode)

        //then
        then(companyRepository).should().findByCompanyCode(company.companyCode)

        assertThat(result.size).isEqualTo(users.size)
        assertThat(result[0].username).isEqualTo(user1.username)
        assertThat(result[0].name).isEqualTo(user1.name)
        assertThat(result[0].phoneNumber).isEqualTo(user1.phoneNumber)
        assertThat(result[0].description).isEqualTo(user1.description)
        assertThat(result[0].gender).isEqualTo(user1.gender)
        assertThat(result[0].userType).isEqualTo(user1.userType)
        assertThat(result[0].point).isEqualTo(user1.point)
        assertThat(result[0].deleted).isEqualTo(user1.deleted)
        assertThat(result[0].deletedDate).isEqualTo(user1.deletedDate)
        assertThat(result[0].companyCode).isEqualTo(user1.company!!.companyCode)

    }

    @Test
    fun 존재하지_않는_companyCode를_이용하여_회사에_속한_유저의_목록을_조회시_예외가_발생한다() {
        //given
        val company: CompanyEntity = CompanyEntity.fixture()

        given(companyRepository.findByCompanyCode(companyCode = company.companyCode)).willReturn(null)

        //when
        val result =
            assertThrows<ApplicationException> { userService.findUsersByCompanyCode(companyCode = company.companyCode) }

        //then
        then(companyRepository).should().findByCompanyCode(company.companyCode)

        assertThat(result.getErrorCode).isEqualTo(ErrorCode.COMPANY_NOT_FOUND)
        assertThat(result.getMessage).isEqualTo(ErrorCode.COMPANY_NOT_FOUND.message)
    }

    @Test
    fun 모든_회원을_조회한다() {
        //given
        val company: CompanyEntity = CompanyEntity.fixture()

        val user1: UserEntity = UserEntity.fixture(
            company = company
        )

        val user2: UserEntity = UserEntity.fixture(
            username = "username1234",
            name = "신세경",
            password = "1q2w3e4r",
            description = "우리가게 매니저",
            phoneNumber = "010-1111-2222",
            userType = UserType.MANAGER,
            gender = Gender.F,
            company = company,
        )

        val user3: UserEntity = UserEntity.fixture(
            username = "user1111",
            name = "윤시윤",
            password = "fdksjn123",
            description = "",
            phoneNumber = "010-2222-3333",
            userType = UserType.CUSTOMER,
            gender = Gender.M,
            company = company,
        )
        val users: List<UserEntity> = mutableListOf(user1, user2, user3)
        company.userEntity.addAll(users)

        given(userRepository.findAll()).willReturn(users)


        //when
        val result: List<UserInformation> = userService.findUsers()

        //then
        then(userRepository).should().findAll()
        assertThat(result.size).isEqualTo(users.size)
        assertThat(result[0].username).isEqualTo(user1.username)
        assertThat(result[0].name).isEqualTo(user1.name)
        assertThat(result[1].phoneNumber).isEqualTo(user2.phoneNumber)
        assertThat(result[1].description).isEqualTo(user2.description)
        assertThat(result[1].gender).isEqualTo(user2.gender)
        assertThat(result[1].userType).isEqualTo(user2.userType)
        assertThat(result[2].point).isEqualTo(user3.point)
        assertThat(result[2].deleted).isEqualTo(user3.deleted)
        assertThat(result[2].deletedDate).isEqualTo(user3.deletedDate)
        assertThat(result[2].companyCode).isEqualTo(user3.company!!.companyCode)
    }

    @Test
    fun 회원가입에_성공한다() {
        //given
        val company: CompanyEntity = CompanyEntity.fixture()
        val userCreate: UserCreate = UserCreate.fixture(
            companyCode = company.companyCode,
        )

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
            company = company
        )

        given(companyRepository.findByCompanyCode(userCreate.companyCode)).willReturn(company)
        given(userRepository.findByUsername(userCreate.username)).willReturn(null)
        given(userRepository.save(user)).willReturn(user)

        //when
        val result: UserInformation = userService.createUser(userCreate)

        //then
        then(companyRepository).should().findByCompanyCode(company.companyCode)
        then(userRepository).should().findByUsername(userCreate.username)
        then(userRepository).should().save(user)


        assertThat(result.username).isEqualTo(userCreate.username)
        assertThat(result.name).isEqualTo(userCreate.name)
        assertThat(result.phoneNumber).isEqualTo(userCreate.phoneNumber)
        assertThat(result.description).isEqualTo(userCreate.description)
        assertThat(result.gender).isEqualTo(userCreate.gender)
        assertThat(result.userType).isEqualTo(userCreate.userType)
        assertThat(result.point).isEqualTo(0)
        assertThat(result.deleted).isEqualTo(false)
        assertThat(result.deletedDate).isNull()
        assertThat(result.companyCode).isEqualTo(company.companyCode)
        assertThat(result.companyName).isEqualTo(company.companyName)
    }

    @Test
    fun 잘못된_회사코드_입력시_회원가입에_실패한다() {
        //given
        val company: CompanyEntity = CompanyEntity.fixture()
        val userCreate: UserCreate = UserCreate.fixture(
            companyCode = "wrongCompanyCode",
        )

        given(companyRepository.findByCompanyCode(userCreate.companyCode)).willReturn(null)

        //when
        val result = assertThrows<ApplicationException> {
            userService.createUser(userCreate)
        }

        //then
        then(companyRepository).should().findByCompanyCode(userCreate.companyCode)
        then(userRepository).shouldHaveNoInteractions()

        assertThat(result.getErrorCode).isEqualTo(ErrorCode.COMPANY_NOT_FOUND)
        assertThat(result.getMessage).isEqualTo(ErrorCode.COMPANY_NOT_FOUND.message)
    }

    @Test
    fun username_중복시_회원가입에_실패한다() {
        //given
        val company: CompanyEntity = CompanyEntity.fixture()
        val userCreate: UserCreate = UserCreate.fixture(
            username = "alreadyUserUsername",
            companyCode = company.companyCode,
        )

        val user: UserEntity = UserEntity.fixture(
            username = userCreate.username
        )

        given(companyRepository.findByCompanyCode(userCreate.companyCode)).willReturn(company)
        given(userRepository.findByUsername(userCreate.username)).willReturn(user)

        //when
        val result = assertThrows<ApplicationException> {
            userService.createUser(userCreate)
        }

        //then
        then(companyRepository).should().findByCompanyCode(userCreate.companyCode)
        then(userRepository).should().findByUsername(userCreate.username)

        assertThat(result.getErrorCode).isEqualTo(ErrorCode.ALREADY_USED_USERNAME)
        assertThat(result.getMessage).isEqualTo(ErrorCode.ALREADY_USED_USERNAME.message)
    }

    @Test
    fun 회원정보_갱신에_성공한다() {
        //given
        val userUpdate: UserUpdate = UserUpdate.fixture()
        val user: UserEntity = UserEntity.fixture(
            username = userUpdate.username
        )

        val updatedUser = user.copy(
            name = userUpdate.name,
            phoneNumber = userUpdate.phoneNumber,
            description = userUpdate.description,
            userType = userUpdate.userType
        )

        given(userRepository.findByUsername(userUpdate.username)).willReturn(user)
        given(userRepository.save(updatedUser)).willReturn(updatedUser)

        //when
        val result: UserInformation = userService.updateUser(userUpdate)

        //then
        then(userRepository).should().findByUsername(userUpdate.username)
        then(userRepository).should().save(updatedUser)

        assertThat(result.username).isEqualTo(userUpdate.username)
        assertThat(result.name).isEqualTo(userUpdate.name)
        assertThat(result.phoneNumber).isEqualTo(userUpdate.phoneNumber)
        assertThat(result.description).isEqualTo(userUpdate.description)
        assertThat(result.userType).isEqualTo(userUpdate.userType)
    }

    @Test
    fun 존재하지_않는_회원에_대해서_갱신시도시_예외가_발생한다() {
        //given
        val userUpdate: UserUpdate = UserUpdate.fixture(
            username = "wrongUsername",
        )

        given(userRepository.findByUsername(userUpdate.username)).willReturn(null)

        //when
        val result = assertThrows<ApplicationException> {
            userService.updateUser(userUpdate)
        }

        //then
        then(userRepository).should().findByUsername(userUpdate.username)

        assertThat(result.getErrorCode).isEqualTo(ErrorCode.USER_NOT_FOUND)
        assertThat(result.getMessage).isEqualTo(ErrorCode.USER_NOT_FOUND.message)

    }

    @Test
    fun 회원삭제에_성공한다() {
        //given
        given(clockConfig.getClock()).willReturn(Clock.fixed(Instant.parse("2025-04-02T12:30:30Z"), ZoneId.systemDefault()))

        val user: UserEntity = UserEntity.fixture()
        val deletedUser: UserEntity = user.copy(
            deleted = true,
            deletedDate = LocalDateTime.now(clockConfig.getClock())
        )

        given(userRepository.findByUsername(user.username)).willReturn(user)
        given(userRepository.save(deletedUser)).willReturn(deletedUser)

        //when
        val result: UserInformation = userService.deleteUser(user.username)

        //then
        then(userRepository).should().findByUsername(user.username)
        then(userRepository).should().save(deletedUser)

        assertThat(result.username).isEqualTo(deletedUser.username)
        assertThat(result.deleted).isEqualTo(deletedUser.deleted)
        assertThat(result.deletedDate).isEqualTo(deletedUser.deletedDate)
    }

    @Test
    fun 존재하지_않는_회원에_대해서_삭제_시도시_예외가_발생한다() {
        //given
        val wrongUsername = "wrongUsername"

        given(userRepository.findByUsername(wrongUsername)).willReturn(null)
        wrongUsername
        //when
        val result = assertThrows<ApplicationException> {
            userService.deleteUser(wrongUsername)
        }

        //then
        then(userRepository).should().findByUsername(wrongUsername)

        assertThat(result.getErrorCode).isEqualTo(ErrorCode.USER_NOT_FOUND)
        assertThat(result.getMessage).isEqualTo(ErrorCode.USER_NOT_FOUND.message)
    }


}

