package com.hana.baseproject.api.company.controller

import com.hana.baseproject.core.response.APIResponse
import org.springframework.web.bind.annotation.*

@RestController
class CompanyController {

    @GetMapping("/v1/{companyCode}/company")
    fun showCompany(@PathVariable("companyCode") companyCode: String): APIResponse<String> {
        // 회사 정보를 표출한다.
        return APIResponse.success("");
    }

    @PostMapping("/v1/company")
    fun createCompany(): APIResponse<String> {
        // 회사를 등록한다.
        return APIResponse.success("");
    }

    @PatchMapping("/v2/company")
    fun updateCompany(): APIResponse<String> {
        // 회사정보를 갱신한다.
        return APIResponse.success("");
    }

    @DeleteMapping("/v2/{companyCode}/company")
    fun deleteCompany(@PathVariable("companyCode") companyCode: String): APIResponse<String> {
        // 회사를 삭제한다.
        return APIResponse.success("");
    }
}
