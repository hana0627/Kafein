package com.hana.baseproject.api.company.service

import com.hana.baseproject.api.company.controller.request.CompanyCreate
import com.hana.baseproject.api.company.controller.request.CompanyUpdate
import com.hana.baseproject.api.company.domain.CompanyEntity

interface CompanyService {
    fun getCompany(companyCode: String): CompanyEntity
    fun createCompany(companyCreate: CompanyCreate): String
    fun updateCompany(companyUpdate: CompanyUpdate): String
    fun deleteCompany(companyCode: String): String
}
