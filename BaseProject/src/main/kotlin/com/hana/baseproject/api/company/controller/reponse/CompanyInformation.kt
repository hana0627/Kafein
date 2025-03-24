package com.hana.baseproject.api.company.controller.reponse

import java.time.LocalDateTime

data class CompanyInformation (
    val companyCode: String,
    val companyName: String,
    val deleted: Boolean,
    val deletedDate: LocalDateTime? = null,
){

    companion object {
        fun fixture(
            companyCode: String = "A0000001",
            companyName: String = "하나다방",
            deleted: Boolean = false,
            deletedDate: LocalDateTime? = null,
        ) : CompanyInformation {
            return CompanyInformation(
                companyCode = companyCode,
                companyName = companyName,
                deleted = deleted,
                deletedDate = deletedDate,
            )
        }
    }
}
