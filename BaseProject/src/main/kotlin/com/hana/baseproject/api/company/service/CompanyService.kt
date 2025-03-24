package com.hana.baseproject.api.company.service

import com.hana.baseproject.api.company.controller.reponse.CompanyInformation
import com.hana.baseproject.api.company.controller.request.CompanyCreate
import com.hana.baseproject.api.company.controller.request.CompanyUpdate

interface CompanyService {
    fun getCompany(companyCode: String): CompanyInformation
    fun createCompany(companyCreate: CompanyCreate): CompanyInformation
    fun updateCompany(companyUpdate: CompanyUpdate): CompanyInformation
    fun deleteCompany(companyCode: String): CompanyInformation
}
