package com.hana.baseproject.api.company.controller.request

data class CompanyCreate (
    val companyName: String,
){

    companion object {
        fun fixture(
            companyName: String = "하나다방",
        ) : CompanyCreate {
            return CompanyCreate(companyName = companyName)
        }
    }
}
