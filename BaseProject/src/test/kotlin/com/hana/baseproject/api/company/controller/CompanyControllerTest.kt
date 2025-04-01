package com.hana.baseproject.api.company.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.hana.baseproject.api.company.controller.reponse.CompanyInformation
import com.hana.baseproject.api.company.controller.request.CompanyCreate
import com.hana.baseproject.api.company.controller.request.CompanyUpdate
import com.hana.baseproject.api.company.domain.CompanyEntity
import com.hana.baseproject.api.company.service.impl.CompanyServiceImpl
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
import java.time.LocalDateTime
import kotlin.test.Test

@WebMvcTest(controllers = [CompanyController::class])
class CompanyControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var om: ObjectMapper

    @MockitoBean
    private lateinit var companyService: CompanyServiceImpl

    @Test
    fun 회사코드로_회사조회가_가능하다() {
        //given
        val company = CompanyEntity.fixture(companyCode = "A0000001", companyName = "하나다방")
        val companyInformation = CompanyInformation.fixture()
        val companyCode: String = company.companyCode


        given(companyService.getCompany(companyCode)).willReturn(companyInformation)

        //when & then
        mvc.perform(get("/v1/{companyCode}/company", companyCode))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.result.companyCode").value(company.companyCode))
            .andExpect(jsonPath("$.result.companyName").value(company.companyName))
            .andDo(print())

        then(companyService).should().getCompany(companyCode)
    }

    @Test
    fun 없는_회사코드로_조회시_예외가_발생한다() {
        //given
//        val company = CompanyEntity.fixture(companyCode = "A0000001")
        val company = CompanyEntity.fixture()
        val wrongCompanyCode: String = "WrongCompanyCode"

        given(companyService.getCompany(wrongCompanyCode)).willThrow(
            ApplicationException(ErrorCode.COMPANY_NOT_FOUND, ErrorCode.COMPANY_NOT_FOUND.message)
        )

        //when & then
        mvc.perform(get("/v1/{companyCode}/company", wrongCompanyCode))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.NOT_FOUND.name))
            .andExpect(jsonPath("$.result").value(ErrorCode.COMPANY_NOT_FOUND.message))
            .andDo(print())

        then(companyService).should().getCompany(wrongCompanyCode)
    }


    @Test
    fun 회사등록이_가능하다() {
        //given
//        val companyCreate: CompanyCreate = CompanyCreate.fixture(companyName = "하나다방")
        val companyCreate: CompanyCreate = CompanyCreate.fixture()
        val companyEntity: CompanyEntity = CompanyEntity.fixture(companyCode = "A0000001", companyName = "하나다방")
        val companyInformation: CompanyInformation = CompanyInformation.fixture()

        given(companyService.createCompany(companyCreate)).willReturn(companyInformation)

        val json: String = om.writeValueAsString(companyCreate)

        //when & then
        mvc.perform(
            post("/v2/company")
                .contentType(APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.result.companyCode").value(companyInformation.companyCode))
            .andExpect(jsonPath("$.result.companyName").value(companyInformation.companyName))
            .andDo(print())

        then(companyService).should().createCompany(companyCreate)
    }

    @Test
    fun 회사정보_수정이_가능하다() {
        //given
//        val companyUpdate: CompanyUpdate = CompanyUpdate.fixture(companyCode = "A0000001", companyName = "신세경다방")
        val companyUpdate: CompanyUpdate = CompanyUpdate.fixture()
        val companyInformation: CompanyInformation = CompanyInformation.fixture(companyName = companyUpdate.companyName)

        given(companyService.updateCompany(companyUpdate)).willReturn(companyInformation)

        val json: String = om.writeValueAsString(companyUpdate)

        //when & then
        mvc.perform(
            patch("/v2/company")
                .contentType(APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.result.companyCode").value(companyUpdate.companyCode))
            .andExpect(jsonPath("$.result.companyName").value(companyUpdate.companyName))
            .andDo(print())

        then(companyService).should().updateCompany(companyUpdate)
    }

    @Test
    fun 존재하지_않는_회사는_수정이_불가능하다() {
        //given
        val companyUpdate: CompanyUpdate = CompanyUpdate.fixture(companyCode = "WrongCompanyCode", companyName = "신세경다방")

        given(companyService.updateCompany(companyUpdate)).willThrow(
            ApplicationException(ErrorCode.COMPANY_NOT_FOUND, ErrorCode.COMPANY_NOT_FOUND.message)
        )

        val json: String = om.writeValueAsString(companyUpdate)

        //when & then
        mvc.perform(
            patch("/v2/company")
                .contentType(APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.NOT_FOUND.name))
            .andExpect(jsonPath("$.result").value(ErrorCode.COMPANY_NOT_FOUND.message))
            .andDo(print())

        then(companyService).should().updateCompany(companyUpdate)
    }

    @Test
    fun 회사_삭제가_가능하다() {
        //given
        val deletedDate: LocalDateTime = LocalDateTime.of(2025,4,1,18,30,30)
        val companyEntity: CompanyEntity = CompanyEntity.fixture(companyCode = "A0000001", companyName = "하나다방")
        val companyInformation: CompanyInformation = CompanyInformation.fixture(
            companyCode = companyEntity.companyCode,
            companyName = companyEntity.companyName,
            deleted = true,
            deletedDate = deletedDate
        )
        val companyCode: String = companyEntity.companyCode

        given(companyService.deleteCompany(companyCode)).willReturn(companyInformation)

        //when & then
        mvc.perform(
            delete("/v2/{companyCode}/company",companyCode))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.result.companyCode").value(companyInformation.companyCode))
            .andExpect(jsonPath("$.result.companyName").value(companyInformation.companyName))
            .andExpect(jsonPath("$.result.deleted").value(companyInformation.deleted))
            .andExpect(jsonPath("$.result.deletedDate").value(companyInformation.deletedDate.toString()))
            .andDo(print())

        then(companyService).should().deleteCompany(companyCode)
    }

    @Test
    fun 존재하지_않는_회사는_삭제가_불가능하다() {
        //given
        val companyEntity: CompanyEntity = CompanyEntity.fixture(companyCode = "A0000001", companyName = "하나다방")
        val wrongCompanyCode: String = companyEntity.companyCode

        given(companyService.deleteCompany(wrongCompanyCode)).willThrow(
            ApplicationException(ErrorCode.COMPANY_NOT_FOUND, ErrorCode.COMPANY_NOT_FOUND.message)
        )

        //when & then
        mvc.perform(
            delete("/v2/{companyCode}/company",wrongCompanyCode))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.NOT_FOUND.name))
            .andExpect(jsonPath("$.result").value(ErrorCode.COMPANY_NOT_FOUND.message))
            .andDo(print())

        then(companyService).should().deleteCompany(wrongCompanyCode)
    }

}
