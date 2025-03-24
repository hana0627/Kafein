package com.hana.baseproject.api.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.hana.baseproject.api.company.domain.CompanyEntity
import com.hana.baseproject.api.user.controller.request.UserCreate
import com.hana.baseproject.api.user.controller.request.UserUpdate
import com.hana.baseproject.api.user.controller.response.UserInformation
import com.hana.baseproject.api.user.domain.UserEntity
import com.hana.baseproject.api.user.domain.constant.Gender
import com.hana.baseproject.api.user.service.impl.UserServiceImpl
import com.hana.baseproject.core.exception.ApplicationException
import com.hana.baseproject.core.exception.constant.ErrorCode
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import kotlin.test.Test

@WebMvcTest(controllers = [UserController::class])
class UserControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var om: ObjectMapper

    @MockitoBean
    private lateinit var userService: UserServiceImpl


    @Test
    fun 아이디_중복확인에_성공한다() {
        //given
        val user: UserEntity = UserEntity.fixture()
        val companyCode: String = user.company!!.companyCode
        val username: String = "new" + user.username

        given(userService.duplicateUser(username)).willReturn(true)

        //when & then
        mvc.perform(get("/v1/{username}/user/duplicate", username))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.result").value(true))
            .andDo(print())

        then(userService).should().duplicateUser(username)
    }


    @Test
    fun 중복된_아이디_검증시_예외가_발생한다() {
        //given
        val user: UserEntity = UserEntity.fixture()
        val companyCode: String = user.company!!.companyCode
        val usedUsername: String = user.username


        given(userService.duplicateUser(usedUsername)).willThrow(
            ApplicationException(ErrorCode.ALREADY_USED_USERNAME, ErrorCode.ALREADY_USED_USERNAME.message)
        )

        //when & then
        mvc.perform(get("/v1/{username}/user/duplicate", usedUsername))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.CONFLICT.name))
            .andExpect(jsonPath("$.result").value(ErrorCode.ALREADY_USED_USERNAME.message))
            .andDo(print())

        then(userService).should().duplicateUser(usedUsername)
    }

    @Test
    fun 회원_한명_조회에_성공한다() {
        //given
        val user: UserEntity = UserEntity.fixture()
        val userInformation: UserInformation =
            UserInformation.fixture(companyCode = user.company!!.companyCode, companyName = user.company!!.companyName)
        given(userService.findUser(user.company!!.companyCode, user.username)).willReturn(userInformation)

        //when & then
        mvc.perform(get("/v2/{companyCode}/{username}/user", user.company!!.companyCode, user.username))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.result.username").value(userInformation.username))
            .andExpect(jsonPath("$.result.name").value(userInformation.name))
            .andExpect(jsonPath("$.result.description").value(userInformation.description))
            .andExpect(jsonPath("$.result.phoneNumber").value(userInformation.phoneNumber))
            .andExpect(jsonPath("$.result.gender").value(userInformation.gender.name))
            .andExpect(jsonPath("$.result.point").value(userInformation.point))
            .andDo(print())

        then(userService).should().findUser(user.company!!.companyCode, user.username)
    }

    @Test
    fun 없는_회사코드로_회원_한명_조회시_예외가_발생한다() {
        //given
        val wrongCompanyCode: String = "WrongCompanyCode"
        val user: UserEntity = UserEntity.fixture()


        given(userService.findUser(wrongCompanyCode, user.username)).willThrow(
            ApplicationException(ErrorCode.COMPANY_NOT_FOUND, ErrorCode.COMPANY_NOT_FOUND.message)
        )

        //when & then
        mvc.perform(get("/v2/{companyCode}/{username}/user", wrongCompanyCode, user.username))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.NOT_FOUND.name))
            .andExpect(jsonPath("$.result").value(ErrorCode.COMPANY_NOT_FOUND.message))
            .andDo(print())

        then(userService).should().findUser(wrongCompanyCode, user.username)
    }

    @Test
    fun 없는_회원아이디로_회원_한명_조회시_예외가_발생한다() {
        //given
        val wrongUsername: String = "WrongUsername"
        val user: UserEntity = UserEntity.fixture()


        given(userService.findUser(user.company!!.companyCode, wrongUsername)).willThrow(
            ApplicationException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.message)
        )

        //when & then
        mvc.perform(get("/v2/{companyCode}/{username}/user", user.company!!.companyCode, wrongUsername))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.NOT_FOUND.name))
            .andExpect(jsonPath("$.result").value(ErrorCode.USER_NOT_FOUND.message))
            .andDo(print())

        then(userService).should().findUser(user.company!!.companyCode, wrongUsername)
    }

    @Test
    fun 회사코드를_통해_회사에_속한_회원조회가_가능하다() {
        //given
        val company: CompanyEntity = CompanyEntity.fixture()
        val user1: UserEntity = UserEntity.fixture(
            username = "hanana",
            name = "박하나",
            description = "하나다방사장님",
            phoneNumber = "010-1234-5678",
            gender = Gender.F,
            company = company,
        )
        val user2: UserEntity = UserEntity.fixture(
            username = "eunu111",
            name = "차은우",
            description = "하나다방매니저",
            phoneNumber = "010-1111-2222",
            gender = Gender.M,
            company = company,
        )
        val user3: UserEntity = UserEntity.fixture(
            username = "username1234",
            name = "소재호",
            description = "하나다방손님",
            phoneNumber = "010-3333-4444",
            gender = Gender.UN,
            company = company,
        )
        val userInformation1: UserInformation =
            UserInformation.fixture(
                username = user1.username,
                name = user1.name,
                description = user1.description,
                phoneNumber = user1.phoneNumber,
                gender = user1.gender,
                companyCode = company.companyCode,
                companyName = company.companyName,
            )


        val userInformation2: UserInformation =
            UserInformation.fixture(
                username = user2.username,
                name = user2.name,
                description = user2.description,
                phoneNumber = user2.phoneNumber,
                gender = user2.gender,
                companyCode = company.companyCode,
                companyName = company.companyName,
            )


        val userInformation3: UserInformation =
            UserInformation.fixture(
                username = user3.username,
                name = user3.name,
                description = user3.description,
                phoneNumber = user3.phoneNumber,
                gender = user3.gender,
                companyCode = company.companyCode,
                companyName = company.companyName,
            )


        val userInformations = listOf(userInformation1, userInformation2, userInformation3)

        given(userService.findUsersByCompanyCode(company.companyCode)).willReturn(
            userInformations
        )

        //when & then
        mvc.perform(get("/v2/{companyCode}/users", company.companyCode))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.result.size()").value(userInformations.size))
            .andExpect(jsonPath("$.result[0].username").value(userInformations[0].username))
            .andExpect(jsonPath("$.result[0].name").value(userInformations[0].name))
            .andExpect(jsonPath("$.result[0].description").value(userInformations[0].description))
            .andExpect(jsonPath("$.result[0].phoneNumber").value(userInformations[0].phoneNumber))
            .andExpect(jsonPath("$.result[0].gender").value(userInformations[0].gender.name))
            .andExpect(jsonPath("$.result[0].point").value(userInformations[0].point))

            .andExpect(jsonPath("$.result[1].username").value(userInformations[1].username))
            .andExpect(jsonPath("$.result[1].name").value(userInformations[1].name))
            .andExpect(jsonPath("$.result[1].description").value(userInformations[1].description))

            .andExpect(jsonPath("$.result[2].username").value(userInformations[2].username))
            .andExpect(jsonPath("$.result[2].name").value(userInformations[2].name))
            .andExpect(jsonPath("$.result[2].description").value(userInformations[2].description))

            .andDo(print())

        then(userService).should().findUsersByCompanyCode(company.companyCode)
    }


    @Test
    fun 모든_회원조회에_성공한다() {
        //given
        val company1: CompanyEntity = CompanyEntity.fixture()
        val company2: CompanyEntity = CompanyEntity.fixture(companyCode = "A0000002", companyName = "단비다방")
        val user1: UserEntity = UserEntity.fixture(
            username = "hanana",
            name = "박하나",
            description = "하나다방사장님",
            phoneNumber = "010-1234-5678",
            gender = Gender.F,
            company = company1,
        )
        val user2: UserEntity = UserEntity.fixture(
            username = "eunu111",
            name = "차은우",
            description = "하나다방매니저",
            phoneNumber = "010-1111-2222",
            gender = Gender.M,
            company = company1,
        )
        val user3: UserEntity = UserEntity.fixture(
            username = "username1234",
            name = "소재호",
            description = "하나다방손님",
            phoneNumber = "010-3333-4444",
            gender = Gender.UN,
            company = company2,
        )
        val userInformation1: UserInformation =
            UserInformation.fixture(
                username = user1.username,
                name = user1.name,
                description = user1.description,
                phoneNumber = user1.phoneNumber,
                gender = user1.gender,
            )


        val userInformation2: UserInformation =
            UserInformation.fixture(
                username = user2.username,
                name = user2.name,
                description = user2.description,
                phoneNumber = user2.phoneNumber,
                gender = user2.gender,
                companyCode = user2.company!!.companyCode,
                companyName = user2.company!!.companyName,
            )


        val userInformation3: UserInformation =
            UserInformation.fixture(
                username = user3.username,
                name = user3.name,
                description = user3.description,
                phoneNumber = user3.phoneNumber,
                gender = user3.gender,
                companyCode = user3.company!!.companyCode,
                companyName = user3.company!!.companyName,
            )


        val userInformations = listOf(userInformation1, userInformation2, userInformation3)

        given(userService.findUsers()).willReturn(
            userInformations
        )

        //when & then
        mvc.perform(get("/v2/users"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.result.size()").value(userInformations.size))
            .andExpect(jsonPath("$.result[0].username").value(userInformations[0].username))
            .andExpect(jsonPath("$.result[0].name").value(userInformations[0].name))
            .andExpect(jsonPath("$.result[0].description").value(userInformations[0].description))
            .andExpect(jsonPath("$.result[0].phoneNumber").value(userInformations[0].phoneNumber))
            .andExpect(jsonPath("$.result[0].gender").value(userInformations[0].gender.name))
            .andExpect(jsonPath("$.result[0].point").value(userInformations[0].point))
            .andExpect(jsonPath("$.result[0].companyCode").value(userInformations[0].companyCode))
            .andExpect(jsonPath("$.result[0].companyName").value(userInformations[0].companyName))

            .andExpect(jsonPath("$.result[1].username").value(userInformations[1].username))
            .andExpect(jsonPath("$.result[1].name").value(userInformations[1].name))
            .andExpect(jsonPath("$.result[1].description").value(userInformations[1].description))
            .andExpect(jsonPath("$.result[1].companyCode").value(userInformations[1].companyCode))
            .andExpect(jsonPath("$.result[1].companyName").value(userInformations[1].companyName))

            .andExpect(jsonPath("$.result[2].username").value(userInformations[2].username))
            .andExpect(jsonPath("$.result[2].name").value(userInformations[2].name))
            .andExpect(jsonPath("$.result[2].description").value(userInformations[2].description))
            .andExpect(jsonPath("$.result[2].companyCode").value(userInformations[2].companyCode))
            .andExpect(jsonPath("$.result[2].companyName").value(userInformations[2].companyName))

            .andDo(print())

        then(userService).should().findUsers()
    }

    @Test
    fun 없는_회사코드로_회원목록_조회시_예외가_발생한다() {
        //given
        val wrongCompanyCode: String = "WrongCompanyCode"
        val user: UserEntity = UserEntity.fixture()


        given(userService.findUsersByCompanyCode(wrongCompanyCode)).willThrow(
            ApplicationException(ErrorCode.COMPANY_NOT_FOUND, ErrorCode.COMPANY_NOT_FOUND.message)
        )

        //when & then
        mvc.perform(get("/v2/{companyCode}/users", wrongCompanyCode, user.username))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.NOT_FOUND.name))
            .andExpect(jsonPath("$.result").value(ErrorCode.COMPANY_NOT_FOUND.message))
            .andDo(print())

        then(userService).should().findUsersByCompanyCode(wrongCompanyCode)
    }


    @Test
    fun 회원가입에_성공한다() {
        //given
        val company: CompanyEntity = CompanyEntity.fixture()
        val userCreate: UserCreate = UserCreate.fixture()
        val userInformation: UserInformation =
            UserInformation.fixture(companyCode = company.companyCode, companyName = company.companyName)

        given(userService.createUser(userCreate)).willReturn(userInformation)

        val json = om.writeValueAsString(userCreate)

        //when & then
        mvc.perform(
            post("/v1/user")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.result.username").value(userInformation.username))
            .andExpect(jsonPath("$.result.name").value(userInformation.name))
            .andExpect(jsonPath("$.result.description").value(userInformation.description))
            .andExpect(jsonPath("$.result.phoneNumber").value(userInformation.phoneNumber))
            .andExpect(jsonPath("$.result.gender").value(userInformation.gender.name))
            .andExpect(jsonPath("$.result.point").value(userInformation.point))
            .andExpect(jsonPath("$.result.companyCode").value(userInformation.companyCode))
            .andExpect(jsonPath("$.result.companyName").value(userInformation.companyName))
            .andDo(print())

        then(userService).should().createUser(userCreate)
    }


    @Test
    fun 중복된_아이디로_회원가입시_예외가_발생한다() {
        //given
        val userCreate: UserCreate = UserCreate.fixture(username = "wrongUsername")

        given(userService.createUser(userCreate)).willThrow(
            ApplicationException(ErrorCode.ALREADY_USED_USERNAME, ErrorCode.ALREADY_USED_USERNAME.message)
        )
        val json = om.writeValueAsString(userCreate)

        //when & then
        mvc.perform(
            post("/v1/user")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.CONFLICT.name))
            .andExpect(jsonPath("$.result").value(ErrorCode.ALREADY_USED_USERNAME.message))
            .andDo(print())

        then(userService).should().createUser(userCreate)
    }


    @Test
    fun 회원정보_수정에_성공한다() {
        //given
        val company: CompanyEntity = CompanyEntity.fixture()
        val userUpdate: UserUpdate = UserUpdate.fixture()
        val userInformation: UserInformation = UserInformation.fixture(
            username = userUpdate.username,
            name = userUpdate.name,
            phoneNumber = userUpdate.phoneNumber,
            description = userUpdate.description,
            userType = userUpdate.userType,
            companyCode = company.companyCode,
            companyName = company.companyName
        )

        given(userService.updateUser(userUpdate)).willReturn(userInformation)

        val json = om.writeValueAsString(userUpdate)

        //when & then
        mvc.perform(
            patch("/v2/user")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.result.username").value(userInformation.username))
            .andExpect(jsonPath("$.result.name").value(userInformation.name))
            .andExpect(jsonPath("$.result.description").value(userInformation.description))
            .andExpect(jsonPath("$.result.phoneNumber").value(userInformation.phoneNumber))
            .andExpect(jsonPath("$.result.gender").value(userInformation.gender.name))
            .andExpect(jsonPath("$.result.point").value(userInformation.point))
            .andExpect(jsonPath("$.result.companyCode").value(userInformation.companyCode))
            .andExpect(jsonPath("$.result.companyName").value(userInformation.companyName))
            .andDo(print())

        then(userService).should().updateUser(userUpdate)
    }


    @Test
    fun 없는_회원에_대한_정보수정시_예외가_발생한다() {
        //given
        val userUpdate: UserUpdate = UserUpdate.fixture(username = "wrongUsername")

        given(userService.updateUser(userUpdate)).willThrow(
            ApplicationException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.message)
        )
        val json = om.writeValueAsString(userUpdate)

        //when & then
        mvc.perform(
            patch("/v2/user")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.NOT_FOUND.name))
            .andExpect(jsonPath("$.result").value(ErrorCode.USER_NOT_FOUND.message))
            .andDo(print())

        then(userService).should().updateUser(userUpdate)
    }

    @Test
    fun 회원정보_삭제에_성공한다() {
        //given
        val username: String = "hanana"
        val userInformation: UserInformation = UserInformation.fixture(username = username)

        given(userService.deleteUser(username)).willReturn(userInformation)


        //when & then
        mvc.perform(
            delete("/v2/{username}/user",username))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.result.username").value(userInformation.username))
            .andExpect(jsonPath("$.result.name").value(userInformation.name))
            .andExpect(jsonPath("$.result.description").value(userInformation.description))
            .andExpect(jsonPath("$.result.phoneNumber").value(userInformation.phoneNumber))
            .andExpect(jsonPath("$.result.gender").value(userInformation.gender.name))
            .andExpect(jsonPath("$.result.point").value(userInformation.point))
            .andExpect(jsonPath("$.result.companyCode").value(userInformation.companyCode))
            .andExpect(jsonPath("$.result.companyName").value(userInformation.companyName))
            .andDo(print())

        then(userService).should().deleteUser(username)
    }


    @Test
    fun 없는_회원에_대한_정보수정시_예외가_발생한2다() {
        //given
        val wrongUsername = "wrongUsername"

        given(userService.deleteUser(wrongUsername)).willThrow(
            ApplicationException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.message)
        )

        //when & then
        mvc.perform(
            delete("/v2/{username}/user",wrongUsername))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.NOT_FOUND.name))
            .andExpect(jsonPath("$.result").value(ErrorCode.USER_NOT_FOUND.message))
            .andDo(print())

        then(userService).should().deleteUser(wrongUsername)
    }



}
