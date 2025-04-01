package com.hana.baseproject.api.company.service.impl

import com.hana.baseproject.api.company.controller.reponse.CompanyInformation
import com.hana.baseproject.api.company.controller.request.CompanyCreate
import com.hana.baseproject.api.company.controller.request.CompanyUpdate
import com.hana.baseproject.api.company.domain.CompanyEntity
import com.hana.baseproject.api.company.repository.CompanyRepository
import com.hana.baseproject.api.company.service.CompanyService
import com.hana.baseproject.core.exception.ApplicationException
import com.hana.baseproject.core.exception.constant.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CompanyServiceImpl(
    private val companyRepository: CompanyRepository
) : CompanyService {

    private val log = LoggerFactory.getLogger(this.javaClass)!!



    override fun getCompany(companyCode: String): CompanyInformation {
        val company = companyRepository.findByCompanyCode(companyCode)
            ?: throw ApplicationException(ErrorCode.COMPANY_NOT_FOUND, ErrorCode.COMPANY_NOT_FOUND.message)

        return CompanyInformation(
            companyCode = company.companyCode,
            companyName = company.companyName,
            deleted = company.deleted,
            deletedDate = company.deletedDate,
        )

    }

    @Transactional(readOnly = false)
    override fun createCompany(companyCreate: CompanyCreate): CompanyInformation {
        while (true) {
            val companyCode = createCompanyCode()
            val company = CompanyEntity(companyCode, companyCreate.companyName)

            try {
                companyRepository.save(company)
                return CompanyInformation(
                    companyCode = companyCode,
                    companyName = companyCreate.companyName,
                    deleted = company.deleted,
                    deletedDate = company.deletedDate,
                )
            } catch (ex: DataIntegrityViolationException) {
                log.info("duplicate companyCode [companyCode={}, companyName={}]", companyCode, companyCreate.companyName)
            }
        }
    }

    @Transactional(readOnly = false)
    override fun updateCompany(companyUpdate: CompanyUpdate): CompanyInformation {
        val findCompany: CompanyEntity = companyRepository.findByCompanyCode(companyUpdate.companyCode) ?: throw ApplicationException(ErrorCode.COMPANY_NOT_FOUND, ErrorCode.COMPANY_NOT_FOUND.message)

        findCompany.updateCompanyName(companyUpdate.companyName)

        companyRepository.save(findCompany)

        return CompanyInformation(
            companyCode = findCompany.companyCode,
            companyName = findCompany.companyName,
            deleted = findCompany.deleted,
            deletedDate = findCompany.deletedDate,
        )
    }

    @Transactional(readOnly = false)
    override fun deleteCompany(companyCode: String): CompanyInformation {
        val findCompany: CompanyEntity = companyRepository.findByCompanyCode(companyCode) ?: throw ApplicationException(ErrorCode.COMPANY_NOT_FOUND, ErrorCode.COMPANY_NOT_FOUND.message)
        findCompany.delete()

        companyRepository.save(findCompany)

        return CompanyInformation(
            companyCode = findCompany.companyCode,
            companyName = findCompany.companyName,
            deleted = findCompany.deleted,
            deletedDate = findCompany.deletedDate,
        )
    }


    // 코드생성로직
    // 영문대문자 + 7자리 숫자로 구성된 8자리 코드
    // 추후 영문자를 통해서 회사별 타입 구분 가능
    // 현재 A0000001 로 시작해서 1씩 증가
    internal fun createCompanyCode(): String {
        val maxCompanyCode: String = companyRepository.getMaxCompanyCode() ?: "A0000000"

        val prefix: String = maxCompanyCode.substring(0, 1) // A,B,C,D
        val numberPart: String = maxCompanyCode.substring(1) // 0~9999999

        val nextNumber: Int = numberPart.toInt() + 1
        val formattedNumber = nextNumber.toString().padStart(7, '0')

        val newCompanyCode: String = prefix + formattedNumber

        return newCompanyCode

    }
}

