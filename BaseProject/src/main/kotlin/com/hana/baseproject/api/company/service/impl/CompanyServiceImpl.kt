package com.hana.baseproject.api.company.service.impl

import com.hana.baseproject.api.company.controller.request.CompanyCreate
import com.hana.baseproject.api.company.controller.request.CompanyUpdate
import com.hana.baseproject.api.company.domain.CompanyEntity
import com.hana.baseproject.api.company.repository.CompanyRepository
import com.hana.baseproject.api.company.service.CompanyService
import com.hana.baseproject.core.exception.ApplicationException
import com.hana.baseproject.core.exception.constant.ErrorCode
import org.springframework.stereotype.Service

@Service
class CompanyServiceImpl(
    val companyRepository: CompanyRepository
) : CompanyService {
    override fun getCompany(companyCode: String): CompanyEntity {
        return companyRepository.findByCompanyCode(companyCode) ?: throw ApplicationException(ErrorCode.SAMPLE_ERROR_CODE, ErrorCode.SAMPLE_ERROR_CODE.message)
    }

    override fun createCompany(companyCreate: CompanyCreate): String {
        return "이름생성"
    }

    override fun updateCompany(companyUpdate: CompanyUpdate): String {
        return "갱신완료"
    }

    override fun deleteCompany(companyCode: String): String {
        return "삭제완료"
    }
}
