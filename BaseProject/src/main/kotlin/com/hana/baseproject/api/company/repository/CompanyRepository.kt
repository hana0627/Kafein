package com.hana.baseproject.api.company.repository

import com.hana.baseproject.api.company.domain.CompanyEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CompanyRepository : JpaRepository<CompanyEntity, Long> {
    fun findByCompanyCode(companyCode: String): CompanyEntity?
}