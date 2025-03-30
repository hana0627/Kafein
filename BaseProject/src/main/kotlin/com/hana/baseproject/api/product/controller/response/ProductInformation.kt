package com.hana.baseproject.api.product.controller.response

import com.hana.baseproject.api.product.domain.constant.ProductLevel
import java.time.LocalDateTime

data class ProductInformation(
    val productCode: String,
    val productName: String,
    val price: Int,
    val stock: Int,

    val productLevel: ProductLevel,
    val categoryCode: String,
    val categoryName: String,

    val companyCode: String,
    var companyName: String,

    val deleted: Boolean = false,
    val deletedDate: LocalDateTime? = null,
) {


    companion object {
        fun fixture(
            productCode: String = "PD000001",
            productName: String = "아이스아메리카노",
            price: Int = 3000,
            stock: Int = 9999,
            productLevel: ProductLevel = ProductLevel.SUB,
            categoryCode: String = "AA010101",
            categoryName: String = "아이스아메리카노",
            companyCode: String = "A0000001",
            companyName: String = "하나다방",
            deleted: Boolean = false,
            deletedDate: LocalDateTime? = null,
        ): ProductInformation {
            return ProductInformation(
                productCode = productCode,
                productName = productName,
                price = price,
                stock = stock,
                productLevel = productLevel,
                categoryCode = categoryCode,
                categoryName = categoryName,
                companyCode = companyCode,
                companyName = companyName,
                deleted = deleted,
                deletedDate = deletedDate,
            )
        }
    }
}

