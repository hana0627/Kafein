package com.hana.baseproject.api.company.repository

import com.hana.baseproject.api.company.domain.CompanyEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CompanyRepository : JpaRepository<CompanyEntity, Long> {
    fun findByCompanyCode(companyCode: String): CompanyEntity?

    @Query("SELECT MAX(c.companyCode) FROM CompanyEntity c")
    fun getMaxCompanyCode(): String?

}
