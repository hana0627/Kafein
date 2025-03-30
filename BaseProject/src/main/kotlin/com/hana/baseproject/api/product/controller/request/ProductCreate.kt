package com.hana.baseproject.api.product.controller.request

import com.hana.baseproject.api.product.domain.constant.ProductLevel

data class ProductCreate(
    val productName: String,
    val price: Int,
    val stock: Int,

    val productLevel: ProductLevel,
    val categoryCode: String,

    val companyCode: String,
) {


    companion object {
        fun fixture(
            productName: String = "아이스아메리카노",
            price: Int = 3000,
            stock: Int = 9999,
            productLevel: ProductLevel = ProductLevel.SUB,
            categoryCode: String = "AA010101",
            companyCode: String = "A0000001",
        ): ProductCreate {
            return ProductCreate(
                productName = productName,
                price = price,
                stock = stock,
                productLevel = productLevel,
                categoryCode = categoryCode,
                companyCode = companyCode,
            )
        }
    }
}

