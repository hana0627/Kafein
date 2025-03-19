package com.hana.baseproject.api.company.controller

import com.hana.baseproject.api.company.controller.request.CompanyCreate
import com.hana.baseproject.api.company.controller.request.CompanyUpdate
import com.hana.baseproject.api.company.domain.CompanyEntity
import com.hana.baseproject.api.company.service.impl.CompanyServiceImpl
import com.hana.baseproject.core.response.APIResponse
import org.springframework.web.bind.annotation.*

@RestController
class CompanyController (
    private val companyService: CompanyServiceImpl
){

    @GetMapping("/v1/{companyCode}/company")
    fun showCompany(@PathVariable("companyCode") companyCode: String): APIResponse<Any> {
        // 회사 정보를 표출한다.
        val result: CompanyEntity = companyService.getCompany(companyCode)
        return APIResponse.success(result)
    }

    @PostMapping("/v2/company")
    fun createCompany(@RequestBody companyCreate: CompanyCreate): APIResponse<String> {
        // 회사를 등록한다.
        val result = companyService.createCompany(companyCreate)
        return APIResponse.success(result)
    }

    @PatchMapping("/v2/company")
    fun updateCompany(@RequestBody companyUpdate: CompanyUpdate): APIResponse<String> {
        // 회사정보를 갱신한다.
        val result = companyService.updateCompany(companyUpdate)
        return APIResponse.success(result);
    }

    @DeleteMapping("/v2/{companyCode}/company")
    fun deleteCompany(@PathVariable("companyCode") companyCode: String): APIResponse<String> {
        // 회사를 삭제한다.
        val result = companyService.deleteCompany(companyCode)
        return APIResponse.success(result);
    }
}
