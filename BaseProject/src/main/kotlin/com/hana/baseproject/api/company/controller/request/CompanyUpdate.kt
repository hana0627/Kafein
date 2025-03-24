package com.hana.baseproject.api.company.controller.request

data class CompanyUpdate (
    val companyCode: String,
    val companyName: String,
){

    companion object {
        fun fixture(
            companyCode: String = "A0000001",
            companyName: String = "신세경다방",
        ) : CompanyUpdate {
            return CompanyUpdate(
                companyCode = companyCode,
                companyName = companyName
            )
        }
    }
}
