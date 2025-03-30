package com.hana.baseproject.api.product.controller.request

import com.hana.baseproject.api.product.domain.constant.ProductLevel

data class ProductUpdate(
    val productCode: String,
    val productName: String,
    val price: Int,
    val stock: Int,
) {


    companion object {
        fun fixture(
            productCode: String = "PD000001",
            productName: String = "아이스아메리카노",
            price: Int = 3000,
            stock: Int = 9999,
        ): ProductUpdate {
            return ProductUpdate(
                productCode = productCode,
                productName = productName,
                price = price,
                stock = stock,
            )
        }
    }
}

