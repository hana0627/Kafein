package com.hana.baseproject.api.product.controller.request

import com.hana.baseproject.api.product.domain.constant.ProductLevel

data class ProductCategoryUpdate(
    val productLevel: ProductLevel,
    val categoryCode: String,
    val upCategoryCode: String,
    val categoryName: String,
) {

    companion object {
        fun fixture(
            productLevel: ProductLevel = ProductLevel.MEGA,
            categoryCode: String = "AA01",
            upCategoryCode: String = "",
            categoryName: String = "커피",
        ): ProductCategoryUpdate {
            return ProductCategoryUpdate(
                productLevel = productLevel,
                categoryCode = categoryCode,
                upCategoryCode = upCategoryCode,
                categoryName = categoryName,
            )
        }
    }
}
