package com.hana.baseproject.api.company.service.impl

import com.hana.baseproject.api.company.controller.reponse.CompanyInformation
import com.hana.baseproject.api.company.controller.request.CompanyCreate
import com.hana.baseproject.api.company.controller.request.CompanyUpdate
import com.hana.baseproject.api.company.domain.CompanyEntity
import com.hana.baseproject.api.company.repository.CompanyRepository
import com.hana.baseproject.core.exception.ApplicationException
import com.hana.baseproject.core.exception.constant.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.dao.DataIntegrityViolationException

@ExtendWith(MockitoExtension::class)
class CompanyServiceImplTest (
    @Mock private val companyRepository: CompanyRepository,
){

    @InjectMocks
    private val companyService: CompanyServiceImpl = CompanyServiceImpl(companyRepository)

    @Test
    fun 새로운_회사코드_발급에_성공한다() {
        //given
        given(companyRepository.getMaxCompanyCode()).willReturn("A0000001")


        //when
        val result: String = companyService.createCompanyCode()

        //then
        then(companyRepository).should().getMaxCompanyCode()
        assertThat(result).isEqualTo("A0000002")
    }

    @Test
    fun 새로운_회사코드_발급에_성공한다_최소실행() {
        //given
        given(companyRepository.getMaxCompanyCode()).willReturn(null)


        //when
        val result: String = companyService.createCompanyCode()

        //then
        then(companyRepository).should().getMaxCompanyCode()
        assertThat(result).isEqualTo("A0000001")
    }


    @Test
    fun 회사코드로_회사정보_조회가_가능하다() {
        //given
        val company = CompanyEntity.fixture(companyCode = "A0000001")
        val companyCode: String = company.companyCode

        given(companyRepository.findByCompanyCode(companyCode)).willReturn(company)

        //when
        val result: CompanyInformation = companyService.getCompany(companyCode)

        //then
        then(companyRepository).should().findByCompanyCode(companyCode)
        assertThat(result.companyCode).isEqualTo(company.companyCode)
        assertThat(result.companyCode).isEqualTo(company.companyCode)
    }



    @Test
    fun 없는_회사코드로_회사정보_조회시_예외가_발생한다() {
        //given
        val company = CompanyEntity.fixture(companyCode = "A0000001")
        val wrongCompanyCode: String = "WrongCompanyCode"

        given(companyRepository.findByCompanyCode(wrongCompanyCode)).willReturn(null)

        //when
        val result = assertThrows<ApplicationException> { companyService.getCompany(wrongCompanyCode)}

        //then
        then(companyRepository).should().findByCompanyCode(wrongCompanyCode)
        assertThat(result.getErrorCode).isEqualTo(ErrorCode.COMPANY_NOT_FOUND)
        assertThat(result.getMessage).isEqualTo(ErrorCode.COMPANY_NOT_FOUND.message)
    }


    @Test
    fun 회사명을_입력하면_회사등록에_성공한다() {
        //given
        val nextCompanyCode: String = "A0000002"
        val companyCreate: CompanyCreate = CompanyCreate.fixture()
        val companyEntity: CompanyEntity = CompanyEntity.fixture(companyCode = nextCompanyCode, companyName = companyCreate.companyName)
        given(companyRepository.getMaxCompanyCode()).willReturn("A0000001")
        given(companyRepository.save(companyEntity)).willReturn(CompanyEntity.fixture(companyCode = nextCompanyCode, companyName = companyCreate.companyName, id=1L))

        //when
        val result: CompanyInformation = companyService.createCompany(companyCreate)

        //then
        then(companyRepository).should().getMaxCompanyCode()
        then(companyRepository).should().save(companyEntity)
        assertThat(result).isEqualTo(CompanyInformation.fixture(companyCode = nextCompanyCode))
    }

    @Test
    fun 만약_등록시점에_회사코드가_중복되면_회사코드_생성_과정을_재수행한다() {
        //given
        val nextCompanyCode1: String = "A0000002"
        val nextCompanyCode2: String = "A0000003"
        val companyCreate: CompanyCreate = CompanyCreate.fixture()
        val companyEntity1: CompanyEntity = CompanyEntity.fixture(companyCode = nextCompanyCode1, companyName = companyCreate.companyName)
        val companyEntity2: CompanyEntity = CompanyEntity.fixture(companyCode = nextCompanyCode2, companyName = companyCreate.companyName)
        given(companyRepository.getMaxCompanyCode()).willReturn("A0000001", "A0000002")

        given(companyRepository.save(companyEntity1))
            .willThrow(DataIntegrityViolationException("Duplicate companyCode"))
            .willAnswer { invocation -> invocation.getArgument(0) }

        given(companyRepository.save(companyEntity2)).willReturn(CompanyEntity.fixture(companyCode = nextCompanyCode2, companyName = companyCreate.companyName, id=1L))


        //when
        val result: CompanyInformation = companyService.createCompany(companyCreate)

        //then
        then(companyRepository).should(times(2)).getMaxCompanyCode()
        then(companyRepository).should().save(companyEntity1)
        then(companyRepository).should().save(companyEntity2)
        assertThat(result).isEqualTo(CompanyInformation.fixture(companyCode = nextCompanyCode2))

    }


    @Test
    fun 회사명_갱신에_성공한다() {
        //given
        val companyUpdate: CompanyUpdate = CompanyUpdate.fixture()
        val companyEntity: CompanyEntity = CompanyEntity.fixture(id = 1L)
        given(companyRepository.findByCompanyCode(companyUpdate.companyCode)).willReturn(companyEntity)
        given(companyRepository.save(companyEntity)).willReturn(companyEntity)

        //when
        val result: CompanyInformation = companyService.updateCompany(companyUpdate)

        //then
        then(companyRepository).should().findByCompanyCode(companyUpdate.companyCode)
        then(companyRepository).should().save(companyEntity)

        assertThat(companyEntity.companyName).isEqualTo(companyUpdate.companyName)
        assertThat(result.companyCode).isEqualTo(companyUpdate.companyCode)
        assertThat(result.companyName).isEqualTo(companyUpdate.companyName)
    }

    @Test
    fun 존재하지_않는_회사코드로_회사명_업데이트시_예외가_발생한다() {
        //given
        val companyUpdate: CompanyUpdate = CompanyUpdate.fixture(companyCode = "WrongCompanyCode")
        val companyEntity: CompanyEntity = CompanyEntity.fixture(id = 1L)
        given(companyRepository.findByCompanyCode(companyUpdate.companyCode)).willReturn(null)

        //when
        val result = assertThrows<ApplicationException> { companyService.updateCompany(companyUpdate)}

        //then
        then(companyRepository).should().findByCompanyCode(companyUpdate.companyCode)

        assertThat(result.getErrorCode).isEqualTo(ErrorCode.COMPANY_NOT_FOUND)
        assertThat(result.getMessage).isEqualTo(ErrorCode.COMPANY_NOT_FOUND.message)
    }


    @Test
    fun 회사_삭제에_성공한다() {
        //given
        val companyCode = "A0000001"
        val companyEntity: CompanyEntity = CompanyEntity.fixture(companyCode = companyCode, id = 1L)
        given(companyRepository.findByCompanyCode(companyCode)).willReturn(companyEntity)
        given(companyRepository.save(companyEntity)).willReturn(companyEntity)

        //when
        val result: CompanyInformation = companyService.deleteCompany(companyCode)

        //then
        then(companyRepository).should().findByCompanyCode(companyCode)
        then(companyRepository).should().save(companyEntity)

        assertThat(companyEntity.deleted).isEqualTo(true)
        assertThat(result.companyCode).isEqualTo(companyEntity.companyCode)
        assertThat(result.companyName).isEqualTo(companyEntity.companyName)
        assertThat(result.deleted).isEqualTo(companyEntity.deleted)
        //TODO deletedDate 비교
    }

    @Test
    fun 존재하지_않는_회사코드로_삭제_시도시_예외가_발생한다() {
        //given
        val wrongCompanyCode = "WrongCompanyCode"
        val companyEntity: CompanyEntity = CompanyEntity.fixture(id = 1L)
        given(companyRepository.findByCompanyCode(wrongCompanyCode)).willReturn(null)

        //when
        val result = assertThrows<ApplicationException> { companyService.deleteCompany(wrongCompanyCode)}

        //then
        then(companyRepository).should().findByCompanyCode(wrongCompanyCode)

        assertThat(result.getErrorCode).isEqualTo(ErrorCode.COMPANY_NOT_FOUND)
        assertThat(result.getMessage).isEqualTo(ErrorCode.COMPANY_NOT_FOUND.message)
    }

}
