package com.hana.baseproject.api.company.controller

import com.hana.baseproject.api.company.controller.reponse.CompanyInformation
import com.hana.baseproject.api.company.controller.request.CompanyCreate
import com.hana.baseproject.api.company.controller.request.CompanyUpdate
import com.hana.baseproject.api.company.domain.CompanyEntity
import com.hana.baseproject.api.company.service.CompanyService
import com.hana.baseproject.api.company.service.impl.CompanyServiceImpl
import com.hana.baseproject.core.response.APIResponse
import org.springframework.web.bind.annotation.*

@RestController
class CompanyController (
    private val companyService: CompanyService
){

    @GetMapping("/v1/{companyCode}/company")
    fun showCompany(@PathVariable("companyCode") companyCode: String): APIResponse<CompanyInformation> {
        // 회사 정보를 표출한다.
        val result: CompanyInformation = companyService.getCompany(companyCode)
        return APIResponse.success(result)
    }

    @PostMapping("/v2/company")
    fun createCompany(@RequestBody companyCreate: CompanyCreate): APIResponse<CompanyInformation> {
        // 회사를 등록한다.
        val result: CompanyInformation = companyService.createCompany(companyCreate)
        return APIResponse.success(result)
    }

    @PatchMapping("/v2/company")
    fun updateCompany(@RequestBody companyUpdate: CompanyUpdate): APIResponse<CompanyInformation> {
        // 회사정보를 갱신한다.
        val result: CompanyInformation = companyService.updateCompany(companyUpdate)
        return APIResponse.success(result);
    }

    @DeleteMapping("/v2/{companyCode}/company")
    fun deleteCompany(@PathVariable("companyCode") companyCode: String): APIResponse<CompanyInformation> {
        // 회사를 삭제한다.
        val result: CompanyInformation = companyService.deleteCompany(companyCode)
        return APIResponse.success(result);
    }
}
